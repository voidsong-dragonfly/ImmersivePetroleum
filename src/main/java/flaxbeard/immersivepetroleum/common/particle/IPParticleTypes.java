package flaxbeard.immersivepetroleum.common.particle;

import com.mojang.serialization.Codec;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;

public class IPParticleTypes{
	public static final BasicParticleType FLARE_FIRE = createBasicParticle("flare_fire", false);
	public static final ParticleType<FluidParticleData> FLUID_SPILL = createParticleWithData("fluid_spill", FluidParticleData.DESERIALIZER, FluidParticleData.CODEC);
	
	private static BasicParticleType createBasicParticle(String name, boolean alwaysShow){
		BasicParticleType particleType = new BasicParticleType(alwaysShow);
		particleType.setRegistryName(new ResourceLocation(ImmersivePetroleum.MODID, name));
		return particleType;
	}
	
	@SuppressWarnings("deprecation")
	private static <T extends IParticleData> ParticleType<T> createParticleWithData(String name, IParticleData.IDeserializer<T> deserializer, Codec<T> codec){
		ParticleType<T> type = new ParticleType<T>(false, deserializer){
			@Override
			public Codec<T> func_230522_e_(){
				return codec;
			}
		};
		type.setRegistryName(new ResourceLocation(ImmersivePetroleum.MODID, name));
		return type;
	}
}
