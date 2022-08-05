package flaxbeard.immersivepetroleum.api.crafting.builders;

import java.util.Objects;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class CokerUnitRecipeBuilder extends IEFinishedRecipe<CokerUnitRecipeBuilder>{
	public static CokerUnitRecipeBuilder builder(ItemStack output, TagKey<Fluid> outputFluid, int fluidOutAmount){
		Objects.requireNonNull(output);
		if(output.isEmpty())
			throw new IllegalArgumentException("Input stack cannot be empty.");
		
		return new CokerUnitRecipeBuilder()
				.addResult(output)
				.addOutputFluid(outputFluid, fluidOutAmount);
	}
	
	private CokerUnitRecipeBuilder(){
		super(Serializers.COKER_SERIALIZER.get());
	}
	
	public CokerUnitRecipeBuilder addInputItem(TagKey<Item> item, int amount){
		return addInput(new IngredientWithSize(item, amount));
	}
	
	public CokerUnitRecipeBuilder addInputFluid(TagKey<Fluid> fluidTag, int amount){
		return addFluidTag("inputfluid", new FluidTagInput(fluidTag.location(), amount));
	}
	
	public CokerUnitRecipeBuilder addOutputFluid(TagKey<Fluid> fluidTag, int amount){
		return addFluidTag("resultfluid", new FluidTagInput(fluidTag.location(), amount));
	}
	
	public CokerUnitRecipeBuilder setTimeAndEnergy(int time, int energy){
		return setTime(time).setEnergy(energy);
	}
}
