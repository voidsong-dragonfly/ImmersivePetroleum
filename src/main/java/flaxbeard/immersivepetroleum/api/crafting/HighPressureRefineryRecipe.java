package flaxbeard.immersivepetroleum.api.crafting;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class HighPressureRefineryRecipe extends MultiblockRecipe{
	
	public static Map<ResourceLocation, RecipeHolder<HighPressureRefineryRecipe>> recipes = new HashMap<>();
	
    private static final RandomSource RANDOM = RandomSource.create();
	
	public static RecipeHolder<HighPressureRefineryRecipe> findRecipe(@Nonnull FluidStack input, @Nonnull FluidStack secondary){
		Objects.requireNonNull(input);
		Objects.requireNonNull(secondary);
		
		for(RecipeHolder<HighPressureRefineryRecipe> holder:recipes.values()){
			final HighPressureRefineryRecipe recipe = holder.value();
			
			if(secondary.isEmpty()){
				if(recipe.inputFluidSecondary == null && (recipe.inputFluid != null && recipe.inputFluid.test(input))){
					return holder;
				}
			}else{
				if((recipe.inputFluid != null && recipe.inputFluid.test(input)) && (recipe.inputFluidSecondary != null && recipe.inputFluidSecondary.test(secondary))){
					return holder;
				}
			}
		}
		return null;
	}
	
	public static boolean hasRecipeWithInput(@Nonnull FluidStack fluid, boolean ignoreAmount){
		Objects.requireNonNull(fluid);
		
		if(!fluid.isEmpty()){
			for(RecipeHolder<HighPressureRefineryRecipe> holder:recipes.values()){
				final HighPressureRefineryRecipe recipe = holder.value();
				
				if(recipe.inputFluid != null){
					if((!ignoreAmount && recipe.inputFluid.test(fluid)) || (ignoreAmount && recipe.inputFluid.testIgnoringAmount(fluid))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean hasRecipeWithSecondaryInput(@Nonnull FluidStack fluid, boolean ignoreAmount){
		Objects.requireNonNull(fluid);
		
		if(!fluid.isEmpty()){
			for(RecipeHolder<HighPressureRefineryRecipe> holder:recipes.values()){
				final HighPressureRefineryRecipe recipe = holder.value();
				
				if(recipe.inputFluidSecondary != null){
					if((!ignoreAmount && recipe.inputFluidSecondary.test(fluid)) || (ignoreAmount && recipe.inputFluidSecondary.testIgnoringAmount(fluid))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static final RecipeMultiplier MULTIPLIER = new RecipeMultiplier(IPServerConfig.REFINING.hydrotreater_timeModifier::get, IPServerConfig.REFINING.hydrotreater_energyModifier::get);
	private static RecipeMultiplier multipliers(){
		return MULTIPLIER;
	}
	
	
	public final ChancedItemStack outputItem;
	
	public final FluidStack output;
	
	public final FluidTagInput inputFluid;
	@Nullable
	public final FluidTagInput inputFluidSecondary;
	
	/**
	 * @param output              {@link FluidStack} to output
	 * @param outputItem          {@link ChancedItemStack} to output
	 * @param inputFluid          {@link FluidStack} to input
	 * @param inputFluidSecondary {@link FluidStack} for secondary input
	 * @param energy              amount of FE to consume
	 * @param time                duration of the recipe
	 */
	public HighPressureRefineryRecipe(FluidStack output, ChancedItemStack outputItem, FluidTagInput inputFluid, @Nullable FluidTagInput inputFluidSecondary, int energy, int time){
		super(TagOutput.EMPTY, IPRecipeTypes.HYDROTREATER, energy, time, HighPressureRefineryRecipe::multipliers);
		this.output = output;
		this.outputItem = outputItem;
		this.inputFluid = inputFluid;
		this.inputFluidSecondary = inputFluidSecondary;
		
		this.fluidOutputList = Collections.singletonList(output);
		this.fluidInputList = Arrays.asList(inputFluidSecondary != null ? new FluidTagInput[]{inputFluid, inputFluidSecondary} : new FluidTagInput[]{inputFluid});
	}
	
	public boolean hasSecondaryItem(){
		return !this.outputItem.stack().isEmpty();
	}
	
	@Override
	public int getMultipleProcessTicks(){
		return 0;
	}
	
	public FluidTagInput getInputFluid(){
		return this.inputFluid;
	}
	
	@Nullable
	public FluidTagInput getSecondaryInputFluid(){
		return this.inputFluidSecondary;
	}
	
	@Override
	public NonNullList<ItemStack> getActualItemOutputs(){
		NonNullList<ItemStack> list = NonNullList.create();
		if(RANDOM.nextFloat() <= this.outputItem.chance()){
			list.add(this.outputItem.stack());
		}
		return list;
	}
	
	@Override
	protected IERecipeSerializer<HighPressureRefineryRecipe> getIESerializer(){
		return Serializers.HYDROTREATER_SERIALIZER.get();
	}
}
