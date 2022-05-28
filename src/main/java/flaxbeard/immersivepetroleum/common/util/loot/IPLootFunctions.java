package flaxbeard.immersivepetroleum.common.util.loot;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IPLootFunctions{
	private static final DeferredRegister<LootPoolEntryType> REGISTER = DeferredRegister.create(
			Registry.LOOT_ENTRY_REGISTRY, ImmersivePetroleum.MODID
	);
	public static final RegistryObject<LootPoolEntryType> TILE_DROP = REGISTER.register(
			IPTileDropLootEntry.ID.getPath(), () -> new LootPoolEntryType(new IPTileDropLootEntry.Serializer())
	);
	
	public static void modConstruction(){
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
