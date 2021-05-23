package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity.DynPortState;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity.Port;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class OilTankRenderer extends TileEntityRenderer<OilTankTileEntity>{
	public OilTankRenderer(TileEntityRendererDispatcher rendererDispatcherIn){
		super(rendererDispatcherIn);
	}
	
	@Override
	public boolean isGlobalRenderer(OilTankTileEntity te){
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(OilTankTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		if(te != null && te.formed && te.getWorldNonnull().isBlockLoaded(te.getPos())){
			combinedOverlay = OverlayTexture.NO_OVERLAY;
			
			matrix.push();
			{
				switch(te.getFacing()){
					case EAST:{
						matrix.rotate(new Quaternion(0, 270F, 0, true));
						matrix.translate(0, 0, -1);
						break;
					}
					case SOUTH:{
						matrix.rotate(new Quaternion(0F, 180F, 0F, true));
						matrix.translate(-1, 0, -1);
						break;
					}
					case WEST:{
						matrix.rotate(new Quaternion(0, 90F, 0, true));
						matrix.translate(-1, 0, 0);
						break;
					}
					default:break;
				}
				
				matrix.push();
				{
					if(te.getIsMirrored()){
						// Tank Display
						if(te.posInMultiblock.equals(new BlockPos(1, 2, 5))){
							// Background
							Matrix4f mat = matrix.getLast().getMatrix();
							IVertexBuilder builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_POSITION_COLOR);
							builder.pos(mat, 1.5F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.pos(mat, 1.5F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.pos(mat, 0F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.pos(mat, 0F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							
							OilTankTileEntity master = te.master();
							if(master != null){
								FluidStack fs = master.tank.getFluid();
								if(!fs.isEmpty()){
									matrix.push();
									{
										matrix.translate(0.25, 0.875, 0.9975F);
										matrix.scale(0.0625F, -0.0625F, 0.0625F);
										
										float h = fs.getAmount() / (float) master.tank.getCapacity();
										GuiHelper.drawRepeatedFluidSprite(buffer.getBuffer(RenderType.getSolid()), matrix, fs, 0, 0 + (1 - h) * 16, 16, h * 16);
									}
									matrix.pop();
								}
							}
						}
						
						// Dynamic Fluid IO Ports
						OilTankTileEntity master = te.master();
						if(master != null){
							for(Port port:Port.DYNAMIC_PORTS){
								if(port.matches(te.posInMultiblock)){
									matrix.push();
									matrix.rotate(new Quaternion(0, 180F, 0, true));
									matrix.translate(-1, 0, -1);
									quad(matrix, buffer, master.getPortStateFor(port), port.posInMultiblock.getX() == 4, combinedLight, combinedOverlay);
									matrix.pop();
									break;
								}
							}
						}
					}else{
						// Tank Display
						if(te.posInMultiblock.equals(new BlockPos(3, 2, 5))){
							// Background
							Matrix4f mat = matrix.getLast().getMatrix();
							IVertexBuilder builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_POSITION_COLOR);
							builder.pos(mat, 1.5F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.pos(mat, 1.5F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.pos(mat, 0F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.pos(mat, 0F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							
							OilTankTileEntity master = te.master();
							if(master != null){
								FluidStack fs = master.tank.getFluid();
								if(!fs.isEmpty()){
									matrix.push();
									{
										matrix.translate(0.25, 0.875, 0.9975F);
										matrix.scale(0.0625F, -0.0625F, 0.0625F);
										
										float h = fs.getAmount() / (float) master.tank.getCapacity();
										GuiHelper.drawRepeatedFluidSprite(buffer.getBuffer(RenderType.getSolid()), matrix, fs, 0, 0 + (1 - h) * 16, 16, h * 16);
									}
									matrix.pop();
								}
							}
						}
						
						// Dynamic Fluid IO Ports
						OilTankTileEntity master = te.master();
						if(master != null){
							for(Port port:Port.DYNAMIC_PORTS){
								if(port.matches(te.posInMultiblock)){
									matrix.push();
									quad(matrix, buffer, master.getPortStateFor(port), port.posInMultiblock.getX() == 4, combinedLight, combinedOverlay);
									matrix.pop();
									break;
								}
							}
						}
					}
				}
				matrix.pop();
			}
			matrix.pop();
		}
	}
	
	public void quad(MatrixStack matrix, IRenderTypeBuffer buffer, DynPortState portState, boolean flip, int combinedLight, int combinedOverlay){
		Matrix4f mat = matrix.getLast().getMatrix();
		IVertexBuilder builder = buffer.getBuffer(IPRenderTypes.OIL_TANK);
		
		boolean input = portState == DynPortState.INPUT;
		float u0 = input ? 0.0F : 0.1F, v0 = 0.5F;
		float u1 = u0 + 0.1F, v1 = v0 + 0.1F;
		if(flip){
			builder.pos(mat, 1.001F, 0F, 0F).color(1F, 1F, 1F, 1F).tex(u1, v1).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
			builder.pos(mat, 1.001F, 1F, 0F).color(1F, 1F, 1F, 1F).tex(u1, v0).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
			builder.pos(mat, 1.001F, 1F, 1F).color(1F, 1F, 1F, 1F).tex(u0, v0).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
			builder.pos(mat, 1.001F, 0F, 1F).color(1F, 1F, 1F, 1F).tex(u0, v1).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
		}else{
			builder.pos(mat, -0.001F, 0F, 0F).color(1F, 1F, 1F, 1F).tex(u0, v1).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
			builder.pos(mat, -0.001F, 0F, 1F).color(1F, 1F, 1F, 1F).tex(u1, v1).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
			builder.pos(mat, -0.001F, 1F, 1F).color(1F, 1F, 1F, 1F).tex(u1, v0).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
			builder.pos(mat, -0.001F, 1F, 0F).color(1F, 1F, 1F, 1F).tex(u0, v0).overlay(combinedOverlay).lightmap(combinedLight).normal(1, 1, 1).endVertex();
		}
	}
}
