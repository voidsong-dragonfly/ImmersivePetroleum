package flaxbeard.immersivepetroleum.common.data.builders;

import java.util.ArrayList;
import java.util.List;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Distillation Recipe creation using DataGeneration
 * 
 * @author TwistedGate
 */
public class DistillationTowerRecipeBuilder extends IPRecipeBuilder<DistillationTowerRecipeBuilder>{

	private FluidTagInput fluidInput;
	private final List<ChancedItemStack> itemOutputs = new ArrayList<>();
	private final List <FluidStack> fluidOutputs = new ArrayList<>();
	int energy;
	int time;

	private DistillationTowerRecipeBuilder(){ }

	public static DistillationTowerRecipeBuilder builder(){
		return new DistillationTowerRecipeBuilder();
	}

	public DistillationTowerRecipeBuilder itemOutput(ChancedItemStack output)
	{
		this.itemOutputs.add(output);
		return this;
	}

	public DistillationTowerRecipeBuilder fluidOutput(FluidStack output)
	{
		this.fluidOutputs.add(output);
		return this;
	}

	public DistillationTowerRecipeBuilder fluidInput(FluidTagInput input)
	{
		this.fluidInput = input;
		return this;
	}

	public DistillationTowerRecipeBuilder setTime(int time)
	{
		this.time = time;
		return this;
	}

	public DistillationTowerRecipeBuilder setEnergy(int amount)
	{
		this.energy = amount;
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation name)
	{
		DistillationTowerRecipe recipe = new DistillationTowerRecipe(fluidOutputs, itemOutputs, fluidInput, energy, time);
		out.accept(name, recipe, null, getConditions());
	}
}
