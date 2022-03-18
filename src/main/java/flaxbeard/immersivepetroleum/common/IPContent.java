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

		public static RegistryObject<DistillationTowerBlock> DISTILLATIONTOWER;
		public static RegistryObject<PumpjackBlock> PUMPJACK;
		public static RegistryObject<CokerUnitBlock> COKERUNIT;
		public static RegistryObject<HydrotreaterBlock> HYDROTREATER;
		public static RegistryObject<DerrickBlock> DERRICK;
		public static RegistryObject<OilTankBlock> OILTANK;
		
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
		public static final RegistryObject<PetcokeBlock> PETCOKE = IPRegisters.registerIPBlock("petcoke_block", PetcokeBlock::new);
		
		public static final RegistryObject<GasGeneratorBlock> GAS_GENERATOR = IPRegisters.registerIPBlock("gas_generator", GasGeneratorBlock::new);
		public static final RegistryObject<AutoLubricatorBlock> AUTO_LUBRICATOR = IPRegisters.registerIPBlock("auto_lubricator", AutoLubricatorBlock::new);
		public static final RegistryObject<FlarestackBlock> FLARESTACK = IPRegisters.registerIPBlock("flarestack", FlarestackBlock::new);
		
		public static final RegistryObject<BlockDummy> DUMMYOILORE = IPRegisters.registerIPBlock("dummy_oil_ore", BlockDummy::new);
		public static final RegistryObject<BlockDummy> DUMMYPIPE = IPRegisters.registerIPBlock("dummy_pipe", BlockDummy::new);
		public static final RegistryObject<BlockDummy> DUMMYCONVEYOR = IPRegisters.registerIPBlock("dummy_conveyor", BlockDummy::new);
		
		public static final RegistryObject<WellBlock> WELL = IPRegisters.registerIPBlock("well", WellBlock::new);
		public static final RegistryObject<WellPipeBlock> WELLPIPE = IPRegisters.registerIPBlock("well_pipe", WellPipeBlock::new);
		
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
		
		public static final RegistryObject<Item> BITUMEN = IPRegisters.registerItem("bitumen", IPItemBase::new);
		public static RegistryObject<Item> PROJECTOR = IPRegisters.registerItem("projector", ProjectorItem::new);
		public static final RegistryObject<MotorboatItem> SPEEDBOAT = IPRegisters.registerItem("speedboat", MotorboatItem::new);
		public static final RegistryObject<OilCanItem> OIL_CAN = IPRegisters.registerItem("oil_can", OilCanItem::new);
		public static final RegistryObject<Item> PETCOKE = IPRegisters.registerItem("petcoke", () -> new IPItemBase(){
			@Override
			public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType){
				return 3200;
			}
		});
		public static final RegistryObject<Item> PETCOKEDUST = IPRegisters.registerItem("petcoke_dust", IPItemBase::new);
		
		private static void forceClassLoad(){
		}
	}
	
	public static class BoatUpgrades{
		@Deprecated public static IPUpgradeItem reinforced_hull;
		@Deprecated public static IPUpgradeItem ice_breaker;
		@Deprecated public static IPUpgradeItem tank;
		@Deprecated public static IPUpgradeItem rudders;
		@Deprecated public static IPUpgradeItem paddles;
		
		public static RegistryObject<IPUpgradeItem> REINFORCED_HULL = createBoatUpgrade("reinforced_hull");
		public static RegistryObject<IPUpgradeItem> ICE_BREAKER = createBoatUpgrade("icebreaker");
		public static RegistryObject<IPUpgradeItem> TANK = createBoatUpgrade("tank");
		public static RegistryObject<IPUpgradeItem> RUDDERS = createBoatUpgrade("rudders");
		public static RegistryObject<IPUpgradeItem> PADDLES = createBoatUpgrade("paddles");
		
		private static void forceClassLoad(){
		}

		private static <T extends Item> RegistryObject<IPUpgradeItem> createBoatUpgrade(String name){
			return IPRegisters.registerItem("upgrade_" + name, () -> new IPUpgradeItem(MotorboatItem.UPGRADE_TYPE));
		}
	}
	
	@Deprecated
	public static DebugItem debugItem;
	public static RegistryObject<Item> DEBUGITEM = IPRegisters.registerItem("debug", DebugItem::new);
	
	/** block/item/fluid population */
	public static void populate(){
		Blocks.forceClassLoad();
		Fluids.forceClassLoad();
		Items.forceClassLoad();
		BoatUpgrades.forceClassLoad();
		Multiblock.forceClassLoad();
		
		// TODO Remove below later
		// ##############################################

		Fluids.crudeOil = new CrudeOilFluid();
		Fluids.diesel = new DieselFluid("diesel");
		Fluids.diesel_sulfur = new DieselFluid("diesel_sulfur");
		Fluids.lubricant = new IPFluid("lubricant", 925, 1000);
		Fluids.gasoline = new IPFluid("gasoline", 789, 1200);
		Fluids.napalm = new NapalmFluid();

		AsphaltBlock asphalt = new AsphaltBlock();
		Blocks.asphalt = asphalt;
		Blocks.asphalt_slab = new AsphaltSlab(asphalt);
		Blocks.asphalt_stair = new AsphaltStairs(asphalt);

		Multiblock.distillationtower = new DistillationTowerBlock();
		Multiblock.pumpjack = new PumpjackBlock();
		Multiblock.cokerunit = new CokerUnitBlock();
		Multiblock.hydrotreater = new HydrotreaterBlock();
		Multiblock.derrick = new DerrickBlock();
		Multiblock.oiltank = new OilTankBlock();
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
