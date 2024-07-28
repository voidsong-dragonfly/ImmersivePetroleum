package flaxbeard.immersivepetroleum.client.render;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.ILubricationHandler;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Quaternionf;

public class AutoLubricatorRenderer implements BlockEntityRenderer<AutoLubricatorTileEntity>{
	
	@Override
	public boolean shouldRenderOffScreen(@Nonnull AutoLubricatorTileEntity te){
		return false;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void render(@Nonnull AutoLubricatorTileEntity te, float partialTicks, @Nonnull PoseStack transform, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn){
		if(te.isSlave)
			return;
		
		FluidStack fs = te.tank.getFluid();
		float level = 0;
		if(!fs.isEmpty()){
			level = fs.getAmount() / (float) te.tank.getCapacity();
		}
		
		if(level > 0){
			float height = 16;
			
			transform.pushPose();
			{
				float scale = 0.0625f;
				transform.translate(0.25, 0.875, 0.25);
				transform.scale(scale, scale, scale);
				
				VertexConsumer builder = bufferIn.getBuffer(RenderType.solid());
				
				float h = height * level;
				GuiHelper.drawRepeatedFluidSprite(builder, transform, fs, 0, 0, 8, h);
				transform.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
				transform.translate(-7.98, 0, 0);
				GuiHelper.drawRepeatedFluidSprite(builder, transform, fs, 0, 0, 8, h);
				transform.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
				transform.translate(-7.98, 0, 0);
				GuiHelper.drawRepeatedFluidSprite(builder, transform, fs, 0, 0, 8, h);
				transform.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
				transform.translate(-7.98, 0, 0);
				GuiHelper.drawRepeatedFluidSprite(builder, transform, fs, 0, 0, 8, h);
				if(h < height){
					transform.mulPose(new Quaternionf().rotateX(Mth.HALF_PI));
					transform.translate(0, 0, -h);
					GuiHelper.drawRepeatedFluidSprite(builder, transform, fs, 0, 0, 8, 8);
				}
			}
			transform.popPose();
		}
		
		transform.pushPose();
		{
			BlockPos target = te.getBlockPos().relative(te.getFacing());
			BlockEntity test = te.getLevel().getBlockEntity(target);
			
			ILubricationHandler<BlockEntity> handler = LubricatedHandler.getHandlerForTile(test);
			if(handler != null){
				BlockEntity master = handler.isPlacedCorrectly(te.getLevel(), te, te.getFacing());
				if(master != null){
					handler.renderPipes(te, master, transform, bufferIn, combinedLightIn, combinedOverlayIn);
				}
			}
		}
		transform.popPose();
	}
}
