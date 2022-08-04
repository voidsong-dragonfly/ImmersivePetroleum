package flaxbeard.immersivepetroleum.common.crafting.serializers;

import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import javax.annotation.Nonnull;

public class CokerUnitRecipeSerializer extends IERecipeSerializer<CokerUnitRecipe>{
	
	@Override
	public CokerUnitRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context){
		FluidTagInput outputFluid = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "resultfluid"));
		FluidTagInput inputFluid = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "inputfluid"));
		
		Lazy<ItemStack> outputItem = readOutput(json.get("result"));
		IngredientWithSize inputItem = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"));
		
		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");
		
		return new CokerUnitRecipe(recipeId, outputItem, outputFluid, inputItem, inputFluid, energy, time);
	}
	
	@Override
	public CokerUnitRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer){
		IngredientWithSize inputItem = IngredientWithSize.read(buffer);
		ItemStack outputItem = buffer.readItem();
		
		FluidTagInput inputFluid = FluidTagInput.read(buffer);
		FluidTagInput outputFluid = FluidTagInput.read(buffer);
		
		int energy = buffer.readInt();
		int time = buffer.readInt();
		
		return new CokerUnitRecipe(recipeId, Lazy.of(() -> outputItem), outputFluid, inputItem, inputFluid, energy, time);
	}
	
	@Override
	public void toNetwork(@Nonnull FriendlyByteBuf buffer, CokerUnitRecipe recipe){
		recipe.inputItem.write(buffer);
		buffer.writeItem(recipe.outputItem.get());
		
		recipe.inputFluid.write(buffer);
		recipe.outputFluid.write(buffer);
		
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
	
	@Override
	public ItemStack getIcon(){
		return new ItemStack(IPContent.Multiblock.COKERUNIT.get());
	}
}
