package flaxbeard.immersivepetroleum.common.data;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import blusunrize.immersiveengineering.common.util.loot.DropInventoryLootEntry;
import blusunrize.immersiveengineering.common.util.loot.MBOriginalBlockLootEntry;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.loot.IPTileDropLootEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;

public class IPBlockLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>{
	private BiConsumer<ResourceLocation, LootTable.Builder> out;

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> out){
		this.out = out;
		registerSelfDropping(IPContent.Blocks.ASPHALT.get());
		registerSelfDropping(IPContent.Blocks.ASPHALT_SLAB.get());
		registerSelfDropping(IPContent.Blocks.ASPHALT_STAIR.get());
		registerSelfDropping(IPContent.Blocks.FLARESTACK.get());
		
		register(IPContent.Blocks.GAS_GENERATOR.get(), tileDrop());
		register(IPContent.Blocks.AUTO_LUBRICATOR.get(), tileDrop());
		
		registerMultiblock(IPContent.Multiblock.DISTILLATIONTOWER.get());
		registerMultiblock(IPContent.Multiblock.PUMPJACK.get());
		registerMultiblock(IPContent.Multiblock.HYDROTREATER.get());
	}
	
	private void registerMultiblock(Block b){
		register(b, dropInv(), dropOriginalBlock());
	}
	
	private LootPool.Builder dropOriginalBlock(){
		return createPoolBuilder().add(MBOriginalBlockLootEntry.builder());
	}
	
	private LootPool.Builder dropInv(){
		return createPoolBuilder().add(DropInventoryLootEntry.builder());
	}
	
	private LootPool.Builder tileDrop(){
		return createPoolBuilder().add(IPTileDropLootEntry.builder());
	}
	
	private void registerSelfDropping(Block b, LootPool.Builder... pool){
		LootPool.Builder[] withSelf = Arrays.copyOf(pool, pool.length + 1);
		withSelf[withSelf.length - 1] = singleItem(b);
		register(b, withSelf);
	}

	private LootPool.Builder singleItem(ItemLike in)
	{
		return createPoolBuilder()
				.setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(in));
	}

	private void register(Block b, LootPool.Builder... pools){
		LootTable.Builder builder = LootTable.lootTable();
		for(LootPool.Builder pool:pools)
			builder.withPool(pool);
		register(b, builder);
	}
	
	private void register(Block b, LootTable.Builder table){
		register(b.getRegistryName(), table);
	}
	
	private void register(ResourceLocation name, LootTable.Builder table){
		out.accept(toTableLoc(name), table);
	}
	
	private LootPool.Builder createPoolBuilder(){
		return LootPool.lootPool().when(ExplosionCondition.survivesExplosion());
	}
	
	private ResourceLocation toTableLoc(ResourceLocation in){
		return new ResourceLocation(in.getNamespace(), "blocks/" + in.getPath());
	}
}
