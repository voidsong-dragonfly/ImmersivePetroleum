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

	/*
	public static DistillationTowerRecipeBuilder builder(FluidStack... fluidOutput){
		if(fluidOutput == null || fluidOutput.length == 0)
			throw new IllegalArgumentException("Fluid output missing. It's required.");
		
		DistillationTowerRecipeBuilder b = new DistillationTowerRecipeBuilder();
		b.addFluids("results", fluidOutput);
		return b;
	}
	
	/** Temporary storage for byproducts
	private final List<Tuple<ItemStack, Double>> byproducts = new ArrayList<>();
	
	private DistillationTowerRecipeBuilder(){
		super(Serializers.DISTILLATION_SERIALIZER.get());
		addWriter(jsonObject -> {
			if(this.byproducts.size() > 0){
				final JsonArray main = new JsonArray();
				this.byproducts.forEach(by -> main.add(serializerItemStackWithChance(by)));
				jsonObject.add("byproducts", main);
				this.byproducts.clear();
			}
		});
	}
	
	/**
	 * Can be called multiple times to add more byproducts to the recipe
	 * 
	 * @param byproduct the {@link ItemStack} byproduct to add to the recipe
	 * @param chance    0 to 100 (clamped)
	 * @return self for chaining

	public DistillationTowerRecipeBuilder addByproduct(ItemStack byproduct, int chance){
		return addByproduct(byproduct, chance / 100D);
	}
	
	/**
	 * Can be called multiple times to add more byproducts to the recipe. Or never to not have any byproducts.
	 * 
	 * @param byproduct {@link ItemStack} to output as byproduct
	 * @param chance    0.0 to 1.0 (clamped)
	 * @return {@link DistillationTowerRecipeBuilder} self for chaining

	public DistillationTowerRecipeBuilder addByproduct(ItemStack byproduct, double chance){
		this.byproducts.add(new Tuple<>(byproduct, Mth.clamp(chance, 0.0, 1.0)));
		return this;
	}
	
	public DistillationTowerRecipeBuilder setTimeAndEnergy(int time, int energy){
		return setTime(time).setEnergy(energy);
	}
	
	public DistillationTowerRecipeBuilder addInput(TagKey<Fluid> fluidTag, int amount){
		return addFluidTag("input", fluidTag, amount);
	}
	
	public DistillationTowerRecipeBuilder addInput(Fluid fluid, int amount){
		return addInput(new FluidStack(fluid, amount));
	}
	
	public DistillationTowerRecipeBuilder addInput(FluidStack fluidStack){
		return addFluid("input", fluidStack);
	}
	
	public DistillationTowerRecipeBuilder addFluids(String key, FluidStack... fluidStacks){
		return addWriter(jsonObject -> {
			JsonArray array = new JsonArray();
			for(FluidStack stack:fluidStacks)
				array.add(ApiUtils.jsonSerializeFluidStack(stack));
			jsonObject.add(key, array);
		});
	}
	
	public DistillationTowerRecipeBuilder addItems(String key, ItemStack... itemStacks){
		return addWriter(jsonObject -> {
			JsonArray array = new JsonArray();
			for(ItemStack stack:itemStacks){
				array.add(serializeItemStack(stack));
			}
			jsonObject.add(key, array);
		});
	}
	
	public static Tuple<ItemStack, Double> deserializeItemStackWithChance(JsonObject jsonObject){
		if(jsonObject.has("item")){
			double chance = 1.0;
			if(jsonObject.has("chance")){
				chance = jsonObject.get("chance").getAsDouble();
			}
			ItemStack stack = ShapedRecipe.itemStackFromJson(jsonObject);
			return new Tuple<>(stack, chance);
		}
		
		throw new IllegalArgumentException("Unexpected json object.");
	}
	
	private static final DistillationTowerRecipeBuilder dummy = new DistillationTowerRecipeBuilder();
	public static JsonObject serializerItemStackWithChance(@Nonnull Tuple<ItemStack, Double> tuple){
		JsonObject itemJson = dummy.serializeItemStack(tuple.getA());
		itemJson.addProperty("chance", tuple.getB().toString());
		return itemJson;
	}*/
}
