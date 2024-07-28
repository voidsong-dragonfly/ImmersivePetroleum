package flaxbeard.immersivepetroleum.client.render;

import javax.annotation.Nonnull;

import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity.Port;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity.PortState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class OilTankRenderer implements BlockEntityRenderer<OilTankTileEntity>{
	@Override
	public boolean shouldRenderOffScreen(@Nonnull OilTankTileEntity te){
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(OilTankTileEntity te, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		if(!te.formed || te.isDummy() || !te.getLevelNonnull().hasChunkAt(te.getBlockPos()))
			return;
		
		combinedOverlay = OverlayTexture.NO_OVERLAY;
		
		matrix.pushPose();
		{
			switch(te.getFacing()){
				case EAST -> {
					matrix.mulPose(new Quaternionf().rotateY(3*Mth.HALF_PI));
					matrix.translate(0, 0, -1);
				}
				case SOUTH -> {
					matrix.mulPose(new Quaternionf().rotateY(Mth.PI));
					matrix.translate(-1, 0, -1);
				}
				case WEST -> {
					matrix.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
					matrix.translate(-1, 0, 0);
				}
				default -> {
				}
			}
			
			// Tank Display
			matrix.pushPose();
			{
				matrix.translate(1, 2, 2.995F);
				
				// Background
				Matrix4f mat = matrix.last().pose();
				VertexConsumer builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_POSITION_COLOR);
				builder.vertex(mat, 1.5F, -0.5F, 0.0F).color(34, 34, 34, 255).endVertex();
				builder.vertex(mat, 1.5F, 1F, 0.0F).color(34, 34, 34, 255).endVertex();
				builder.vertex(mat, 0F, 1F, 0.0F).color(34, 34, 34, 255).endVertex();
				builder.vertex(mat, 0F, -0.5F, 0.0F).color(34, 34, 34, 255).endVertex();
				
				OilTankTileEntity master = te.master();
				if(master != null){
					FluidStack fs = master.tank.getFluid();
					if(!fs.isEmpty()){
						matrix.pushPose();
						{
							matrix.translate(0.25, 0.875, 0.0025F);
							matrix.scale(0.0625F, -0.0625F, 0.0625F);
							
							float h = fs.getAmount() / (float) master.tank.getCapacity();
							GuiHelper.drawRepeatedFluidSprite(buffer.getBuffer(RenderType.solid()), matrix, fs, 0, 0 + (1 - h) * 16, 16, h * 16);
						}
						matrix.popPose();
					}
				}
			}
			matrix.popPose();
			
			matrix.pushPose();
			{
				// Dynamic Fluid IO Ports
				if(te.getIsMirrored()){
					OilTankTileEntity master = te.master();
					if(master != null){
						for(Port port:Port.DYNAMIC_PORTS){
							matrix.pushPose();
							{
								BlockPos p = port.posInMultiblock.subtract(te.posInMultiblock);
								matrix.mulPose(new Quaternionf().rotateY(Mth.PI));
								matrix.translate(p.getX() - 1, p.getY(), -p.getZ() - 1);
								quad(matrix, buffer, master.getPortStateFor(port), port.posInMultiblock.getX() == 4, combinedLight, combinedOverlay);
							}
							matrix.popPose();
						}
					}
				}else{
					OilTankTileEntity master = te.master();
					if(master != null){
						for(Port port:Port.DYNAMIC_PORTS){
							matrix.pushPose();
							{
								BlockPos p = port.posInMultiblock.subtract(te.posInMultiblock);
								matrix.translate(p.getX(), p.getY(), p.getZ());
								quad(matrix, buffer, master.getPortStateFor(port), port.posInMultiblock.getX() == 4, combinedLight, combinedOverlay);
							}
							matrix.popPose();
						}
					}
				}
			}
			matrix.popPose();
		}
		matrix.popPose();
	}
	
	public void quad(PoseStack matrix, MultiBufferSource buffer, PortState portState, boolean flip, int combinedLight, int combinedOverlay){
		Matrix4f mat = matrix.last().pose();
		VertexConsumer builder = buffer.getBuffer(IPRenderTypes.OIL_TANK);
		
		boolean input = portState == PortState.INPUT;
		float u0 = input ? 0.0F : 0.1F, v0 = 0.5F;
		float u1 = u0 + 0.1F, v1 = v0 + 0.1F;
		if(flip){
			builder.vertex(mat, 1.001F, 0F, 0F).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, 1.001F, 1F, 0F).color(1F, 1F, 1F, 1F).uv(u1, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, 1.001F, 1F, 1F).color(1F, 1F, 1F, 1F).uv(u0, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, 1.001F, 0F, 1F).color(1F, 1F, 1F, 1F).uv(u0, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
		}else{
			builder.vertex(mat, -0.001F, 0F, 0F).color(1F, 1F, 1F, 1F).uv(u0, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, -0.001F, 0F, 1F).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, -0.001F, 1F, 1F).color(1F, 1F, 1F, 1F).uv(u1, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, -0.001F, 1F, 0F).color(1F, 1F, 1F, 1F).uv(u0, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
		}
	}
}
