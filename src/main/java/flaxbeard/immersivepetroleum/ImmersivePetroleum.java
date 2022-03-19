package flaxbeard.immersivepetroleum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.client.ClientProxy;
import flaxbeard.immersivepetroleum.common.CommonEventHandler;
import flaxbeard.immersivepetroleum.common.CommonProxy;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPContent.Fluids;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import flaxbeard.immersivepetroleum.common.cfg.IPClientConfig;
import flaxbeard.immersivepetroleum.common.cfg.IPCommonConfig;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.RecipeReloadListener;
import flaxbeard.immersivepetroleum.common.network.IPPacketHandler;
import flaxbeard.immersivepetroleum.common.util.commands.IslandCommand;
import flaxbeard.immersivepetroleum.common.util.loot.IPLootFunctions;
import flaxbeard.immersivepetroleum.common.world.IPWorldGen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ImmersivePetroleum.MODID)
public class ImmersivePetroleum{
	public static final String MODID = "immersivepetroleum";
	
	public static final Logger log = LogManager.getLogger(MODID);
	
	public static final CreativeModeTab creativeTab = new CreativeModeTab(MODID){
		@Override
		public ItemStack makeIcon(){
			return new ItemStack(Fluids.CRUDEOIL.bucket().get());
		}
	};
	
	public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	
	public static ImmersivePetroleum INSTANCE;
	
	public ImmersivePetroleum(){
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IPServerConfig.ALL);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, IPClientConfig.ALL);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IPCommonConfig.ALL);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
		
		IEventBus eBus = FMLJavaModLoadingContext.get().getModEventBus();
		IPRegisters.addRegistersToEventBus(eBus);
		//Serializers.RECIPE_SERIALIZERS.register(eBus);
		
		IPContent.populate();
		IPLootFunctions.modConstruction();
		
		//IPTileTypes.REGISTER.register(eBus);
		
		MinecraftForge.EVENT_BUS.register(new IPWorldGen());
		IPWorldGen.init(eBus);
	}
	
	public void setup(FMLCommonSetupEvent event){
		proxy.setup();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.preInit();
		
		IPContent.preInit();
		IPPacketHandler.preInit();
		
		proxy.preInitEnd();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		IPContent.init(event);
		
		MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
		
		proxy.init();
		
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		proxy.postInit();
		
		ReservoirHandler.recalculateChances();
		ExternalModContent.init();

		proxy.registerContainersAndScreens();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event){
		proxy.completed(event);
	}
	
	public void serverAboutToStart(ServerAboutToStartEvent event){
		proxy.serverAboutToStart();
	}
	
	public void serverStarting(ServerStartingEvent event){
		proxy.serverStarting();
	}
	
	public void registerCommand(RegisterCommandsEvent event){
		LiteralArgumentBuilder<CommandSourceStack> ip = Commands.literal("ip");
		
		ip.then(IslandCommand.create());
		
		event.getDispatcher().register(ip);
	}
	
	public void addReloadListeners(AddReloadListenerEvent event){
		event.addListener(new RecipeReloadListener(event.getDataPackRegistries()));
	}
	
	public void serverStarted(ServerStartedEvent event){
		proxy.serverStarted();
		
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		if(!world.isClientSide){
			IPSaveData worldData = world.getDataStorage().computeIfAbsent(IPSaveData::new, IPSaveData::new, IPSaveData.dataName);
			IPSaveData.setInstance(worldData);
		}
		
		ReservoirHandler.recalculateChances();
	}
}
