package flaxbeard.immersivepetroleum.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidParticleData implements IParticleData{
	public static final Codec<FluidParticleData> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(Codec.STRING.fieldOf("fluid").forGetter(data -> data.fluid.getRegistryName().toString())).apply(instance, FluidParticleData::new);
	});
	
	@SuppressWarnings("deprecation")
	public static final IParticleData.IDeserializer<FluidParticleData> DESERIALIZER = new IParticleData.IDeserializer<FluidParticleData>(){
		@Override
		public FluidParticleData deserialize(ParticleType<FluidParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
			String name = reader.getString();
			return new FluidParticleData(name);
		}
		
		@Override
		public FluidParticleData read(ParticleType<FluidParticleData> particleTypeIn, PacketBuffer buffer){
			String name = buffer.readString();
			return new FluidParticleData(name);
		}
	};
	
	private final Fluid fluid;
	public FluidParticleData(String name){
		this(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(name)));
	}
	
	public FluidParticleData(Fluid fluid){
		this.fluid = fluid;
	}
	
	@Override
	public ParticleType<FluidParticleData> getType(){
		return IPParticleTypes.FLUID_SPILL;
	}
	
	@Override
	public void write(PacketBuffer buffer){
		buffer.writeString(this.fluid.getRegistryName().toString());
	}
	
	@Override
	public String getParameters(){
		return this.fluid.getRegistryName().toString();
	}
	
	@OnlyIn(Dist.CLIENT)
	public Fluid getFluid(){
		return this.fluid;
	}
}
