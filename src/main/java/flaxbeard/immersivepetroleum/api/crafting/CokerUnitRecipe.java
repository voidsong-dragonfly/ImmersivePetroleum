package flaxbeard.immersivepetroleum.api.crafting;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;

public class CokerUnitRecipe extends MultiblockRecipe{
	public static Map<ResourceLocation, RecipeHolder<CokerUnitRecipe>> recipes = new HashMap<>();
	
	public static RecipeHolder<CokerUnitRecipe> findRecipe(ItemStack stack, FluidStack fluid){
		for(RecipeHolder<CokerUnitRecipe> holder:recipes.values()){
			final CokerUnitRecipe recipe = holder.value();
			
			if((recipe.inputItem != null && recipe.inputItem.test(stack)) && (recipe.inputFluid != null && recipe.inputFluid.test(fluid))){
				return holder;
			}
		}
		
		return null;
	}
	
	public static boolean hasRecipeWithInput(@Nonnull ItemStack stack, @Nonnull FluidStack fluid){
		Objects.requireNonNull(stack);
		Objects.requireNonNull(fluid);
		
		if(!stack.isEmpty() && !fluid.isEmpty()){
			for(RecipeHolder<CokerUnitRecipe> holder:recipes.values()){
				final CokerUnitRecipe recipe = holder.value();
				
				if(recipe.inputItem != null && recipe.inputFluid != null && recipe.inputItem.test(stack) && recipe.inputFluid.test(fluid)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean hasRecipeWithInput(@Nonnull ItemStack stack, boolean ignoreSize){
		Objects.requireNonNull(stack);
		
		if(!stack.isEmpty()){
			for(RecipeHolder<CokerUnitRecipe> holder:recipes.values()){
				final CokerUnitRecipe recipe = holder.value();
				
				if(recipe.inputItem != null){
					if((!ignoreSize && recipe.inputItem.test(stack)) || (ignoreSize && recipe.inputItem.testIgnoringSize(stack))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean hasRecipeWithInput(@Nonnull FluidStack fluid, boolean ignoreAmount){
		Objects.requireNonNull(fluid);
		
		if(!fluid.isEmpty()){
			for(RecipeHolder<CokerUnitRecipe> holder:recipes.values()){
				final CokerUnitRecipe recipe = holder.value();
				
				if(recipe.inputFluid != null){
					if((!ignoreAmount && recipe.inputFluid.test(fluid)) || (ignoreAmount && recipe.inputFluid.testIgnoringAmount(fluid))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static final RecipeMultiplier MULTIPLIER = new RecipeMultiplier(IPServerConfig.REFINING.cokerUnit_timeModifier::get, IPServerConfig.REFINING.cokerUnit_energyModifier::get);
	private static RecipeMultiplier multipliers(){
		return MULTIPLIER;
	}
	
	// just a "Reference"
	// Water Input -> FluidIn
	// Bitumen Input -> Item In
	// Coke Output -> Item Out
	// Diesel Output -> Fluid Out
	
	public final Lazy<ItemStack> outputItem;
	public final FluidStack outputFluid;
	
	public final IngredientWithSize inputItem;
	public final FluidTagInput inputFluid;
	
	public CokerUnitRecipe(ResourceLocation id, Lazy<ItemStack> outputItem2, FluidStack outputFluid, IngredientWithSize inputItem, FluidTagInput inputFluid, int energy, int time){
		super(TagOutput.EMPTY, IPRecipeTypes.COKER, energy, time, CokerUnitRecipe::multipliers);
		this.inputFluid = inputFluid;
		this.inputItem = inputItem;
		this.outputFluid = outputFluid;
		this.outputItem = outputItem2;
	}
	
	@Override
	public int getMultipleProcessTicks(){
		return 0;
	}
	
	@Override
	public NonNullList<ItemStack> getActualItemOutputs(){
		NonNullList<ItemStack> list = NonNullList.create();
		list.add(this.outputItem.get());
		return list;
	}
	
	@Override
	protected IERecipeSerializer<CokerUnitRecipe> getIESerializer(){
		return Serializers.COKER_SERIALIZER.get();
	}
}
