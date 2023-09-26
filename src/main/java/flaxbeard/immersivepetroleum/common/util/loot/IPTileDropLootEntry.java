package flaxbeard.immersivepetroleum.common.util.loot;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import flaxbeard.immersivepetroleum.common.blocks.interfaces.IBlockEntityDrop;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class IPTileDropLootEntry extends LootPoolSingletonContainer{
	public static final ResourceLocation ID = ResourceUtils.ip("tile_drop");
	
	protected IPTileDropLootEntry(int weightIn, int qualityIn, LootItemCondition[] conditionsIn, LootItemFunction[] functionsIn){
		super(weightIn, qualityIn, conditionsIn, functionsIn);
	}
	
	@Override
	protected void createItemStack(@Nonnull Consumer<ItemStack> stackConsumer, LootContext context){
		if(context.hasParam(LootContextParams.BLOCK_ENTITY)){
			BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
			if(te instanceof IBlockEntityDrop dropTe){
				dropTe.getBlockEntityDrop(context).forEach(stackConsumer);
			}
		}
	}
	
	@Override
	@Nonnull
	public LootPoolEntryType getType(){
		return IPLootFunctions.TILE_DROP.get();
	}
	
	public static LootPoolSingletonContainer.Builder<?> builder(){
		return simpleBuilder(IPTileDropLootEntry::new);
	}
	
	public static class Serializer extends LootPoolSingletonContainer.Serializer<IPTileDropLootEntry>{
		@Nonnull
		@Override
		protected IPTileDropLootEntry deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, int weight, int quality, @Nonnull LootItemCondition[] conditions, @Nonnull LootItemFunction[] functions){
			return new IPTileDropLootEntry(weight, quality, conditions, functions);
		}
	}
}
