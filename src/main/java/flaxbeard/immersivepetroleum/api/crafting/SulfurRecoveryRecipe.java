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
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class SulfurRecoveryRecipe extends IPMultiblockRecipe{

	public static Map<ResourceLocation, SulfurRecoveryRecipe> recipes = new HashMap<>();
	
	public static SulfurRecoveryRecipe findRecipe(@Nonnull FluidStack input, @Nonnull FluidStack secondary){
		Objects.requireNonNull(input);
		Objects.requireNonNull(secondary);
		
		for(SulfurRecoveryRecipe recipe:recipes.values()){
			if(secondary.isEmpty()){
				if(recipe.inputFluidSecondary == null && (recipe.inputFluid != null && recipe.inputFluid.test(input))){
					return recipe;
				}
			}else{
				if((recipe.inputFluid != null && recipe.inputFluid.test(input)) && (recipe.inputFluidSecondary != null && recipe.inputFluidSecondary.test(secondary))){
					return recipe;
				}
			}
		}
		return null;
	}
	
	public static boolean hasRecipeWithInput(@Nonnull FluidStack fluid, boolean ignoreAmount){
		Objects.requireNonNull(fluid);
		
		if(!fluid.isEmpty()){
			for(SulfurRecoveryRecipe recipe:recipes.values()){
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
			for(SulfurRecoveryRecipe recipe:recipes.values()){
				if(recipe.inputFluidSecondary != null){
					if((!ignoreAmount && recipe.inputFluidSecondary.test(fluid)) || (ignoreAmount && recipe.inputFluidSecondary.testIgnoringAmount(fluid))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public final ItemStack outputItem;
	public final double chance;
	
	public final FluidStack output;
	
	public final FluidTagInput inputFluid;
	@Nullable
	public final FluidTagInput inputFluidSecondary;
	
	/**
	 * 
	 * @param id {@link ResourceLocation} ID to create the recipe with
	 * @param output {@link FluidStack} to output
	 * @param outputItem {@link ItemStack} to output
	 * @param inputFluid {@link FluidStack} to input
	 * @param inputFluidSecondary {@link FluidStack} for secondary input
	 * @param chance double chance of the {@link ItemStack} output
	 * @param energy amount of FE to consume
	 * @param time duration of the recipe
	 */
	public SulfurRecoveryRecipe(ResourceLocation id, FluidStack output, ItemStack outputItem, FluidTagInput inputFluid, @Nullable FluidTagInput inputFluidSecondary, double chance, int energy, int time){
		super(ItemStack.EMPTY, IPRecipeTypes.SULFUR_RECOVERY, id);
		this.output = output;
		this.outputItem = outputItem;
		this.inputFluid = inputFluid;
		this.inputFluidSecondary = inputFluidSecondary;
		this.chance = chance;
		
		this.fluidOutputList = Collections.singletonList(output);
		this.fluidInputList = Arrays.asList(inputFluidSecondary != null ? new FluidTagInput[]{inputFluid, inputFluidSecondary} : new FluidTagInput[]{inputFluid});
		
		timeAndEnergy(time, energy);
		modifyTimeAndEnergy(IPServerConfig.REFINING.hydrotreater_timeModifier::get, IPServerConfig.REFINING.hydrotreater_energyModifier::get);
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
	public NonNullList<ItemStack> getActualItemOutputs(BlockEntity tile){
		NonNullList<ItemStack> list = NonNullList.create();
		Level level = tile.getLevel();
		if(level.random.nextFloat() <= chance){
			list.add(this.outputItem);
		}
		return list;
	}
	
	@Override
	protected IERecipeSerializer<SulfurRecoveryRecipe> getIESerializer(){
		return Serializers.HYDROTREATER_SERIALIZER.get();
	}
}
