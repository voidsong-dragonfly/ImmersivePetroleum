package flaxbeard.immersivepetroleum.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record ChancedItemStack(@Nonnull ItemStack stack, double chance){
	public static final Codec<ChancedItemStack> CODEC = RecordCodecBuilder.create(inst -> inst.group(
		ItemStack.CODEC.fieldOf("item").forGetter(r -> r.stack()),
		Codec.DOUBLE.optionalFieldOf("chance", 1.0D).forGetter(r -> r.chance())
	).apply(inst, ChancedItemStack::new));
	
	public static final ChancedItemStack EMPTY = new ChancedItemStack(ItemStack.EMPTY, 1.0D);
	
	public static ChancedItemStack[] readArrayFromBuffer(FriendlyByteBuf buffer){
		int size = buffer.readInt();
		
		ChancedItemStack[] array = new ChancedItemStack[size];
		for(int i = 0;i < size;i++){
			ChancedItemStack chancedItemStack = new ChancedItemStack(buffer);
			array[i] = chancedItemStack;
		}
		
		return array;
	}
	
	public static void writeArrayToBuffer(@Nullable ChancedItemStack[] array, FriendlyByteBuf buffer){
		if(array == null || (array != null && array.length == 0)){
			buffer.writeInt(0);
			return;
		}
		
		buffer.writeInt(array.length);
		for(int i = 0;i < array.length;i++){
			array[i].writeToBuffer(buffer);
		}
	}
	
	public ChancedItemStack(FriendlyByteBuf buffer){
		this(buffer.readItem(), buffer.readDouble());
	}
	
	public void writeToBuffer(FriendlyByteBuf buffer){
		buffer.writeItem(this.stack);
		buffer.writeDouble(this.chance);
	}
	
	@Override
	@Nonnull
	public ItemStack stack(){
		return this.stack;
	}
}