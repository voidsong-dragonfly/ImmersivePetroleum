package flaxbeard.immersivepetroleum.common.crafting.serializers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class ReservoirSerializer extends IERecipeSerializer<Reservoir>{
	@Override
	public Reservoir readFromJson(ResourceLocation recipeId, JsonObject json){
		String name = GsonHelper.getAsString(json, "name");
		ResourceLocation fluid = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
		int min = GsonHelper.getAsInt(json, "fluidminimum");
		int max = GsonHelper.getAsInt(json, "fluidcapacity");
		int trace = GsonHelper.getAsInt(json, "fluidtrace");
		int weight = GsonHelper.getAsInt(json, "weight");
		
		Reservoir reservoir = new Reservoir(name, recipeId, fluid, min, max, trace, weight);
		
		ImmersivePetroleum.log.debug(String.format("Loaded reservoir %s as %s, with %smB to %smB of %s and %smB trace, with %s of weight.",
				recipeId, name, min, max, fluid, trace, weight));
		
		if(GsonHelper.isValidNode(json, "dimension")){
			JsonObject dimension = GsonHelper.getAsJsonObject(json, "dimension");
			
			List<ResourceLocation> whitelist = new ArrayList<>();
			List<ResourceLocation> blacklist = new ArrayList<>();
			
			if(GsonHelper.isValidNode(dimension, "whitelist")){
				JsonArray array = GsonHelper.getAsJsonArray(dimension, "whitelist");
				for(JsonElement obj:array){
					whitelist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(GsonHelper.isValidNode(dimension, "blacklist")){
				JsonArray array = GsonHelper.getAsJsonArray(dimension, "blacklist");
				for(JsonElement obj:array){
					blacklist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(whitelist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to dimension-whitelist for {} -", name);
				whitelist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addDimension(false, whitelist);
			}else if(blacklist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to dimension-blacklist for {} -", name);
				blacklist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addDimension(true, blacklist);
			}
		}
		
		if(GsonHelper.isValidNode(json, "biome")){
			JsonObject biome = GsonHelper.getAsJsonObject(json, "biome");
			
			List<ResourceLocation> whitelist = new ArrayList<>();
			List<ResourceLocation> blacklist = new ArrayList<>();
			
			if(GsonHelper.isValidNode(biome, "whitelist")){
				JsonArray array = GsonHelper.getAsJsonArray(biome, "whitelist");
				for(JsonElement obj:array){
					whitelist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(GsonHelper.isValidNode(biome, "blacklist")){
				JsonArray array = GsonHelper.getAsJsonArray(biome, "blacklist");
				for(JsonElement obj:array){
					blacklist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(whitelist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to biome-whitelist for {} -", name);
				whitelist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addBiome(false, whitelist);
			}else if(blacklist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to biome-blacklist for {} -", name);
				blacklist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addBiome(true, blacklist);
			}
		}
		
		return reservoir;
	}
	
	@Override
	public Reservoir fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
		return new Reservoir(buffer.readNbt()); // Very convenient having the NBT stuff already.
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buffer, Reservoir recipe){
		buffer.writeNbt(recipe.writeToNBT());
	}
	
	@Override
	public ItemStack getIcon(){
		return ItemStack.EMPTY;
	}
}
