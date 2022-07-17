package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import flaxbeard.immersivepetroleum.client.render.dyn.DynamicTextureWrapper;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.SeismicSurveyTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * TODO Highly Experimental. Name not final. Function not final.
 * 
 * @author TwistedGate
 */
public class RenderTests{
	
	@SubscribeEvent
	public void renderHandEvent(RenderHandEvent event){
		if(event.getItemStack().getItem().equals(IPContent.Items.SURVEYRESULT.get())){
			event.setCanceled(true);
		}
	}
	
	static final Tesselator TESSELATOR = new Tesselator();
	
	@SubscribeEvent
	public void renderGameOverlayEvent(RenderGameOverlayEvent.Post event){
		Minecraft mc = Minecraft.getInstance();
		
		if(mc.player != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL){
			ItemStack main = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack off = mc.player.getItemInHand(InteractionHand.OFF_HAND); // TODO Offhand variant
			
			if((main != ItemStack.EMPTY && main.getItem() == IPContent.Items.SURVEYRESULT.get()) || (off != ItemStack.EMPTY && off.getItem() == IPContent.Items.SURVEYRESULT.get())){
				PoseStack matrix = event.getMatrixStack();
				
				int width = event.getWindow().getGuiScaledWidth();
				int height = event.getWindow().getGuiScaledHeight();
				
				float xCenter = width / 2F;
				float yCenter = height / 2F;
				
				CompoundTag tag;
				if((main.hasTag() && main.getTag() != null) && (tag = main.getTagElement("surveyscan")) != null){
					DynamicTextureWrapper wrapper = DynamicTextureWrapper.getOrCreate(SeismicSurveyTileEntity.SCAN_SIZE, SeismicSurveyTileEntity.SCAN_SIZE, tag);
					
					if(wrapper != null){
						MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(TESSELATOR.getBuilder());
						
						matrix.pushPose();
						{
							float yRot = mc.player.getYRot();
							matrix.translate(xCenter, yCenter, -SeismicSurveyTileEntity.SCAN_SIZE);
							matrix.mulPose(new Quaternion(0, 0, -yRot, true));
							matrix.translate(-wrapper.width, -wrapper.height, 0);
							
							int a = wrapper.width * 2;
							int b = wrapper.height * 2;
							
							matrix.pushPose();
							{
								VertexConsumer builder = buffer.getBuffer(wrapper.renderType);
								builder.defaultColor(255, 255, 255, 255);
								Matrix4f mat = matrix.last().pose();
								
								builder.vertex(mat, 0, 0, 0).uv(0.0F, 0.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, 0, b, 0).uv(0.0F, 1.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, a, b, 0).uv(1.0F, 1.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, a, 0, 0).uv(1.0F, 0.0F).uv2(0xF000F0).endVertex();
							}
							matrix.popPose();
						}
						matrix.popPose();
						
						buffer.endBatch();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void entityViewRenderEvent(EntityViewRenderEvent event){
	}
	
	@SubscribeEvent
	public void cameraSetupRenderEvent(EntityViewRenderEvent.CameraSetup event){
	}
	
	@SubscribeEvent
	public void fieldOfViewRenderEvent(EntityViewRenderEvent.FieldOfView event){
	}
}
