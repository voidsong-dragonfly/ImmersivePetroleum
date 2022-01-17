package flaxbeard.immersivepetroleum.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DerrickScreen extends ContainerScreen<DerrickContainer>{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/derrick.png");
	
	Button cfgButton;
	
	final DerrickTileEntity tile;
	
	public DerrickScreen(DerrickContainer inventorySlotsIn, PlayerInventory inv, ITextComponent title){
		super(inventorySlotsIn, inv, title);
		this.tile = container.tile;
		
		// TODO GUI may either get bigger or smaller as i figure this out
		this.xSize = 200;
		this.ySize = 164;
	}
	
	@Override
	protected void init(){
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		this.cfgButton = new Button(this.guiLeft + 125, this.guiTop + 52, 50, 20, new StringTextComponent("Config"), button -> {
			this.minecraft.displayGuiScreen(new DerrickSettingsScreen(this));
		}, (button, matrix, mx, my) -> {
			if(!button.active){
				GuiUtils.drawHoveringText(matrix, Arrays.asList(new StringTextComponent("Set in Stone.")), mx, my, width, height, -1, font);
			}
		});
		addButton(this.cfgButton);
	}
	
	@Override
	public void render(MatrixStack matrix, int mx, int my, float partialTicks){
		this.playerInventoryTitleY = this.ySize - 40;
		this.renderBackground(matrix);
		super.render(matrix, mx, my, partialTicks);
		this.renderHoveredTooltip(matrix, mx, my);
		
		List<ITextComponent> tooltip = new ArrayList<>();
		GuiHelper.handleGuiTank(matrix, this.tile.waterTank, guiLeft + 11, guiTop + 16, 16, 47, 0, 0, 0, 0, mx, my, GUI_TEXTURE, tooltip);
		
		// Power Stored
		if(mx > guiLeft + 184 && mx < guiLeft + 192 && my > guiTop + 18 && my < guiTop + 65){
			tooltip.add(new StringTextComponent(this.tile.energyStorage.getEnergyStored() + "/" + this.tile.energyStorage.getMaxEnergyStored() + " IF"));
		}
		
		if(!tooltip.isEmpty()){
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, font);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int x, int y){
		//super.drawGuiContainerForegroundLayer(matrixStack, x, y);
		
		World world = this.tile.getWorldNonnull();
		WellTileEntity well = null;
		
		TileEntity te = world.getTileEntity(this.tile.getPos().down());
		if(te instanceof WellTileEntity){
			well = (WellTileEntity) te;
		}
		
		if(well != null){
			if(this.cfgButton.active && well.pipeLength > 0){
				this.cfgButton.active = false;
			}
			
			// Possible display prototypes
			
			if(well.pipeLength < well.pipeMaxLength()){
				String str = String.format(Locale.ENGLISH, "%d%%", (int) (100 * well.pipeLength / (float) well.pipeMaxLength()));
				drawInfoText(matrix, new StringTextComponent("Drilling... " + str), 0);
				drawInfoText(matrix, new StringTextComponent("Length: " + well.pipeLength + "/" + well.pipeMaxLength() + "m"), 1);
				drawInfoText(matrix, new StringTextComponent("§8Pipe, Timer: " + well.pipe + ", " + this.tile.timer + "t"), 2);
				//drawInfoText(matrix, new StringTextComponent(""), 3);
			}else{
				boolean debug = false;
				if(this.tile.spilling || debug){
					drawInfoText(matrix, new StringTextComponent("§l !  !  !  !  !  !  !  !  !"), 0, 0xEF0000);
					drawInfoText(matrix, new StringTextComponent("§lSAFETYVALVE OPEN"), 1, 0xEF0000);
					drawInfoText(matrix, new StringTextComponent("§lPRESSURE TOO HIGH"), 2, 0xEF0000);
					drawInfoText(matrix, new StringTextComponent("§l !  !  !  !  !  !  !  !  !"), 3, 0xEF0000);
				}else{
					drawInfoText(matrix, new StringTextComponent("   Drilling Completed."), 1);
					drawInfoText(matrix, new StringTextComponent("   Have a nice day :3"), 3);
				}
			}
		}
	}
	
	private void drawInfoText(MatrixStack matrix, ITextComponent text, int line){
		drawInfoText(matrix, text, line, Lib.colour_nixieTubeText);
	}
	
	private void drawInfoText(MatrixStack matrix, ITextComponent text, int line, int color){
		this.font.drawText(matrix, text, 60, 8 + (9 * line), color);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mx, int my){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientUtils.bindTexture(GUI_TEXTURE);
		this.blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);
		
		GuiHelper.handleGuiTank(matrix, this.tile.waterTank, guiLeft + 11, guiTop + 16, 16, 47, 200, 0, 20, 51, mx, my, GUI_TEXTURE, null);
		
		int x = guiLeft + 185;
		int y = guiTop + 44;
		int stored = (int) (this.tile.energyStorage.getEnergyStored() / (float) this.tile.energyStorage.getMaxEnergyStored() * 46);
		fillGradient(matrix, x, y + 21 - stored, x + 7, y + 21, 0xffb51500, 0xff600b00);
	}
}
