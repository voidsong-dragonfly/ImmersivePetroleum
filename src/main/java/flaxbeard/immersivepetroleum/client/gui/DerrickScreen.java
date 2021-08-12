package flaxbeard.immersivepetroleum.client.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import blusunrize.immersiveengineering.client.ClientUtils;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DerrickScreen extends ContainerScreen<DerrickContainer>{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/derrick.png");
	
	DerrickTileEntity tile;
	
	public DerrickScreen(DerrickContainer inventorySlotsIn, PlayerInventory inv, ITextComponent title){
		super(inventorySlotsIn, inv, title);
		this.tile = container.tile;
		
		// TODO GUI may either get bigger or smaller
		this.xSize = 200;
		this.ySize = 187;
	}
	
	@Override
	public void render(MatrixStack matrix, int mx, int my, float partialTicks){
		this.playerInventoryTitleY = this.ySize - 40;
		this.renderBackground(matrix);
		super.render(matrix, mx, my, partialTicks);
		this.renderHoveredTooltip(matrix, mx, my);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int x, int y){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientUtils.bindTexture(GUI_TEXTURE);
		this.blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}
