package flaxbeard.immersivepetroleum.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.util.IPEffects.IPEffect;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class IPRegisters{
	private static final DeferredRegister<CreativeModeTab> CTAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ImmersivePetroleum.MODID);
	
	private static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.createBlocks(ImmersivePetroleum.MODID);
	public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.createItems(ImmersivePetroleum.MODID);
	private static final DeferredRegister<Fluid> FLUID_REGISTER = DeferredRegister.create(Registries.FLUID, ImmersivePetroleum.MODID);
	private static final DeferredRegister<BlockEntityType<?>> TE_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ImmersivePetroleum.MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, ImmersivePetroleum.MODID);
	private static final DeferredRegister<MenuType<?>> MENU_REGISTER = DeferredRegister.create(Registries.MENU, ImmersivePetroleum.MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ImmersivePetroleum.MODID);
	private static final DeferredRegister<MobEffect> MOB_EFFECT = DeferredRegister.create(Registries.MOB_EFFECT, ImmersivePetroleum.MODID);
	private static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(Registries.SOUND_EVENT, ImmersivePetroleum.MODID);
	private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPE = DeferredRegister.create(Registries.PARTICLE_TYPE, ImmersivePetroleum.MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(Registries.ENTITY_TYPE, ImmersivePetroleum.MODID);
	public static final DeferredRegister<FluidType> FLUID_TYPE = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ImmersivePetroleum.MODID);
	
	private static final List<Consumer<IEventBus>> MOD_BUS_CALLBACKS = new ArrayList<>();
	
	public static void addRegistersToEventBus(final IEventBus eventBus){
		CTAB_REGISTER.register(eventBus);
		FLUID_REGISTER.register(eventBus);
		BLOCK_REGISTER.register(eventBus);
		ITEM_REGISTER.register(eventBus);
		TE_REGISTER.register(eventBus);
		ENTITY_REGISTER.register(eventBus);
		MENU_REGISTER.register(eventBus);
		RECIPE_SERIALIZERS.register(eventBus);
		MOB_EFFECT.register(eventBus);
		SOUND_EVENT.register(eventBus);
		PARTICLE_TYPE.register(eventBus);
		ENTITY_TYPE.register(eventBus);
		FLUID_TYPE.register(eventBus);
		MOD_BUS_CALLBACKS.forEach(e -> e.accept(eventBus));
	}
	
	public static <T extends CreativeModeTab> DeferredHolder<CreativeModeTab, T> registerCreativeTab(String name, Supplier<T> tabConstruction){
		return CTAB_REGISTER.register(name, tabConstruction);
	}
	
	public static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> blockConstructor){
		return registerBlock(name, blockConstructor, null);
	}
	
	public static <S extends IMultiblockState> MultiblockRegistration<S> registerMetalMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure){
		// @formatter:off
		BlockBehaviour.Properties prop = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
			.strength(3, 15)
			.requiresCorrectToolForDrops()
			.isViewBlocking((state, blockReader, pos) -> false)
			.noOcclusion()
			.dynamicShape()
			.pushReaction(PushReaction.BLOCK);
		// @formatter:on
		
		return registerMultiblock(name, logic, structure, prop);
	}
	
	public static <S extends IMultiblockState> MultiblockRegistration<S> registerMultiblock(String name, IMultiblockLogic<S> logic, Supplier<TemplateMultiblock> structure, BlockBehaviour.Properties prop){
		final ResourceLocation rl = ResourceUtils.ip(name);
		
		// @formatter:off
		MultiblockBuilder<S> builder = new MultiblockBuilder<>(logic, rl)
			.structure(structure)
			.defaultBEs(TE_REGISTER)
			.defaultBlock(BLOCK_REGISTER, ITEM_REGISTER, prop);
		// @formatter:on
		
		return builder.build(MOD_BUS_CALLBACKS::add);
	}
	
	private static class MultiblockBuilder<S extends IMultiblockState> extends MultiblockRegistrationBuilder<S, MultiblockBuilder<S>>{
		public MultiblockBuilder(IMultiblockLogic<S> logic, ResourceLocation name){
			super(logic, name);
		}
		
		@Override
		protected MultiblockBuilder<S> self(){
			return this;
		}
	}
	
	@Deprecated
	public static <T extends Block> DeferredHolder<Block, T> registerMultiblockBlock(String name, Supplier<T> blockConstructor){
		throw new UnsupportedOperationException();
	}
	
	public static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> blockConstructor, @Nullable Function<T, ? extends BlockItem> blockItem){
		DeferredHolder<Block, T> block = BLOCK_REGISTER.register(name, blockConstructor);
		if(blockItem != null){
			registerItem(name, () -> blockItem.apply(block.get()));
		}
		return block;
	}
	
	public static <T extends IPBlockBase> DeferredHolder<Block, T> registerIPBlock(String name, Supplier<T> blockConstructor){
		DeferredHolder<Block, T> block = BLOCK_REGISTER.register(name, blockConstructor);
		
		registerItem(name, () -> block.get().blockItemSupplier().get());
		
		return block;
	}
	
	public static <T extends Item> DeferredHolder<Item, T> registerItem(String name, Supplier<T> itemConstructor){
		return ITEM_REGISTER.register(name, itemConstructor);
	}
	
	public static <T extends Fluid> DeferredHolder<Fluid, T> registerFluid(String name, Supplier<T> fluidConstructor){
		return FLUID_REGISTER.register(name, fluidConstructor);
	}
	
	public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerTE(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> valid){
		return TE_REGISTER.register(name, () -> new BlockEntityType<>(factory, ImmutableSet.of(valid.get()), null));
	}
	
	@Deprecated
	public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblockTE(String name, MultiblockBEType.BEWithTypeConstructor<T> factory, Supplier<? extends Block> valid){
		throw new UnsupportedOperationException();
	}
	
	public static <T extends EntityType<?>> DeferredHolder<EntityType<?>, T> registerEntity(String name, Supplier<T> entityConstructor){
		return ENTITY_REGISTER.register(name, entityConstructor);
	}
	
	public static <T extends RecipeSerializer<?>> DeferredHolder<RecipeSerializer<?>, T> registerSerializer(String name, Supplier<T> serializer){
		return RECIPE_SERIALIZERS.register(name, serializer);
	}
	
	public static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenu(String name, Supplier<MenuType<T>> factory){
		return MENU_REGISTER.register(name, factory);
	}
	
	public static <T extends IPEffect> DeferredHolder<MobEffect, T> registerMobEffect(String name, Supplier<T> constructor){
		return MOB_EFFECT.register(name, constructor);
	}
	
	public static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name){
		return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceUtils.ip(name)));
	}
	
	public static <PType extends ParticleType<?>> DeferredHolder<ParticleType<?>, PType> registerParticleType(String name, Supplier<PType> particleType){
		return PARTICLE_TYPE.register(name, particleType);
	}
	
	public static <EType extends EntityType<?>> DeferredHolder<EntityType<?>, EType> registerEntityType(String name, Function<ResourceLocation, EType> entityType){
		return ENTITY_TYPE.register(name, () -> entityType.apply(ResourceUtils.ip(name)));
	}
	
	private IPRegisters(){
	}
}
