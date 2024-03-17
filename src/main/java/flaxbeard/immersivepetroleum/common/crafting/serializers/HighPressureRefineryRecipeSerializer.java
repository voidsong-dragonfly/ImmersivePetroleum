package flaxbeard.immersivepetroleum.common.crafting.serializers;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.api.crafting.HighPressureRefineryRecipe;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class HighPressureRefineryRecipeSerializer extends IERecipeSerializer<HighPressureRefineryRecipe>{
	// @formatter:off
	public static final Codec<HighPressureRefineryRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		FluidStack.CODEC.fieldOf("result").forGetter(r -> r.output),
		FluidTagInput.CODEC.fieldOf("input").forGetter(r -> r.inputFluid),
		FluidTagInput.CODEC.optionalFieldOf("secondary_input").forGetter(r -> Optional.ofNullable(r.inputFluidSecondary)),
		ChancedItemStack.CODEC.optionalFieldOf("secondary_result", ChancedItemStack.EMPTY).forGetter(a -> a.outputItem),
		Codec.INT.fieldOf("energy").forGetter(r -> r.getBaseEnergy()),
		Codec.INT.fieldOf("time").forGetter(r -> r.getBaseTime())
	).apply(inst, (result, inputFluid, inputFluidSecondary, secondaryResult, energy, time) -> {
		return new HighPressureRefineryRecipe(result, secondaryResult, inputFluid, inputFluidSecondary.orElse(null), energy, time);
	}));
	// @formatter:on
	
	@Override
	public Codec<HighPressureRefineryRecipe> codec(){
		return CODEC;
	}
	
	@Override
	public HighPressureRefineryRecipe fromNetwork(FriendlyByteBuf buffer){
		ChancedItemStack outputItem = new ChancedItemStack(buffer);
		
		FluidStack output = buffer.readFluidStack();
		FluidTagInput inputFluid0 = FluidTagInput.read(buffer);
		FluidTagInput inputFluid1 = null;
		
		boolean hasSecondary = buffer.readBoolean();
		if(hasSecondary){
			inputFluid1 = FluidTagInput.read(buffer);
		}
		
		int energy = buffer.readInt();
		int time = buffer.readInt();
		
		return new HighPressureRefineryRecipe(output, outputItem, inputFluid0, inputFluid1, energy, time);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buffer, HighPressureRefineryRecipe recipe){
		recipe.outputItem.writeToBuffer(buffer);
		
		buffer.writeFluidStack(recipe.output);
		recipe.inputFluid.write(buffer);
		
		boolean hasSecondary = recipe.getSecondaryInputFluid() != null;
		buffer.writeBoolean(hasSecondary);
		if(hasSecondary){
			recipe.inputFluidSecondary.write(buffer);
		}
		
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
	
	@Override
	public ItemStack getIcon(){
		return IPContent.Multiblock.HYDROTREATER.iconStack();
	}
}
