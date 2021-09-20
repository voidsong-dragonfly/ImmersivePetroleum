package flaxbeard.immersivepetroleum.common.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class FluidSpill extends SpriteTexturedParticle{
	final double ogMotionX, ogMotionY, ogMotionZ;
	protected FluidSpill(Fluid fluid, ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ){
		super(world, x, y, z, motionX, motionY, motionZ);
		setSize(0.5F, 0.5F);
		setMaxAge(50);
		this.particleScale = 4 / 16F;
		
		this.motionY = motionY;
		
		this.ogMotionX = motionX;
		this.ogMotionY = motionY;
		this.ogMotionZ = motionZ;
		
		FluidStack fs = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
		
		ResourceLocation location = fluid.getAttributes().getStillTexture(fs);
		TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(location);
		if(sprite == null){
			sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(MissingTextureSprite.getLocation());
		}
		setSprite(sprite);
		
		int argb = fluid.getFluid().getAttributes().getColor(fs);
		this.particleAlpha = ((argb >> 24) & 255) / 255F;
		this.particleRed = ((argb >> 16) & 255) / 255F;
		this.particleGreen = ((argb >> 8 & 255)) / 255F;
		this.particleBlue = (argb & 255) / 255F;
	}
	
	@Override
	public void tick(){
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.age++ >= this.maxAge){
			this.setExpired();
		}else{
			this.motionY -= 0.04D;
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.98D;
			this.motionY *= 0.98D;
			this.motionZ *= 0.98D;
			if(this.onGround){
				this.motionX *= 0.7D;
				this.motionZ *= 0.7D;
			}
			
			this.particleScale *= 0.97D;
		}
	}
	
	@Override
	public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks){
		super.renderParticle(buffer, renderInfo, partialTicks);
	}
	
	@Override
	public IParticleRenderType getRenderType(){
		return IParticleRenderType.TERRAIN_SHEET;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<FluidParticleData>{
		@Override
		public Particle makeParticle(FluidParticleData type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			FluidSpill fluidSpill = new FluidSpill(type.getFluid(), world, x, y, z, xSpeed, ySpeed, zSpeed);
			return fluidSpill;
		}
	}
}
