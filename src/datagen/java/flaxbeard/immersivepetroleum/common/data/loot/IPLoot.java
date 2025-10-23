package flaxbeard.immersivepetroleum.common.data.loot;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class IPLoot implements LootTableSubProvider{
	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> pOutput){
		LootPool.Builder pool = LootPool.lootPool()
				.name("cookie_for_your_trouble")
				.add(LootItem.lootTableItem(Items.COOKIE))
				.setRolls(ConstantValue.exactly(1))
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)));

		pOutput.accept(ResourceUtils.ip("advancements/forming_coker_reward"), LootTable.lootTable().withPool(pool));
	}
}
