package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import flaxbeard.immersivepetroleum.client.render.dyn.DynamicTextureWrapper;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.SeismicSurveyTileEntity;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author TwistedGate
 */
public class SeismicResultRenderer{
	
	@SubscribeEvent
	public void renderHandEvent(RenderHandEvent event){
		ItemStack stack = event.getItemStack();
		if(stack.getItem().equals(IPContent.Items.SURVEYRESULT.get()) && stack.hasTag() && stack.getTagElement("surveyscan") != null){
			event.setCanceled(true);
		}
	}
	
	static final Tesselator TESSELATOR = new Tesselator();
	
	private static final ResourceLocation OVERLAY = ResourceUtils.ip("textures/gui/seismic_overlay.png");
	
	@SubscribeEvent
	public void renderGameOverlayEvent(RenderGameOverlayEvent.Post event){
		Minecraft mc = Minecraft.getInstance();
		
		if(mc.player != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL){
			ItemStack main = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack off = mc.player.getItemInHand(InteractionHand.OFF_HAND); // TODO Offhand variant?
			
			if((main != ItemStack.EMPTY && main.getItem() == IPContent.Items.SURVEYRESULT.get()) || (off != ItemStack.EMPTY && off.getItem() == IPContent.Items.SURVEYRESULT.get())){
				PoseStack matrix = event.getMatrixStack();
				
				int guiScaledWidth = event.getWindow().getGuiScaledWidth();
				int guiScaledHeight = event.getWindow().getGuiScaledHeight();
				
				float xCenter = guiScaledWidth / 2F;
				float yCenter = guiScaledHeight / 2F;
				
				CompoundTag tag;
				if((main.hasTag() && main.getTag() != null) && (tag = main.getTagElement("surveyscan")) != null){
					DynamicTextureWrapper wrapper = DynamicTextureWrapper.getOrCreate(SeismicSurveyTileEntity.SCAN_SIZE, SeismicSurveyTileEntity.SCAN_SIZE, tag);
					
					if(wrapper != null){
						MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(TESSELATOR.getBuilder());
						
						matrix.pushPose();
						{
							matrix.translate(xCenter, yCenter, 0);
							matrix.scale(2F, 2F, 2F);
							
							matrix.pushPose();
							{
								matrix.translate(-wrapper.width / 2F, -wrapper.height / 2F, 0);
								
								int a = wrapper.width;
								int b = wrapper.height;
								VertexConsumer builder = buffer.getBuffer(wrapper.renderType);
								builder.defaultColor(255, 255, 255, 255);
								Matrix4f mat = matrix.last().pose();
								
								builder.vertex(mat, 0, 0, 0).uv(1.0F, 1.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, 0, b, 0).uv(1.0F, 0.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, a, b, 0).uv(0.0F, 0.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, a, 0, 0).uv(0.0F, 1.0F).uv2(0xF000F0).endVertex();
							}
							matrix.popPose();
							
							matrix.pushPose();
							{
								final int w = 85;
								final int h = 85;
								final float u = w / 256F;
								final float v = h / 256F;
								
								matrix.translate(-w / 2F, -h / 2F, 1);
								
								VertexConsumer builder = buffer.getBuffer(RenderType.text(OVERLAY));
								builder.defaultColor(255, 255, 255, 255);
								Matrix4f mat = matrix.last().pose();
								
								builder.vertex(mat, 0, 0, 0).uv(0.0F, 0.0F).uv2(0xF000F0).endVertex();
								builder.vertex(mat, 0, h, 0).uv(0.0F, v).uv2(0xF000F0).endVertex();
								builder.vertex(mat, w, h, 0).uv(u, v).uv2(0xF000F0).endVertex();
								builder.vertex(mat, w, 0, 0).uv(u, 0.0F).uv2(0xF000F0).endVertex();
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
