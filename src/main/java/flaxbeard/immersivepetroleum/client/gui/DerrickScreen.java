package flaxbeard.immersivepetroleum.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import flaxbeard.immersivepetroleum.client.MCUtil;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DerrickScreen extends AbstractContainerScreen<DerrickContainer>{
	static final ResourceLocation GUI_TEXTURE = ResourceUtils.ip("textures/gui/derrick.png");
	
	Button cfgButton;
	
	final DerrickTileEntity tile;
	private List<InfoArea> areas;
	
	public DerrickScreen(DerrickContainer inventorySlotsIn, Inventory inv, Component title){
		super(inventorySlotsIn, inv, title);
		this.tile = menu.tile;
		
		// TODO GUI may either get bigger or smaller as i figure this out
		this.imageWidth = 200;
		this.imageHeight = 164;
	}
	
	@Override
	protected void init(){
		this.leftPos = (this.width - this.imageWidth) / 2;
		this.topPos = (this.height - this.imageHeight) / 2;
		
		this.cfgButton = new Button(this.leftPos + 125, this.topPos + 52, 50, 20, new TextComponent("Config"), button -> this.minecraft.setScreen(new DerrickSettingsScreen(this)), (button, matrix, mx, my) -> {
			if(!button.active){
				renderTooltip(matrix, List.of(new TextComponent("Set in Stone.")), Optional.empty(), mx, my);
			}
		});
		addRenderableWidget(this.cfgButton);
		this.areas = List.of(
				new FluidInfoArea(tile.tank, new Rect2i(leftPos + 11, topPos + 16, 16, 47), 200, 0, 20, 51, GUI_TEXTURE),
				new EnergyInfoArea(leftPos + 185, topPos + 19, tile.energyStorage)
		);
	}
	
	@Override
	public void render(@Nonnull PoseStack matrix, int mx, int my, float partialTicks){
		this.inventoryLabelY = this.imageHeight - 40;
		this.renderBackground(matrix);
		super.render(matrix, mx, my, partialTicks);
		this.renderTooltip(matrix, mx, my);
		
		List<Component> tooltip = new ArrayList<>();
		
		for(InfoArea area:areas){
			area.fillTooltip(mx, my, tooltip);
		}
		
		if(!tooltip.isEmpty()){
			renderTooltip(matrix, tooltip, Optional.empty(), mx, my);
		}
	}
	
	// TODO ! All of these below need translation stuff!
	@Override
	protected void renderLabels(@Nonnull PoseStack matrix, int x, int y){
		//super.drawGuiContainerForegroundLayer(matrixStack, x, y);
		
		if(this.tile.getBlockPos().getY() <= 62){
			// TODO Split with "<br>" using the translation?
			/*
			 * Basicly get the translation, look for "<br>" and split it into an array.
			 * Go through the array one by one and do what is already been done below, just not hardcoded.
			 * Also limit it to 4 lines, every thing that uses more than 4 lines is cut off.
			 * 
			 * (Maybe even cut off at a certain length, to avoid overly loooong text breaching the boundaries?)
			 * 
			 * This should make multi-line stuff like the one below far easier to translate
			 * and cuts down the number of language file entries
			 */
			drawInfoTextCentered(matrix, new TextComponent("! WARNING !"), 0, 0xEF0000);
			drawInfoTextCentered(matrix, new TextComponent("Derrick is being flooded"), 2, 0xEF0000);
			drawInfoTextCentered(matrix, new TextComponent("below the water table"), 3, 0xEF0000);
			return;
		}
		
		WellTileEntity well = this.tile.getOrCreateWell(false);
		if(well != null){
			if(this.cfgButton.active && well.wellPipeLength > 0){
				this.cfgButton.active = false;
			}
			
			{
				// Debug Stuff (Renders over the GUI)
				int realPipes = Math.max(0, (this.tile.getBlockPos().getY() - well.getBlockPos().getY() - 1) - well.wellPipeLength);
				String str = String.format(Locale.ROOT, "R.Pipes: %d (%dmB)", realPipes, realPipes * 125);
				drawInfoText(matrix, new TextComponent("Pipe, Timer: " + well.pipes + ", " + this.tile.timer + "t").withStyle(ChatFormatting.DARK_GRAY), -5);
				drawInfoText(matrix, new TextComponent(str).withStyle(ChatFormatting.DARK_GRAY), -4);
			}
			
			// Possible display prototypes
			
			if(this.tile.isRSDisabled()){
				drawInfoTextCentered(matrix, new TextComponent("Disabled via Controller"), 0, 0xEF0000);
				return;
			}
			
			if(well.wellPipeLength < well.getMaxPipeLength()){
				if(this.tile.drilling){
					String str = String.format(Locale.ROOT, "(%d%%)", (int) (100 * well.wellPipeLength / (float) well.getMaxPipeLength()));
					drawInfoText(matrix, new TextComponent("Drilling... " + str), 0);
					return;
				}else if(well.pipes <= 0 && !this.menu.getSlot(0).hasItem()){
					drawInfoTextCentered(matrix, new TextComponent("Ran out of Pipes."), 3, 0xEF0000);
					return;
				}
				
				if(this.tile.tank.isEmpty()){
					int realPipeLength = (this.tile.getBlockPos().getY() - 1) - well.getBlockPos().getY();
					int concreteNeeded = (DerrickTileEntity.CONCRETE.getAmount() * (realPipeLength - well.wellPipeLength));
					if(concreteNeeded > 0){
						drawInfoText(matrix, new TextComponent("Missing " + Utils.fDecimal(concreteNeeded) + "mB of"), 0, 0xEF0000);
						drawInfoText(matrix, DerrickTileEntity.CONCRETE.getDisplayName(), 1, 0xEF0000);
						return;
					}
					
					int waterNeeded = DerrickTileEntity.WATER.getAmount() * (well.getMaxPipeLength() - well.wellPipeLength);
					if(waterNeeded > 0){
						drawInfoText(matrix, new TextComponent("Missing " + Utils.fDecimal(waterNeeded) + "mB of"), 0, 0xEF0000);
						drawInfoText(matrix, DerrickTileEntity.WATER.getDisplayName(), 1, 0xEF0000);
						return;
					}
				}
			}else{
				if(this.tile.spilling){
					drawInfoTextCentered(matrix, new TextComponent("! WARNING !"), 0, 0xEF0000);
					drawInfoTextCentered(matrix, new TextComponent("SAFETYVALVE OPEN"), 2, 0xEF0000);
					drawInfoTextCentered(matrix, new TextComponent("PRESSURE TOO HIGH"), 3, 0xEF0000);
				}else{
					drawInfoTextCentered(matrix, new TextComponent("Drilling Completed"), 0);
					drawInfoTextCentered(matrix, new TextComponent("Have a nice day :3"), 3);
				}
			}
		}
	}
	
	private void drawInfoText(PoseStack matrix, Component text, int line){
		drawInfoText(matrix, text, line, Lib.colour_nixieTubeText);
	}
	
	private void drawInfoText(PoseStack matrix, Component text, int line, int color){
		this.font.draw(matrix, text, 60, 10 + (9 * line), color);
	}
	
	private void drawInfoTextCentered(PoseStack matrix, Component text, int line){
		drawInfoTextCentered(matrix, text, line, Lib.colour_nixieTubeText);
	}
	
	private void drawInfoTextCentered(PoseStack matrix, Component text, int line, int color){
		int strWidth = this.font.width(text.getString());
		this.font.draw(matrix, text, 118.5F - (strWidth / 2F), 10 + (9 * line), color);
	}
	
	@Override
	protected void renderBg(@Nonnull PoseStack matrix, float partialTicks, int mx, int my){
		MCUtil.bindTexture(GUI_TEXTURE);
		this.blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		for(InfoArea area:areas){
			area.draw(matrix);
		}
	}
}
