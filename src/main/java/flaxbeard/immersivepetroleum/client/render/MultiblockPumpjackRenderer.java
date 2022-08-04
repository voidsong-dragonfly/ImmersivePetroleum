package flaxbeard.immersivepetroleum.client.render;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

import flaxbeard.immersivepetroleum.client.model.IPModel;
import flaxbeard.immersivepetroleum.client.model.IPModels;
import flaxbeard.immersivepetroleum.client.model.ModelPumpjack;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiblockPumpjackRenderer implements BlockEntityRenderer<PumpjackTileEntity>{
	private static final Supplier<IPModel> pumpjackarm = IPModels.getSupplier(ModelPumpjack.ID);
	
	@Override
	public int getViewDistance(){
		return 100;
	}
	
	@Override
	public void render(PumpjackTileEntity te, float partialTicks, PoseStack transform, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn){
		if(te != null && !te.isDummy()){
			transform.pushPose();
			Direction rotation = te.getFacing();
            switch(rotation){
                case NORTH -> {
                    transform.mulPose(new Quaternion(0, 90F, 0, true));
                    transform.translate(-6, 0, -1);
                }
                case EAST -> transform.translate(-5, 0, -1);
                case SOUTH -> {
                    transform.mulPose(new Quaternion(0, 270F, 0, true));
                    transform.translate(-5, 0, -2);
                }
                case WEST -> {
                    transform.mulPose(new Quaternion(0, 180F, 0, true));
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
