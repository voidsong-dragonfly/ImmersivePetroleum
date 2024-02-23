package flaxbeard.immersivepetroleum.client.particle;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;

import flaxbeard.immersivepetroleum.common.IPRegisters;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class IPParticleTypes{
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLARE_FIRE = createBasicParticle("flare_fire", false);
	public static final DeferredHolder<ParticleType<?>, ParticleType<FluidParticleData>> FLUID_SPILL = createParticleWithData("fluid_spill", FluidParticleData.DESERIALIZER, FluidParticleData.CODEC);
	
	public static void forceClassLoad(){
	}
	
	private static DeferredHolder<ParticleType<?>, SimpleParticleType> createBasicParticle(String name, boolean alwaysShow){
		return IPRegisters.registerParticleType(name, () -> new SimpleParticleType(alwaysShow));
	}
	
	@SuppressWarnings("deprecation")
	private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> createParticleWithData(String name, ParticleOptions.Deserializer<T> deserializer, Codec<T> codec){
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
