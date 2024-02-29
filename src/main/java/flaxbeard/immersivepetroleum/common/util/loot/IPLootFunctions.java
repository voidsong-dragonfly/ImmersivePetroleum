package flaxbeard.immersivepetroleum.common.util.loot;

import com.mojang.serialization.Codec;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IPLootFunctions{
	private static final DeferredRegister<LootPoolEntryType> REGISTER = DeferredRegister.create(
			BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, ImmersivePetroleum.MODID
	);
	
	public static final Holder<LootPoolEntryType> TILE_DROP = registerEntry("tile_drop", IPTileDropLootEntry.CODEC);
	
	public static void modConstruction(IEventBus modEventBus){
		REGISTER.register(modEventBus);
	}
	
	// Joinked from IELootFunctions :3
	private static Holder<LootPoolEntryType> registerEntry(String id, Codec<? extends LootPoolEntryContainer> serializer){
		return REGISTER.register(id, () -> new LootPoolEntryType(serializer));
	}
}
