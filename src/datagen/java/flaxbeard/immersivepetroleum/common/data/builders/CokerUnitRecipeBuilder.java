package flaxbeard.immersivepetroleum.common.data.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class CokerUnitRecipeBuilder extends IPRecipeBuilder<CokerUnitRecipeBuilder>
{
	private IngredientWithSize itemInput;
	private FluidTagInput fluidInput;
	private ItemStack itemOutput;
	private FluidStack fluidOutput;
	int energy;
	int time;

	private CokerUnitRecipeBuilder(){ }

	public static CokerUnitRecipeBuilder builder(){
		return new CokerUnitRecipeBuilder();
	}

	public CokerUnitRecipeBuilder itemOutput(ItemStack output)
	{
		this.itemOutput = output;
		return this;
	}

	public CokerUnitRecipeBuilder itemInput(IngredientWithSize input)
	{
		this.itemInput = input;
		return this;
	}

	public CokerUnitRecipeBuilder fluidOutput(FluidStack output)
	{
		this.fluidOutput = output;
		return this;
	}

	public CokerUnitRecipeBuilder fluidInput(FluidTagInput input)
	{
		this.fluidInput = input;
		return this;
	}

	public CokerUnitRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public CokerUnitRecipeBuilder setEnergy(int amount)
	{
		this.energy = amount;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		CokerUnitRecipe recipe = new CokerUnitRecipe(itemOutput, fluidOutput, itemInput, fluidInput, energy, time);
		out.accept(name, recipe, null, getConditions());
	}
}
