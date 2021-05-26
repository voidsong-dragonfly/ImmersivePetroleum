package flaxbeard.immersivepetroleum.common.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class IPWorldGen{
	public static Map<String, ConfiguredFeature<?, ?>> features = new HashMap<>();
	
	private static DeferredRegister<Feature<?>> FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, ImmersivePetroleum.MODID);
	
	private static RegistryObject<FeatureReservoir> RESERVOIR_FEATURE = FEATURE_REGISTER.register("reservoir", FeatureReservoir::new);
	
	public static void init(IEventBus eBus){
		FEATURE_REGISTER.register(eBus);
	}
	
	public static void registerReservoirGen(){
		ConfiguredFeature<?, ?> reservoirFeature = register(new ResourceLocation(ImmersivePetroleum.MODID, "reservoir"),
				RESERVOIR_FEATURE.get()
					.withConfiguration(new NoFeatureConfig())
					.withPlacement(new ConfiguredPlacement<>(Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG))
				);
		features.put("reservoirs", reservoirFeature);
	}
	
	@SubscribeEvent
	public void onBiomeLoad(BiomeLoadingEvent event){
		BiomeGenerationSettingsBuilder generation = event.getGeneration();
		for(Entry<String, ConfiguredFeature<?, ?>> entry:features.entrySet()){
			generation.withFeature(Decoration.UNDERGROUND_ORES, entry.getValue());
		}
	}
	
	private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(ResourceLocation key, ConfiguredFeature<FC, ?> configuredFeature){
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, configuredFeature);
	}
}
