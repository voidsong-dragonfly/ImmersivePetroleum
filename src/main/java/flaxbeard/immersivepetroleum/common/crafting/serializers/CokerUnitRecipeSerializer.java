package flaxbeard.immersivepetroleum.common.crafting.serializers;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class CokerUnitRecipeSerializer extends IERecipeSerializer<CokerUnitRecipe>{
	
	// @formatter:off
	public static final Codec<CokerUnitRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		ItemStack.CODEC.fieldOf("result").forGetter(r -> r.outputItem),
		FluidStack.CODEC.fieldOf("resultfluid").forGetter(r -> r.outputFluid),
		IngredientWithSize.CODEC.fieldOf("input").forGetter(r -> r.inputItem),
		FluidTagInput.CODEC.fieldOf("inputfluid").forGetter(r -> r.inputFluid),
		Codec.INT.fieldOf("energy").forGetter(r -> r.getBaseEnergy()),
		Codec.INT.fieldOf("time").forGetter(r -> r.getBaseTime())
	).apply(inst, (outputItem, outputFluid, inputItem, inputFluid, energy, time) -> {
		return new CokerUnitRecipe(outputItem, outputFluid, inputItem, inputFluid, energy, time);
	}));
	// @formatter:on
	
	@Override
	public Codec<CokerUnitRecipe> codec(){
		return CODEC;
	}
	
	@Override
	public CokerUnitRecipe fromNetwork(@Nonnull FriendlyByteBuf buffer){
		IngredientWithSize inputItem = IngredientWithSize.read(buffer);
		ItemStack outputItem = buffer.readItem();
		
		FluidTagInput inputFluid = FluidTagInput.read(buffer);
		FluidStack outputFluid = FluidStack.readFromPacket(buffer);
		
		int energy = buffer.readInt();
		int time = buffer.readInt();
		
		return new CokerUnitRecipe(outputItem, outputFluid, inputItem, inputFluid, energy, time);
	}
	
	@Override
	public void toNetwork(@Nonnull FriendlyByteBuf buffer, CokerUnitRecipe recipe){
		recipe.inputItem.write(buffer);
		buffer.writeItem(recipe.outputItem);
		
		recipe.inputFluid.write(buffer);
		recipe.outputFluid.writeToPacket(buffer);
		
		buffer.writeInt(recipe.getTotalProcessEnergy());
		buffer.writeInt(recipe.getTotalProcessTime());
	}
	
	@Override
	public ItemStack getIcon(){
		return IPContent.Multiblock.COKERUNIT.iconStack();
	}
}
