package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import flaxbeard.immersivepetroleum.client.model.ModelMotorboat;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;;

@OnlyIn(Dist.CLIENT)
public class MotorboatRenderer extends EntityRenderer<MotorboatEntity>{
	private static ResourceLocation texture = rl("textures/models/boat_motor.png");
	private static ResourceLocation textureArmor = rl("textures/models/boat_motor_armor.png");
	
	/** instance of ModelBoat for rendering */
	protected final ModelMotorboat modelBoat = new ModelMotorboat();
	
	public MotorboatRenderer(EntityRendererProvider.Context renderManagerIn){
		super(renderManagerIn);
		this.shadowRadius = 0.8F;
	}
	
	@Override
	public void render(MotorboatEntity entity, float entityYaw, float partialTicks, PoseStack matrix, MultiBufferSource bufferIn, int packedLight){
		matrix.pushPose();
		{
			matrix.translate(0.0D, 0.375D, 0.0D);
			this.setupRotation(entity, entityYaw, partialTicks, matrix);
			this.modelBoat.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
			
			if(entity.isInLava()){
				matrix.translate(0, -3.9F / 16F, 0);
			}
			
			{
				if(!entity.isEmergency()){
					if(entity.isForwardDown()){
						entity.propellerXRotSpeed += entity.isBoosting ? 0.2F : 0.1F;
					}
					if(entity.isBackDown()){
						entity.propellerXRotSpeed -= 0.2F;
					}
					
					entity.propellerXRot += entity.propellerXRotSpeed;
					entity.propellerXRot %= 360.0F;
				}
				
				entity.propellerXRotSpeed *= 0.985F;
				if(entity.propellerXRotSpeed != 0.0F && entity.propellerXRotSpeed >= -1.0E-3F && entity.propellerXRotSpeed <= 1.0E-3F){
					entity.propellerXRotSpeed = 0.0F;
				}
				
				this.modelBoat.propeller.xRot = entity.propellerXRot * Mth.DEG_TO_RAD;
				
				float pr = entity.isEmergency() ? 0F : entity.propellerYRotation;
				if(entity.isLeftInDown() && !entity.isRightInDown() && pr > -1)
					pr = pr - 0.1F * partialTicks;
				
				if(entity.isRightInDown() && !entity.isLeftInDown() && pr < 1)
					pr = pr + 0.1F * partialTicks;
				
				if(!entity.isLeftInDown() && !entity.isRightInDown())
					pr = (float) (pr * Math.pow(0.7, partialTicks));
				
				this.modelBoat.propellerAssembly.yRot = (float) Math.toRadians(pr * 15);
			}
			
			this.modelBoat.renderToBuffer(matrix, bufferIn.getBuffer(this.modelBoat.renderType(getEntityTexture(entity.isFireproof))), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			
			if(entity.hasPaddles){
				VertexConsumer vbuilder_normal = bufferIn.getBuffer(this.modelBoat.renderType(texture));
				
				this.modelBoat.paddles[0].render(matrix, vbuilder_normal, packedLight, OverlayTexture.NO_OVERLAY);
				this.modelBoat.paddles[1].render(matrix, vbuilder_normal, packedLight, OverlayTexture.NO_OVERLAY);
			}
			
			VertexConsumer vbuilder_armored = bufferIn.getBuffer(this.modelBoat.renderType(textureArmor));
			
			if(entity.hasIcebreaker){
				this.modelBoat.icebreak.render(matrix, vbuilder_armored, packedLight, OverlayTexture.NO_OVERLAY);
			}
			
			if(entity.hasRudders){
				this.modelBoat.ruddersBase.render(matrix, vbuilder_armored, packedLight, OverlayTexture.NO_OVERLAY);
				
				float pr = entity.propellerYRotation;
				if(entity.isLeftInDown() && !entity.isRightInDown() && pr > -1){
					pr = pr - 0.1F * partialTicks;
				}
				
				if(entity.isRightInDown() && !entity.isLeftInDown() && pr < 1){
					pr = pr + 0.1F * partialTicks;
				}
				
				if(!entity.isLeftInDown() && !entity.isRightInDown()){
					pr = (float) (pr * Math.pow(0.7F, partialTicks));
				}
				
				this.modelBoat.rudder1.yRot = (float) Math.toRadians(pr * 20F);
				this.modelBoat.rudder2.yRot = (float) Math.toRadians(pr * 20F);
				
				this.modelBoat.rudder1.render(matrix, vbuilder_armored, packedLight, OverlayTexture.NO_OVERLAY);
				this.modelBoat.rudder2.render(matrix, vbuilder_armored, packedLight, OverlayTexture.NO_OVERLAY);
			}
			
			if(entity.hasTank){
				this.modelBoat.tank.render(matrix, vbuilder_armored, packedLight, OverlayTexture.NO_OVERLAY);
			}
			
			if(!entity.isUnderWater()){
				VertexConsumer vbuilder_mask = bufferIn.getBuffer(RenderType.waterMask());
				this.modelBoat.noWaterRenderer().render(matrix, vbuilder_mask, packedLight, OverlayTexture.NO_OVERLAY);
			}
		}
		matrix.popPose();
		
		super.render(entity, entityYaw, partialTicks, matrix, bufferIn, packedLight);
	}
	
	@Override
	public ResourceLocation getTextureLocation(MotorboatEntity entity){
		return texture;
	}
	
	public ResourceLocation getEntityTexture(boolean armored){
		return armored ? textureArmor : texture;
	}
	
	public void setupRotation(MotorboatEntity boat, float entityYaw, float partialTicks, PoseStack matrix){
		matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
		float f = (float) boat.getHurtTime() - partialTicks;
		float f1 = boat.getDamage() - partialTicks;
		
		if(f1 < 0.0F){
			f1 = 0.0F;
		}
		
		if(f > 0.0F){
			matrix.mulPose(new Quaternion(Mth.sin(f) * f * f1 / 10.0F * (float) boat.getHurtDir(), 0.0F, 0.0F, true));
		}
		
		if(boat.isBoosting){
			matrix.mulPose(new Quaternion(3, 0, 0, true));
		}
		
		matrix.scale(-1.0F, -1.0F, 1.0F);
		matrix.mulPose(Vector3f.YP.rotationDegrees(90.0F));
	}
	
	private static ResourceLocation rl(String str){
		return new ResourceLocation("immersivepetroleum", str);
	}
}
