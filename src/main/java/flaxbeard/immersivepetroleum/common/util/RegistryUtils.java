package flaxbeard.immersivepetroleum.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public class RegistryUtils{
	@Nullable
	public static ResourceLocation getRegistryNameOf(Item item){
		return BuiltInRegistries.ITEM.getKey(item);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(Block block){
		return BuiltInRegistries.BLOCK.getKey(block);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(Fluid fluid){
		return BuiltInRegistries.FLUID.getKey(fluid);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(@Nonnull Level level, BlockPos pos){
		Biome biome = level.getBiome(pos).value();
		return level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(SoundEvent soundEvent){
		return BuiltInRegistries.SOUND_EVENT.getKey(soundEvent);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(MobEffect mobEffect){
		return BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(EntityType<?> entityType){
		return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(BlockEntityType<?> blockEntityType){
		return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(ParticleType<?> particleType){
		return BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(RecipeType<?> recipeType){
		return BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(RecipeSerializer<?> recipeSerializer){
		return BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
	}
	
	@Nullable
	public static ResourceLocation getRegistryNameOf(MenuType<?> menuType){
		return BuiltInRegistries.MENU.getKey(menuType);
	}
	
	private RegistryUtils(){
	}
}
