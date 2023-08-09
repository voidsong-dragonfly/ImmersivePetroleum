package flaxbeard.immersivepetroleum.api.crafting.builders;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Distillation Recipe creation using DataGeneration
 * 
 * @author TwistedGate
 */
public class DistillationTowerRecipeBuilder extends IEFinishedRecipe<DistillationTowerRecipeBuilder>{
	
	public static DistillationTowerRecipeBuilder builder(FluidStack... fluidOutput){
		if(fluidOutput == null || fluidOutput.length == 0)
			throw new IllegalArgumentException("Fluid output missing. It's required.");
		
		DistillationTowerRecipeBuilder b = new DistillationTowerRecipeBuilder();
		b.addFluids("results", fluidOutput);
		return b;
	}
	
	/** Temporary storage for byproducts */
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
	 */
	public DistillationTowerRecipeBuilder addByproduct(ItemStack byproduct, int chance){
		return addByproduct(byproduct, chance / 100D);
	}
	
	/**
	 * Can be called multiple times to add more byproducts to the recipe. Or never to not have any byproducts.
	 * 
	 * @param byproduct {@link ItemStack} to output as byproduct
	 * @param chance    0.0 to 1.0 (clamped)
	 * @return {@link DistillationTowerRecipeBuilder} self for chaining
	 */
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
	}
}
