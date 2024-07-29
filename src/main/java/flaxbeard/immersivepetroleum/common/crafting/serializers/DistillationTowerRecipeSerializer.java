package flaxbeard.immersivepetroleum.common.crafting.serializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class DistillationTowerRecipeSerializer extends IERecipeSerializer<DistillationTowerRecipe>{
	
	// @formatter:off
	public static final Codec<DistillationTowerRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		FluidTagInput.CODEC.fieldOf("input").forGetter(DistillationTowerRecipe::getInputFluid),
		FluidStack.CODEC.listOf().fieldOf("results").forGetter(MultiblockRecipe::getFluidOutputs),
		ChancedItemStack.CODEC.listOf().fieldOf("byproducts").forGetter(DistillationTowerRecipe::getByproducts),
		Codec.INT.fieldOf("energy").forGetter(MultiblockRecipe::getBaseEnergy),
		Codec.INT.fieldOf("time").forGetter(MultiblockRecipe::getBaseTime)
	).apply(inst, (input, result, byproducts, energy, time) -> {
		return new DistillationTowerRecipe(result, byproducts, input, energy, time);
	}));
	// @formatter:on
	
	@Override
	public Codec<DistillationTowerRecipe> codec(){
		return CODEC;
	}
	
	@Override
	public DistillationTowerRecipe fromNetwork(FriendlyByteBuf buffer){
		List<FluidStack> fluidOutput = new ArrayList<>();
		for(int i = 0;i < buffer.readInt();i++)
			fluidOutput.add(buffer.readFluidStack());
		
		List<ChancedItemStack> byproducts = List.of(ChancedItemStack.readArrayFromBuffer(buffer));
		
		FluidTagInput input = FluidTagInput.read(buffer);
		int energy = buffer.readInt();
		int time = buffer.readInt();
		
		return new DistillationTowerRecipe(fluidOutput, byproducts, input, energy, time);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buffer, DistillationTowerRecipe recipe){
		buffer.writeInt(recipe.getFluidOutputs().size());
		for(FluidStack stack:recipe.getFluidOutputs())
			buffer.writeFluidStack(stack);
		
		ChancedItemStack.writeArrayToBuffer(recipe.getByproducts().toArray(new ChancedItemStack[0]), buffer);
		
		recipe.getInputFluid().write(buffer);
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
	
	@Override
	public ItemStack getIcon(){
		return IPContent.Multiblock.DISTILLATIONTOWER.iconStack();
	}
}
