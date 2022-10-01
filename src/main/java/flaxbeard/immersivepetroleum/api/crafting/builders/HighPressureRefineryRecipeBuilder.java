package flaxbeard.immersivepetroleum.api.crafting.builders;

import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class HighPressureRefineryRecipeBuilder extends IEFinishedRecipe<HighPressureRefineryRecipeBuilder>{
	
	public static HighPressureRefineryRecipeBuilder builder(FluidStack fluidOutput, int energy, int time){
		return new HighPressureRefineryRecipeBuilder()
				.setTimeAndEnergy(time, energy)
				.addResultFluid(fluidOutput);
	}
	
	protected HighPressureRefineryRecipeBuilder(){
		super(Serializers.HYDROTREATER_SERIALIZER.get());
	}
	
	public HighPressureRefineryRecipeBuilder addResultFluid(FluidStack fluid){
		return addFluid("result", fluid);
	}
	
	public HighPressureRefineryRecipeBuilder addInputFluid(FluidStack fluid){
		return addFluid("input", fluid);
	}
	
	public HighPressureRefineryRecipeBuilder addInputFluid(FluidTagInput fluid){
		return addFluidTag("input", fluid);
	}
	
	public HighPressureRefineryRecipeBuilder addInputFluid(TagKey<Fluid> fluid, int amount){
		return addFluidTag("input", fluid, amount);
	}
	
	/** Optionaly add a second fluid to be pumped in */
	public HighPressureRefineryRecipeBuilder addSecondaryInputFluid(FluidStack fluid){
		return addFluid("secondary_input", fluid);
	}
	
	/** Optionaly add a second fluid to be pumped in */
	public HighPressureRefineryRecipeBuilder addSecondaryInputFluid(FluidTagInput fluid){
		return addFluidTag("secondary_input", fluid);
	}
	
	/** Optionaly add a second fluid to be pumped in */
	public HighPressureRefineryRecipeBuilder addSecondaryInputFluid(TagKey<Fluid> fluid, int amount){
		return addFluidTag("secondary_input", fluid, amount);
	}
	
	public HighPressureRefineryRecipeBuilder addItemWithChance(ItemStack item, double chance){
		return addWriter(jsonObject -> {
			jsonObject.add("secondary_result", this.serializerItemStackWithChance(item, chance));
		});
	}
	
	protected HighPressureRefineryRecipeBuilder setTimeAndEnergy(int time, int energy){
		return setTime(time).setEnergy(energy);
	}
	
	protected JsonObject serializerItemStackWithChance(ItemStack stack, double chance){
		JsonObject itemJson = this.serializeItemStack(stack);
		itemJson.addProperty("chance", Double.toString(chance));
		return itemJson;
	}
}
