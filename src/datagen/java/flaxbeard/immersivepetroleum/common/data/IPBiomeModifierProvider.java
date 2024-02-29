package flaxbeard.immersivepetroleum.common.data;

import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.world.IPWorldGen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;

public class IPBiomeModifierProvider{
	public static void method(DataGenerator generator, ExistingFileHelper exhelper, Consumer<DataProvider> add){
		final RegistryAccess registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		
		final RegistryOps<JsonElement> jsonOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
		final Registry<Biome> biomeReg = registryAccess.registryOrThrow(Registries.BIOME);
		final Registry<PlacedFeature> featureReg = registryAccess.registryOrThrow(Registries.PLACED_FEATURE);
		
		final AnyHolderSet<Biome> anyBiome = new AnyHolderSet<Biome>(biomeReg.asLookup());
		
		final ImmutableMap.Builder<ResourceLocation, BiomeModifier> modifiers = ImmutableMap.builder();
		for(Entry<String, Holder<PlacedFeature>> entry:IPWorldGen.features.entrySet()){
			ResourceLocation name = ResourceUtils.ip(entry.getKey());
			
			ResourceKey<PlacedFeature> key = ResourceKey.create(Registries.PLACED_FEATURE, name);
			Holder<PlacedFeature> featureHolder = featureReg.getHolderOrThrow(key);
			
			AddFeaturesBiomeModifier modifier = new AddFeaturesBiomeModifier(anyBiome, HolderSet.direct(featureHolder), Decoration.UNDERGROUND_ORES);
			modifiers.put(name, modifier);
		}
		
		add.accept(JsonCodecProvider.forDatapackRegistry(generator, exhelper, ImmersivePetroleum.MODID, jsonOps, Keys.BIOME_MODIFIERS, modifiers.build()));
	}
}
