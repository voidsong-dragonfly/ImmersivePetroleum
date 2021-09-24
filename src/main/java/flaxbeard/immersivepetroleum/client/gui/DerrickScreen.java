package flaxbeard.immersivepetroleum.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DerrickScreen extends ContainerScreen<DerrickContainer>{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/derrick.png");
	
	final DerrickTileEntity tile;
	
	public DerrickScreen(DerrickContainer inventorySlotsIn, PlayerInventory inv, ITextComponent title){
		super(inventorySlotsIn, inv, title);
		this.tile = container.tile;
		
		// TODO GUI may either get bigger or smaller as i figure this out
		this.xSize = 200;
		this.ySize = 159;
	}
	
	@Override
	protected void init(){
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		addButton(new Button(this.guiLeft + 125, this.guiTop + 52, 50, 20, new StringTextComponent("Config"), button -> {
			this.minecraft.displayGuiScreen(new DerrickSettingsScreen(this));
		}, (button, matrix, mx, my) -> {
			GuiUtils.drawHoveringText(matrix, Arrays.asList(new StringTextComponent("Configuration")), mx, my, width, height, -1, font);
		}));
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
			tooltip.add(new StringTextComponent(this.tile.energyStorage.getEnergyStored() + "/" + this.tile.energyStorage.getMaxEnergyStored() + " RF"));
		}
		
		if(!tooltip.isEmpty()){
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, font);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y){
		//super.drawGuiContainerForegroundLayer(matrixStack, x, y);
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
