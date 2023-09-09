package flaxbeard.immersivepetroleum.client.particle;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;

import flaxbeard.immersivepetroleum.common.IPRegisters;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

@OnlyIn(Dist.CLIENT)
public class IPParticleTypes{
	public static final RegistryObject<SimpleParticleType> FLARE_FIRE = createBasicParticle("flare_fire", false);
	public static final RegistryObject<ParticleType<FluidParticleData>> FLUID_SPILL = createParticleWithData("fluid_spill", FluidParticleData.DESERIALIZER, FluidParticleData.CODEC);
	
	private static RegistryObject<SimpleParticleType> createBasicParticle(String name, boolean alwaysShow){
		return IPRegisters.registerParticleType(name, () -> new SimpleParticleType(alwaysShow));
	}
	
	@SuppressWarnings("deprecation")
	private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> createParticleWithData(String name, ParticleOptions.Deserializer<T> deserializer, Codec<T> codec){
		ParticleType<T> type = new ParticleType<>(false, deserializer){
			@Override
			@Nonnull
			public Codec<T> codec(){
				return codec;
			}
		};
		
		return IPRegisters.registerParticleType(name, () -> type);
	}
}
