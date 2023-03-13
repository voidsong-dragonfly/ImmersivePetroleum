package flaxbeard.immersivepetroleum.common.crafting.serializers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;

public class ReservoirSerializer extends IERecipeSerializer<ReservoirType>{
	@Override
	public ReservoirType readFromJson(ResourceLocation recipeId, JsonObject json, IContext context){
		String name = GsonHelper.getAsString(json, "name");
		ResourceLocation fluid = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
		int min = GsonHelper.getAsInt(json, "fluidminimum");
		int max = GsonHelper.getAsInt(json, "fluidcapacity");
		int trace = GsonHelper.getAsInt(json, "fluidtrace");
		int equilibrium = GsonHelper.getAsInt(json, "equilibrium", 0);
		int weight = GsonHelper.getAsInt(json, "weight");
		
		ReservoirType reservoir = new ReservoirType(name, recipeId, fluid, min, max, trace, equilibrium, weight);
		
		ImmersivePetroleum.log.debug("Loaded reservoir {} as {}, with {}mB to {}mB of {} and {}mB trace at {}mB equilibrium, with {} of weight.",
				recipeId, name, min, max, fluid, trace, equilibrium, weight);
		
		if(GsonHelper.isValidNode(json, "dimensions")){
			JsonObject dimensions = GsonHelper.getAsJsonObject(json, "dimensions");
			
			boolean isBlacklist = GsonHelper.getAsBoolean(dimensions, "isBlacklist");
			
			if(GsonHelper.isValidNode(dimensions, "list")){
				JsonArray array = GsonHelper.getAsJsonArray(dimensions, "list");
				
				List<ResourceLocation> list = new ArrayList<>();
				array.forEach(rl -> list.add(new ResourceLocation(rl.getAsString())));
				reservoir.setDimensions(isBlacklist, list);
			}
		}
		
		if(GsonHelper.isValidNode(json, "biomes")){
			JsonObject biomes = GsonHelper.getAsJsonObject(json, "biomes");
			
			boolean isBlacklist = GsonHelper.getAsBoolean(biomes, "isBlacklist");
			
			if(GsonHelper.isValidNode(biomes, "list")){
				JsonArray array = GsonHelper.getAsJsonArray(biomes, "list");
				
				List<ResourceLocation> list = new ArrayList<>();
				array.forEach(rl -> list.add(new ResourceLocation(rl.getAsString())));
				reservoir.setBiomes(isBlacklist, list);
			}
		}
		
		return reservoir;
	}
	
	@Override
	public ReservoirType fromNetwork(@Nonnull ResourceLocation recipeId, FriendlyByteBuf buffer){
		return new ReservoirType(buffer.readNbt()); // Very convenient having the NBT stuff already.
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buffer, ReservoirType recipe){
		buffer.writeNbt(recipe.writeToNBT());
	}
	
	@Override
	public ItemStack getIcon(){
		return ItemStack.EMPTY;
	}
}
