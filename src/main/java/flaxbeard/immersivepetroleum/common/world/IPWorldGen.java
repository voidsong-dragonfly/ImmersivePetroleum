package flaxbeard.immersivepetroleum.common.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
					.configured(new NoneFeatureConfiguration())
				);
		features.put("reservoirs", reservoirFeature);
	}
	
	@SubscribeEvent
	public void onBiomeLoad(BiomeLoadingEvent event){
		BiomeGenerationSettingsBuilder generation = event.getGeneration();
		for(Entry<String, ConfiguredFeature<?, ?>> entry:features.entrySet()){
			generation.addFeature(Decoration.UNDERGROUND_ORES, entry.getValue().placed());
		}
	}
	
	private static <FC extends FeatureConfiguration> ConfiguredFeature<FC, ?> register(ResourceLocation key, ConfiguredFeature<FC, ?> configuredFeature){
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key, configuredFeature);
	}
}
