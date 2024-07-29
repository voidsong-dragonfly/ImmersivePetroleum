package flaxbeard.immersivepetroleum;

import flaxbeard.immersivepetroleum.common.items.MotorboatItem;
import flaxbeard.immersivepetroleum.common.util.IPItemStackHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import flaxbeard.immersivepetroleum.api.crafting.IPRecipeTypes;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.client.ClientProxy;
import flaxbeard.immersivepetroleum.common.CommonEventHandler;
import flaxbeard.immersivepetroleum.common.CommonProxy;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import flaxbeard.immersivepetroleum.common.IPToolShaders;
import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage;
import flaxbeard.immersivepetroleum.common.cfg.IPClientConfig;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.RecipeReloadListener;
import flaxbeard.immersivepetroleum.common.network.IPPacketHandler;
import flaxbeard.immersivepetroleum.common.util.commands.IslandCommand;
import flaxbeard.immersivepetroleum.common.util.loot.IPLootFunctions;
import flaxbeard.immersivepetroleum.common.world.IPWorldGen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ImmersivePetroleum.MODID)
public class ImmersivePetroleum{
	public static final String MODID = "immersivepetroleum";
	
	public static final Logger log = LogManager.getLogger(MODID);
	
	public static final CommonProxy proxy = makeProxy();
	private static CommonProxy makeProxy(){
		return FMLLoader.getDist() == Dist.CLIENT ? new ClientProxy() : new CommonProxy();
	}
	
	public ImmersivePetroleum(IEventBus modBus){
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IPServerConfig.ALL);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, IPClientConfig.ALL);
		
		modBus.addListener(this::setup);
		modBus.addListener(IPPacketHandler.Register::init);
		modBus.addListener(this::loadComplete);
		modBus.addListener(this::registerMenuScreens);
		
		NeoForge.EVENT_BUS.addListener(this::worldLoad);
		NeoForge.EVENT_BUS.addListener(this::serverStarting);
		NeoForge.EVENT_BUS.addListener(this::registerCommand);
		NeoForge.EVENT_BUS.addListener(this::registerCapabilities);
		NeoForge.EVENT_BUS.addListener(this::addReloadListeners);
		
		IPRegisters.addRegistersToEventBus(modBus); // TODO Might need to be moved to be *under* IPContent.modConstruction
		
		IPContent.modConstruction(modBus);
		IPLootFunctions.modConstruction(modBus);
		IPRecipeTypes.modConstruction(modBus);
		
		IPWorldGen.init(modBus);
	}
	
	public void setup(FMLCommonSetupEvent event){
		proxy.setup();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.preInit();
		
		IPContent.preInit();
		IPToolShaders.preInit();
		
		proxy.preInitEnd();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		IPContent.init(event);
		
		NeoForge.EVENT_BUS.register(new CommonEventHandler());
		
		proxy.init();
		
		if(ModList.get().isLoaded("computercraft")){
			//IPPeripheralProvider.init();
		}
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.postInit();
		
		ReservoirHandler.recalculateChances();
		ExternalModContent.init();
		
		//proxy.registerContainersAndScreens();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event){
		proxy.completed(event);
	}
	
	public void registerMenuScreens(RegisterMenuScreensEvent event){
		proxy.registerContainersAndScreens(event);
	}
	
	public void registerCommand(RegisterCommandsEvent event){
		LiteralArgumentBuilder<CommandSourceStack> ip = Commands.literal("ip");
		
		ip.then(IslandCommand.create());
		
		event.getDispatcher().register(ip);
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(MotorboatItem.MOTORBOAT_INV, (itemStack, context) -> new IPItemStackHandler(4, itemStack.getCapability(Capabilities.ItemHandler.ITEM)), IPContent.Items.SPEEDBOAT.get());
		event.registerItem(Capabilities.FluidHandler.ITEM, (itemStack, context) -> new FluidHandlerItemStack(itemStack, 8000), IPContent.Items.OIL_CAN.get());
	}
	
	public void addReloadListeners(AddReloadListenerEvent event){
		event.addListener(new RecipeReloadListener(event.getServerResources()));
	}
	
	public void worldLoad(LevelEvent.Load event){
		if(!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel world && world.dimension() == Level.OVERWORLD){
			ReservoirRegionDataStorage.init(world.getDataStorage());
			world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(IPSaveData::new, IPSaveData::new), IPSaveData.dataName);
		}
	}
	
	public void serverStarting(ServerStartingEvent event){
		ReservoirHandler.recalculateChances();
	}
}
