package flaxbeard.immersivepetroleum.common.crafting.serializers;

import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.api.crafting.SulfurRecoveryRecipe;
import flaxbeard.immersivepetroleum.api.crafting.builders.DistillationRecipeBuilder;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.fluids.FluidStack;
import javax.annotation.Nonnull;

public class SulfurRecoveryRecipeSerializer extends IERecipeSerializer<SulfurRecoveryRecipe>{
	
	@Override
	public SulfurRecoveryRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context){
		FluidStack output = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "result"));
		FluidTagInput inputFluid0 = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "input"));
		FluidTagInput inputFluid1 = null;
		
		if(json.has("secondary_input")){
			inputFluid1 = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "secondary_input"));
		}
		
		Tuple<ItemStack, Double> itemWithChance = DistillationRecipeBuilder.deserializeItemStackWithChance(json.get("secondary_result").getAsJsonObject());
		
		int energy = GsonHelper.getAsInt(json, "energy");
		int time = GsonHelper.getAsInt(json, "time");
		
		return new SulfurRecoveryRecipe(recipeId, output, itemWithChance.getA(), inputFluid0, inputFluid1, itemWithChance.getB(), energy, time);
	}
	
	@Override
	public SulfurRecoveryRecipe fromNetwork(@Nonnull ResourceLocation id, FriendlyByteBuf buffer){
		ItemStack outputItem = buffer.readItem();
		double chance = buffer.readDouble();
		
		FluidStack output = buffer.readFluidStack();
		FluidTagInput inputFluid0 = FluidTagInput.read(buffer);
		FluidTagInput inputFluid1 = FluidTagInput.read(buffer);
		
		int energy = buffer.readInt();
		int time = buffer.readInt();
		
		return new SulfurRecoveryRecipe(id, output, outputItem, inputFluid0, inputFluid1, chance, energy, time);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buffer, SulfurRecoveryRecipe recipe){
		buffer.writeItem(recipe.outputItem);
		buffer.writeDouble(recipe.chance);
		
		buffer.writeFluidStack(recipe.output);
		recipe.inputFluid.write(buffer);
		recipe.inputFluidSecondary.write(buffer);
		
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
	
	@Override
	public ItemStack getIcon(){
		return new ItemStack(IPContent.Multiblock.HYDROTREATER.get());
	}
}
