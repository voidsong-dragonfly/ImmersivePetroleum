package flaxbeard.immersivepetroleum.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.gui.DistillationTowerContainer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.fluids.FluidStack;

public class DistillationTowerScreen extends IEContainerScreen<DistillationTowerContainer>{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/distillation.png");
	
	DistillationTowerTileEntity tile;
	
	public DistillationTowerScreen(DistillationTowerContainer container, Inventory playerInventory, Component title){
		super(container, playerInventory, title);
		this.tile = container.tile;
	}
	
	@Override
	public void render(PoseStack matrix, int mx, int my, float partialTicks){
		this.renderBackground(matrix);
		super.render(matrix, mx, my, partialTicks);
		this.renderTooltip(matrix, mx, my);
		
		List<Component> tooltip = new ArrayList<>();
		GuiHelper.handleGuiTank(matrix, tile.tanks[0], leftPos + 62, topPos + 21, 16, 47, 177, 31, 20, 51, mx, my, GUI_TEXTURE, tooltip);
		
		if(mx >= leftPos + 112 && mx <= leftPos + 112 + 16 && my >= topPos + 21 && my <= topPos + 21 + 47){
			float capacity = tile.tanks[1].getCapacity();
			int yy = topPos + 21 + 47;
			if(tile.tanks[1].getFluidTypes() == 0){
				tooltip.add(new TranslatableComponent("gui.immersiveengineering.empty"));
			}else{
				for(int i = tile.tanks[1].getFluidTypes() - 1;i >= 0;i--){
					FluidStack fs = tile.tanks[1].fluids.get(i);
					if(fs != null && fs.getFluid() != null){
						int fluidHeight = (int) (47 * (fs.getAmount() / capacity));
						yy -= fluidHeight;
						if(my >= yy && my < yy + fluidHeight)
							GuiHelper.addFluidTooltip(fs, tooltip, (int) capacity);
					}
				}
			}
		}
		
		if(mx > leftPos + 157 && mx < leftPos + 164 && my > topPos + 21 && my < topPos + 67)
			tooltip.add(new TextComponent(tile.getEnergyStored(null) + "/" + tile.getMaxEnergyStored(null) + " IF"));
		
		if(!tooltip.isEmpty()){
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, font);
		}
	}
	
	@Override
	protected void renderBg(PoseStack matrix, float f, int mx, int my){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientUtils.bindTexture(GUI_TEXTURE);
		this.blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		int stored = (int) (46 * (tile.getEnergyStored(null) / (float) tile.getMaxEnergyStored(null)));
		fillGradient(matrix, leftPos + 158, topPos + 22 + (46 - stored), leftPos + 165, topPos + 68, 0xffb51500, 0xff600b00);
		
		GuiHelper.handleGuiTank(matrix, tile.tanks[0], leftPos + 62, topPos + 21, 16, 47, 177, 31, 20, 51, mx, my, GUI_TEXTURE, null);
		
		MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		float capacity = tile.tanks[1].getCapacity();
		int yy = topPos + 21 + 47;
		for(int i = tile.tanks[1].getFluidTypes() - 1;i >= 0;i--){
			FluidStack fs = tile.tanks[1].fluids.get(i);
			if(fs != null && fs.getFluid() != null){
				int fluidHeight = (int) (47 * (fs.getAmount() / capacity));
				yy -= fluidHeight;
				GuiHelper.drawRepeatedFluidSpriteGui(buffers, matrix, fs, leftPos + 112, yy, 16, fluidHeight);
			}
		}
		buffers.endBatch();
	}
}
