package flaxbeard.immersivepetroleum.api.crafting;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;

public class DistillationTowerRecipe extends MultiblockRecipe{
	public static Map<ResourceLocation, RecipeHolder<DistillationTowerRecipe>> recipes = new HashMap<>();
	
    private static final RandomSource RANDOM = RandomSource.create();
	
	/** May return null! */
	@Nullable
	public static RecipeHolder<DistillationTowerRecipe> findRecipe(FluidStack input){
		if(!recipes.isEmpty()){
			for(RecipeHolder<DistillationTowerRecipe> holder:recipes.values()){
				final DistillationTowerRecipe recipe = holder.value();
				
				if(recipe.input != null && recipe.input.testIgnoringAmount(input)){
					return holder;
				}
			}
		}
		return null;
	}
	
	@Nullable
	public static RecipeHolder<DistillationTowerRecipe> getRecipe(ResourceLocation id){
		return recipes.get(id);
	}
	
	@Nullable
	public static RecipeHolder<DistillationTowerRecipe> loadFromNBT(CompoundTag nbt){
		FluidStack input = FluidStack.loadFluidStackFromNBT(nbt.getCompound("input"));
		return findRecipe(input);
	}
	
	private static final RecipeMultiplier MULTIPLIER = new RecipeMultiplier(IPServerConfig.REFINING.distillationTower_timeModifier::get, IPServerConfig.REFINING.distillationTower_energyModifier::get);
	private static RecipeMultiplier multipliers(){
		return MULTIPLIER;
	}
	
	protected final Lazy<NonNullList<ItemStack>> lazyOutputList;
	protected final FluidTagInput input;
	protected final FluidStack[] fluidOutput;
	@Deprecated(forRemoval = true) protected ItemStack[] itemOutput;
	@Deprecated(forRemoval = true) protected double[] chances;
	protected final ChancedItemStack[] itemOutputs;
	
	public DistillationTowerRecipe(FluidStack[] fluidOutput, ChancedItemStack[] itemOutput, FluidTagInput input, int energy, int time){
		super(TagOutput.EMPTY, IPRecipeTypes.DISTILLATION, energy, time, DistillationTowerRecipe::multipliers);
		this.fluidOutput = fluidOutput;
		this.itemOutputs = itemOutput;
		
		this.input = input;
		this.fluidInputList = Collections.singletonList(input);
		this.fluidOutputList = Arrays.asList(this.fluidOutput);
		this.lazyOutputList = Lazy.of(() -> {
			NonNullList<ItemStack> list = Arrays.asList(itemOutput).stream()
				.map(m -> m.stack())
				.collect(() -> NonNullList.withSize(itemOutput.length, ItemStack.EMPTY), NonNullList::add, NonNullList::addAll);
			return list;
		});
	}
	
	@Override
	protected IERecipeSerializer<DistillationTowerRecipe> getIESerializer(){
		return Serializers.DISTILLATION_SERIALIZER.get();
	}
	
	@Override
	public int getMultipleProcessTicks(){
		return 0;
	}
	
	@Override
	public NonNullList<ItemStack> getItemOutputs(){
		return this.lazyOutputList.get();
	}
	
	@Override
	public NonNullList<ItemStack> getActualItemOutputs(){
		if(this.itemOutputs.length == 0)
			return NonNullList.withSize(0, ItemStack.EMPTY);
		
		NonNullList<ItemStack> output = NonNullList.create();
		for(int i = 0;i < this.itemOutputs.length;i++){
			if(RANDOM.nextFloat() <= this.itemOutputs[i].chance()){
				output.add(this.itemOutputs[i].stack());
			}
		}
		
		return output;
	}
	
	public FluidTagInput getInputFluid(){
		return this.input;
	}
	
	@Deprecated
	public double[] chances(){
		throw new UnsupportedOperationException();
	}
	
	public ChancedItemStack[] getByproducts(){
		return this.itemOutputs;
	}
}
