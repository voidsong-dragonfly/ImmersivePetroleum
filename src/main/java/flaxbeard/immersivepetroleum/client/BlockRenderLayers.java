package flaxbeard.immersivepetroleum.client;

import java.util.function.Predicate;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.fluids.IPFluid;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class BlockRenderLayers{
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event){
		setRenderLayer(IPContent.Blocks.AUTO_LUBRICATOR, RenderType.translucent());
		setRenderLayer(IPContent.Blocks.GAS_GENERATOR, BlockRenderLayers::solidCutout);
		setRenderLayer(IPContent.Blocks.FLARESTACK, RenderType.cutout());
		
		setRenderLayer(IPContent.Blocks.WELL, RenderType.cutout());
		
		setRenderLayer(IPContent.Blocks.DUMMYCONVEYOR, RenderType.cutout());
		setRenderLayer(IPContent.Blocks.DUMMYOILORE, RenderType.cutout());
		setRenderLayer(IPContent.Blocks.DUMMYPIPE, RenderType.cutout());
		
		setRenderLayer(IPContent.Multiblock.DISTILLATIONTOWER, RenderType.cutout());
		setRenderLayer(IPContent.Multiblock.PUMPJACK, RenderType.cutout());
		setRenderLayer(IPContent.Multiblock.COKERUNIT, RenderType.cutout());
		setRenderLayer(IPContent.Multiblock.HYDROTREATER, RenderType.cutout());
		setRenderLayer(IPContent.Multiblock.DERRICK, RenderType.cutout());
		setRenderLayer(IPContent.Multiblock.OILTANK, RenderType.cutout());
		
		setRenderLayer(IPContent.Fluids.CRUDEOIL, RenderType.translucent());
		setRenderLayer(IPContent.Fluids.DIESEL, RenderType.translucent());
		setRenderLayer(IPContent.Fluids.DIESEL_SULFUR, RenderType.translucent());
		setRenderLayer(IPContent.Fluids.GASOLINE, RenderType.translucent());
		setRenderLayer(IPContent.Fluids.LUBRICANT, RenderType.translucent());
		setRenderLayer(IPContent.Fluids.NAPALM, RenderType.translucent());
	}

	private static void setRenderLayer(RegistryObject<? extends Block> block, RenderType types){
		ItemBlockRenderTypes.setRenderLayer(block.get(), types);
	}

	private static void setRenderLayer(IPFluid.IPFluidEntry entry, RenderType types){
		ItemBlockRenderTypes.setRenderLayer(entry.still().get(), types);
		ItemBlockRenderTypes.setRenderLayer(entry.flowing().get(), types);
	}

	private static void setRenderLayer(RegistryObject<? extends Block> block, Predicate<RenderType> types){
		ItemBlockRenderTypes.setRenderLayer(block.get(), types);
	}

	public static boolean solidCutout(RenderType t){
		return t == RenderType.solid() || t == RenderType.cutout();
	}
}
