package flaxbeard.immersivepetroleum.common;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IPRegisters{
	private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<Fluid> FLUID_REGISTER = DeferredRegister.create(ForgeRegistries.FLUIDS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<BlockEntityType<?>> TE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ImmersivePetroleum.MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, ImmersivePetroleum.MODID);
	private static final DeferredRegister<MenuType<?>> GUI_REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, ImmersivePetroleum.MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ImmersivePetroleum.MODID);
	
	public static final void addRegistersToEventBus(IEventBus eventBus){
		BLOCK_REGISTER.register(eventBus);
		ITEM_REGISTER.register(eventBus);
		FLUID_REGISTER.register(eventBus);
		TE_REGISTER.register(eventBus);
		ENTITY_REGISTER.register(eventBus);
		GUI_REGISTER.register(eventBus);
		RECIPE_SERIALIZERS.register(eventBus);
	}
	
	public static final <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor, @Nullable Function<RegistryObject<T>, ? extends BlockItem> blockItem){
		RegistryObject<T> block = BLOCK_REGISTER.register(name, blockConstructor);
		if(blockItem != null){
			registerItem(name, () -> blockItem.apply(block));
		}
		return block;
	}
	
	public static final <T extends IPBlockBase> RegistryObject<T> registerIPBlock(String name, Supplier<T> blockConstructor){
		RegistryObject<T> block = BLOCK_REGISTER.register(name, blockConstructor);
		
		Supplier<BlockItem> iBlockSupplier = block.get().blockItemSupplier();
		if(iBlockSupplier != null){
			registerItem(name, iBlockSupplier);
		}
		
		return block;
	}
	
	public static final <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemConstructor){
		return ITEM_REGISTER.register(name, itemConstructor);
	}
	
	public static final <T extends Fluid> RegistryObject<T> registerFluid(String name, Supplier<T> fluidConstructor){
		return FLUID_REGISTER.register(name, fluidConstructor);
	}
	
	public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerTE(String name, BlockEntityType.BlockEntitySupplier<T> factory, Block... valid){
		return TE_REGISTER.register(name, () -> new BlockEntityType<T>(factory, ImmutableSet.copyOf(valid), null));
	}
	
	public static <T extends EntityType<?>> RegistryObject<T> registerEntity(String name, Supplier<T> entityConstructor){
		return ENTITY_REGISTER.register(name, entityConstructor);
	}
	
	public static <T extends RecipeSerializer<?>> RegistryObject<T> registerSerializer(String name, Supplier<T> serializer){
		return RECIPE_SERIALIZERS.register(name, serializer);
	}
	
	private static void registerGUI(String name){
		// TODO GUI Register
		GUI_REGISTER.register(name, () -> {
			new MenuType(null);
			return null;
		});
	}
	
	private IPRegisters(){
	}
}
