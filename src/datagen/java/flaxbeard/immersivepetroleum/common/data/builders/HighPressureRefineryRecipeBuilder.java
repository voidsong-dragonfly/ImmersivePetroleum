package flaxbeard.immersivepetroleum.common.data.builders;


import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import flaxbeard.immersivepetroleum.api.crafting.HighPressureRefineryRecipe;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class HighPressureRefineryRecipeBuilder extends IPRecipeBuilder<HighPressureRefineryRecipeBuilder>{

	private FluidTagInput fluidInput;
	private FluidTagInput fluidInput2;
	private ChancedItemStack itemOutput = new ChancedItemStack(ItemStack.EMPTY, 0);
	private FluidStack fluidOutput;
	int energy;
	int time;

	private HighPressureRefineryRecipeBuilder(){ }

	public static HighPressureRefineryRecipeBuilder builder(){
		return new HighPressureRefineryRecipeBuilder();
	}

	public HighPressureRefineryRecipeBuilder itemOutput(ChancedItemStack output)
	{
		this.itemOutput = output;
		return this;
	}

	public HighPressureRefineryRecipeBuilder fluidOutput(FluidStack output)
	{
		this.fluidOutput = output;
		return this;
	}

	public HighPressureRefineryRecipeBuilder fluidInput(FluidTagInput input)
	{
		if(this.fluidInput==null) this.fluidInput = input;
		else this.fluidInput2 = input;
		return this;
	}

	public HighPressureRefineryRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public HighPressureRefineryRecipeBuilder setEnergy(int amount)
	{
		this.energy = amount;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		HighPressureRefineryRecipe recipe = new HighPressureRefineryRecipe(fluidOutput, itemOutput, fluidInput, fluidInput2, energy, time);
		out.accept(name, recipe, null, getConditions());
	}
}
