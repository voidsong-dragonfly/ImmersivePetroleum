package flaxbeard.immersivepetroleum.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect_Potion;
import blusunrize.immersiveengineering.common.blocks.metal.CrusherBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ExcavatorBlockEntity;
import blusunrize.immersiveengineering.common.register.IEPotions;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.IPTags;
import flaxbeard.immersivepetroleum.api.crafting.FlarestackHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricantHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.LubricantEffect;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.metal.AutoLubricatorBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.BlockDummy;
import flaxbeard.immersivepetroleum.common.blocks.metal.CokerUnitBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.DerrickBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.DistillationTowerBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.FlarestackBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.GasGeneratorBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.HydrotreaterBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.OilTankBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.PumpjackBlock;
import flaxbeard.immersivepetroleum.common.blocks.stone.AsphaltBlock;
import flaxbeard.immersivepetroleum.common.blocks.stone.AsphaltSlab;
import flaxbeard.immersivepetroleum.common.blocks.stone.AsphaltStairs;
import flaxbeard.immersivepetroleum.common.blocks.stone.PetcokeBlock;
import flaxbeard.immersivepetroleum.common.blocks.stone.WellBlock;
import flaxbeard.immersivepetroleum.common.blocks.stone.WellPipeBlock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import flaxbeard.immersivepetroleum.common.fluids.CrudeOilFluid;
import flaxbeard.immersivepetroleum.common.fluids.DieselFluid;
import flaxbeard.immersivepetroleum.common.fluids.IPFluid;
import flaxbeard.immersivepetroleum.common.fluids.NapalmFluid;
import flaxbeard.immersivepetroleum.common.items.DebugItem;
import flaxbeard.immersivepetroleum.common.items.IPItemBase;
import flaxbeard.immersivepetroleum.common.items.IPUpgradeItem;
import flaxbeard.immersivepetroleum.common.items.MotorboatItem;
import flaxbeard.immersivepetroleum.common.items.OilCanItem;
import flaxbeard.immersivepetroleum.common.items.ProjectorItem;
import flaxbeard.immersivepetroleum.common.lubehandlers.CrusherLubricationHandler;
import flaxbeard.immersivepetroleum.common.lubehandlers.ExcavatorLubricationHandler;
import flaxbeard.immersivepetroleum.common.lubehandlers.PumpjackLubricationHandler;
import flaxbeard.immersivepetroleum.common.multiblocks.CokerUnitMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.DistillationTowerMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.HydroTreaterMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.OilTankMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.PumpjackMultiblock;
import flaxbeard.immersivepetroleum.common.particle.FlareFire;
import flaxbeard.immersivepetroleum.common.particle.FluidSpill;
import flaxbeard.immersivepetroleum.common.particle.IPParticleTypes;
import flaxbeard.immersivepetroleum.common.util.IPEffects;
import flaxbeard.immersivepetroleum.common.util.MCUtil;
import flaxbeard.immersivepetroleum.common.world.IPWorldGen;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPContent{
	public static final Logger log = LogManager.getLogger(ImmersivePetroleum.MODID + "/Content");
	
	/** @deprecated Switch to {@link IPRegisters#BLOCK_REGISTER}. */
	public static final List<Block> registeredIPBlocks = new ArrayList<>();
	/** @deprecated Switch to {@link IPRegisters#ITEM_REGISTER}. */
	public static final List<Item> registeredIPItems = new ArrayList<>();
	/** @deprecated Switch to {@link IPRegisters#FLUID_REGISTER}. */
	public static final List<Fluid> registeredIPFluids = new ArrayList<>();
	
	public static class Multiblock{
		@Deprecated public static Block distillationtower;
		@Deprecated public static Block pumpjack;
		@Deprecated public static Block cokerunit;
		@Deprecated public static Block hydrotreater;
		@Deprecated public static Block derrick;
		@Deprecated public static Block oiltank;
		
		public static RegistryObject<Block> DISTILLATIONTOWER;
		public static RegistryObject<Block> PUMPJACK;
		public static RegistryObject<Block> COKERUNIT;
		public static RegistryObject<Block> HYDROTREATER;
		public static RegistryObject<Block> DERRICK;
		public static RegistryObject<Block> OILTANK;
		
		private static void forceClassLoad(){
		}
	}
	
	public static class Fluids{
		@Deprecated public static IPFluid crudeOil;
		@Deprecated public static IPFluid diesel;
		@Deprecated public static IPFluid diesel_sulfur;
		@Deprecated public static IPFluid lubricant;
		@Deprecated public static IPFluid gasoline;
		@Deprecated public static IPFluid napalm;
		
		public static RegistryObject<Fluid> CRUDEOIL;
		public static RegistryObject<Fluid> DIESEL;
		public static RegistryObject<Fluid> DIESEL_SULFUR;
		public static RegistryObject<Fluid> LUBRICANT;
		public static RegistryObject<Fluid> GASOLINE;
		public static RegistryObject<Fluid> NAPALM;
		
		private static void forceClassLoad(){
		}
	}
	
	public static class Blocks{
		@Deprecated public static IPBlockBase asphalt;
		@Deprecated public static AsphaltSlab asphalt_slab;
		@Deprecated public static AsphaltStairs asphalt_stair;
		@Deprecated public static IPBlockBase petcoke;
		
		@Deprecated public static IPBlockBase gas_generator;
		@Deprecated public static IPBlockBase auto_lubricator;
		@Deprecated public static IPBlockBase flarestack;
		
		@Deprecated public static BlockDummy dummyOilOre;
		@Deprecated public static BlockDummy dummyPipe;
		@Deprecated public static BlockDummy dummyConveyor;
		
		@Deprecated public static WellBlock well;
		@Deprecated public static WellPipeBlock wellPipe;
		
		public static RegistryObject<Block> ASPHALT;
		public static RegistryObject<Block> ASPHALT_SLAB;
		public static RegistryObject<Block> ASPHALT_STAIR;
		public static RegistryObject<Block> PETCOKE;
		
		public static RegistryObject<Block> GAS_GENERATOR;
		public static RegistryObject<Block> AUTO_LUBRICATOR;
		public static RegistryObject<Block> FLARESTACK;
		
		public static RegistryObject<Block> DUMMYOILORE;
		public static RegistryObject<Block> DUMMYPIPE;
		public static RegistryObject<Block> DUMMYCONVEYOR;
		
		public static RegistryObject<Block> WELL;
		public static RegistryObject<Block> WELLPIPE;
		
		private static void forceClassLoad(){
		}
	}
	
	public static class Items{
		@Deprecated public static IPItemBase bitumen;
		@Deprecated public static IPItemBase projector;
		@Deprecated public static IPItemBase speedboat;
		@Deprecated public static IPItemBase oil_can;
		@Deprecated public static IPItemBase petcoke;
		@Deprecated public static IPItemBase petcokedust;
		
		public static RegistryObject<Item> BITUMEN;
		public static RegistryObject<Item> PROJECTOR;
		public static RegistryObject<Item> SPEEDBOAT;
		public static RegistryObject<Item> OIL_CAN;
		public static RegistryObject<Item> PETCOKE;
		public static RegistryObject<Item> PETCOKEDUST;
		
		private static void forceClassLoad(){
		}
	}
	
	public static class BoatUpgrades{
		@Deprecated public static IPUpgradeItem reinforced_hull;
		@Deprecated public static IPUpgradeItem ice_breaker;
		@Deprecated public static IPUpgradeItem tank;
		@Deprecated public static IPUpgradeItem rudders;
		@Deprecated public static IPUpgradeItem paddles;
		
		public static RegistryObject<Item> REINFORCED_HULL;
		public static RegistryObject<Item> ICE_BREAKER;
		public static RegistryObject<Item> TANK;
		public static RegistryObject<Item> RUDDERS;
		public static RegistryObject<Item> PADDLES;
		
		private static void forceClassLoad(){
		}
	}
	
	@Deprecated
	public static DebugItem debugItem;
	public static RegistryObject<Item> DEBUGITEM = IPRegisters.registerItem("debug", DebugItem::new);
	
	/** block/item/fluid population */
	@SuppressWarnings("deprecation")
	public static void populate(){
		Blocks.forceClassLoad();
		Fluids.forceClassLoad();
		Items.forceClassLoad();
		BoatUpgrades.forceClassLoad();
		Multiblock.forceClassLoad();
		
		// TODO Remove below later
		// ##############################################
		
		//IPContent.debugItem = new DebugItem();
		
		Fluids.crudeOil = new CrudeOilFluid();
		Fluids.diesel = new DieselFluid("diesel");
		Fluids.diesel_sulfur = new DieselFluid("diesel_sulfur");
		Fluids.lubricant = new IPFluid("lubricant", 925, 1000);
		Fluids.gasoline = new IPFluid("gasoline", 789, 1200);
		Fluids.napalm = new NapalmFluid();
		
		Blocks.dummyOilOre = new BlockDummy("dummy_oil_ore");
		Blocks.dummyPipe = new BlockDummy("dummy_pipe");
		Blocks.dummyConveyor = new BlockDummy("dummy_conveyor");
		
		Blocks.petcoke = new PetcokeBlock();
		Blocks.gas_generator = new GasGeneratorBlock();
		
		AsphaltBlock asphalt = new AsphaltBlock();
		Blocks.asphalt = asphalt;
		Blocks.asphalt_slab = new AsphaltSlab(asphalt);
		Blocks.asphalt_stair = new AsphaltStairs(asphalt);
		
		Blocks.well = new WellBlock();
		Blocks.wellPipe = new WellPipeBlock();
		
		Blocks.auto_lubricator = new AutoLubricatorBlock("auto_lubricator");
		Blocks.flarestack = new FlarestackBlock();
		
		Multiblock.distillationtower = new DistillationTowerBlock();
		Multiblock.pumpjack = new PumpjackBlock();
		Multiblock.cokerunit = new CokerUnitBlock();
		Multiblock.hydrotreater = new HydrotreaterBlock();
		Multiblock.derrick = new DerrickBlock();
		Multiblock.oiltank = new OilTankBlock();
		
		Items.bitumen = new IPItemBase("bitumen");
		Items.oil_can = new OilCanItem("oil_can");
		Items.speedboat = new MotorboatItem("speedboat");
		Items.petcoke = new IPItemBase("petcoke"){
			@Override
			public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType){
				return 3200;
			}
		};
		Items.petcokedust = new IPItemBase("petcoke_dust");
		
		BoatUpgrades.reinforced_hull = new IPUpgradeItem("reinforced_hull", MotorboatItem.UPGRADE_TYPE);
		BoatUpgrades.ice_breaker = new IPUpgradeItem("icebreaker", MotorboatItem.UPGRADE_TYPE);
		BoatUpgrades.tank = new IPUpgradeItem("tank", MotorboatItem.UPGRADE_TYPE);
		BoatUpgrades.rudders = new IPUpgradeItem("rudders", MotorboatItem.UPGRADE_TYPE);
		BoatUpgrades.paddles = new IPUpgradeItem("paddles", MotorboatItem.UPGRADE_TYPE);
		
		Items.projector = new ProjectorItem("projector");
	}
	
	public static void preInit(){
	}
	
	public static void init(ParallelDispatchEvent event){
		event.enqueueWork(() -> {
			IPWorldGen.registerReservoirGen();
		});
		
		//blockFluidCrudeOil.setPotionEffects(new PotionEffect(IEPotions.flammable, 100, 1));
		//blockFluidDiesel.setPotionEffects(new PotionEffect(IEPotions.flammable, 100, 1));
		//blockFluidLubricant.setPotionEffects(new PotionEffect(IEPotions.slippery, 100, 1));
		//blockFluidNapalm.setPotionEffects(new PotionEffect(IEPotions.flammable, 140, 2));
		
		ChemthrowerHandler.registerEffect(IPTags.Fluids.lubricant, new LubricantEffect());
		ChemthrowerHandler.registerEffect(IPTags.Fluids.lubricant, new ChemthrowerEffect_Potion(null, 0, IEPotions.SLIPPERY.get(), 60, 1));
		ChemthrowerHandler.registerEffect(IETags.fluidPlantoil, new LubricantEffect());
		
		ChemthrowerHandler.registerFlammable(IPTags.Fluids.crudeOil);
		ChemthrowerHandler.registerEffect(IPTags.Fluids.crudeOil, new ChemthrowerEffect_Potion(null, 0, IEPotions.FLAMMABLE.get(), 60, 1));
		
		ChemthrowerHandler.registerFlammable(IPTags.Fluids.gasoline);
		ChemthrowerHandler.registerEffect(IPTags.Fluids.gasoline, new ChemthrowerEffect_Potion(null, 0, IEPotions.FLAMMABLE.get(), 60, 1));
		
		ChemthrowerHandler.registerFlammable(IPTags.Fluids.napalm);
		ChemthrowerHandler.registerEffect(IPTags.Fluids.napalm, new ChemthrowerEffect_Potion(null, 0, IEPotions.FLAMMABLE.get(), 60, 2));
		
		MultiblockHandler.registerMultiblock(DistillationTowerMultiblock.INSTANCE);
		MultiblockHandler.registerMultiblock(PumpjackMultiblock.INSTANCE);
		MultiblockHandler.registerMultiblock(CokerUnitMultiblock.INSTANCE);
		MultiblockHandler.registerMultiblock(HydroTreaterMultiblock.INSTANCE);
		MultiblockHandler.registerMultiblock(DerrickMultiblock.INSTANCE);
		MultiblockHandler.registerMultiblock(OilTankMultiblock.INSTANCE);
		
		//ConfigUtils.addFuel(IPServerConfig.GENERATION.fuels.get());
		//ConfigUtils.addBoatFuel(IPServerConfig.MISCELLANEOUS.boat_fuels.get());
		
		LubricantHandler.register(IPTags.Fluids.lubricant, 3);
		LubricantHandler.register(IETags.fluidPlantoil, 12);
		
		FlarestackHandler.register(IPTags.Utility.burnableInFlarestack);
		
		LubricatedHandler.registerLubricatedTile(PumpjackTileEntity.class, PumpjackLubricationHandler::new);
		LubricatedHandler.registerLubricatedTile(ExcavatorBlockEntity.class, ExcavatorLubricationHandler::new);
		LubricatedHandler.registerLubricatedTile(CrusherBlockEntity.class, CrusherLubricationHandler::new);
	}
	
	@SubscribeEvent
	public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event){
		try{
			event.getRegistry().register(MotorboatEntity.TYPE);
		}catch(Throwable e){
			log.error("Failed to register Speedboat Entity. {}", e.getMessage());
			throw e;
		}
	}
	
	@SubscribeEvent
	public static void registerEffects(RegistryEvent.Register<MobEffect> event){
		IPEffects.init();
	}
	
	@SubscribeEvent
	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event){
		event.getRegistry().register(IPParticleTypes.FLARE_FIRE);
		event.getRegistry().register(IPParticleTypes.FLUID_SPILL);
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event){
		ParticleEngine manager = MCUtil.getParticleEngine();

		manager.register(IPParticleTypes.FLARE_FIRE, FlareFire.Factory::new);
		manager.register(IPParticleTypes.FLUID_SPILL, new FluidSpill.Factory());
	}
}
