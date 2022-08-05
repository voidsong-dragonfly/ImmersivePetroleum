package flaxbeard.immersivepetroleum.client.particle;

import com.mojang.serialization.Codec;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class IPParticleTypes{
	public static final SimpleParticleType FLARE_FIRE = createBasicParticle("flare_fire", false);
	public static final ParticleType<FluidParticleData> FLUID_SPILL = createParticleWithData("fluid_spill", FluidParticleData.DESERIALIZER, FluidParticleData.CODEC);
	
	private static SimpleParticleType createBasicParticle(String name, boolean alwaysShow){
		SimpleParticleType particleType = new SimpleParticleType(alwaysShow);
		particleType.setRegistryName(ResourceUtils.ip(name));
		return particleType;
	}
	
	@SuppressWarnings("deprecation")
	private static <T extends ParticleOptions> ParticleType<T> createParticleWithData(String name, ParticleOptions.Deserializer<T> deserializer, Codec<T> codec){
		ParticleType<T> type = new ParticleType<>(false, deserializer){
			@Override
			@Nonnull
			public Codec<T> codec(){
				return codec;
			}
		};
		type.setRegistryName(ResourceUtils.ip(name));
		return type;
	}
}
