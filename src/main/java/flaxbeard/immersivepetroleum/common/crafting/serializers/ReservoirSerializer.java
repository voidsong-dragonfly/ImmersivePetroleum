package flaxbeard.immersivepetroleum.common.crafting.serializers;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ReservoirSerializer extends IERecipeSerializer<ReservoirType>{
	
	// @formatter:off
	public static final Codec<ReservoirType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		Codec.STRING.fieldOf("name").forGetter(r -> r.name),
		ResourceLocation.CODEC.fieldOf("fluid").forGetter(r -> r.fluidLocation),
		Codec.INT.fieldOf("fluidminimum").forGetter(r -> r.minSize),
		Codec.INT.fieldOf("fluidcapacity").forGetter(r -> r.maxSize),
		Codec.INT.fieldOf("fluidtrace").forGetter(r -> r.residual),
		Codec.INT.fieldOf("equilibrium").forGetter(r -> r.equilibrium),
		Codec.INT.fieldOf("weight").forGetter(r -> r.weight),

		ReservoirType.BWList.CODEC.optionalFieldOf("dimensions").forGetter(r -> Optional.of(r.getDimensions())),
		ReservoirType.BWList.CODEC.optionalFieldOf("biomes").forGetter(r -> Optional.of(r.getBiomes()))
		
	).apply(inst, (name, fluid, min, max, trace, equilibrium, weight, dimensions, biomes) -> {
		ReservoirType type = new ReservoirType(name, fluid, min, max, trace, equilibrium, weight);
		
		dimensions.ifPresent(type::setDimensions);
		biomes.ifPresent(type::setBiomes);
		
		return type;
	}));
	// @formatter:on
	
	@Override
	public Codec<ReservoirType> codec(){
		return CODEC;
	}
	
	@Override
	public ReservoirType fromNetwork(FriendlyByteBuf buffer){
		return new ReservoirType(buffer.readNbt()); // Very convenient having the NBT stuff already.
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buffer, ReservoirType recipe){
		buffer.writeNbt(recipe.writeToNBT());
	}
	
	@Override
	public ItemStack getIcon(){
		return ItemStack.EMPTY;
	}
}
