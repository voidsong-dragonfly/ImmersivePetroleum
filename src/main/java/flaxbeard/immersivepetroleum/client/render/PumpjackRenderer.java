package flaxbeard.immersivepetroleum.client.render;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import flaxbeard.immersivepetroleum.client.model.IPModel;
import flaxbeard.immersivepetroleum.client.model.IPModels;
import flaxbeard.immersivepetroleum.client.model.ModelPumpjack;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class PumpjackRenderer implements BlockEntityRenderer<PumpjackTileEntity>{
	private static final Supplier<IPModel> pumpjackarm = IPModels.getSupplier(ModelPumpjack.ID);
	
	@Override
	public int getViewDistance(){
		return 100;
	}
	
	@Override
	public void render(@Nonnull PumpjackTileEntity te, float partialTicks, @Nonnull PoseStack transform, @Nonnull MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn){
		if(!te.isDummy()){
			transform.pushPose();
			Direction rotation = te.getFacing();
			switch(rotation){
				case NORTH -> {
					transform.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
					transform.translate(-6, 0, -1);
				}
				case EAST -> transform.translate(-5, 0, -1);
				case SOUTH -> {
					transform.mulPose(new Quaternionf().rotateY(3*Mth.HALF_PI));
					transform.translate(-5, 0, -2);
				}
				case WEST -> {
					transform.mulPose(new Quaternionf().rotateY(Mth.PI));
					transform.translate(-6, 0, -2);
				}
				default -> {
				}
			}
			
			ModelPumpjack model;
			if((model = (ModelPumpjack) pumpjackarm.get()) != null){
				float ticks = te.activeTicks + (te.wasActive ? partialTicks : 0);
				model.ticks = 1.5F * ticks;
				
				model.renderToBuffer(transform, buffer.getBuffer(model.renderType(ModelPumpjack.TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
			}
			transform.popPose();
		}
	}
}
