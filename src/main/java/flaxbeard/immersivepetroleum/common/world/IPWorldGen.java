package flaxbeard.immersivepetroleum.common.world;

import java.util.HashMap;
import java.util.Map;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IPWorldGen{
	public static Map<String, Holder<PlacedFeature>> features = new HashMap<>();
	
	private static final DeferredRegister<Feature<?>> FEATURE_REGISTER = DeferredRegister.create(Registries.FEATURE, ImmersivePetroleum.MODID);
	
	public static final DeferredHolder<Feature<?>, FeatureReservoir> RESERVOIR_FEATURE = FEATURE_REGISTER.register("reservoir", FeatureReservoir::new);
	
	public static void init(IEventBus eBus){
		FEATURE_REGISTER.register(eBus);
	}
}
