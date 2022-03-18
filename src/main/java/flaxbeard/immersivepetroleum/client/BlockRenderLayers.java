package flaxbeard.immersivepetroleum.client;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class BlockRenderLayers{
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event){
		ItemBlockRenderTypes.setRenderLayer(IPContent.Blocks.auto_lubricator, BlockRenderLayers::lubeLayer);
		ItemBlockRenderTypes.setRenderLayer(IPContent.Blocks.gas_generator, BlockRenderLayers::solidCutout);
		ItemBlockRenderTypes.setRenderLayer(IPContent.Blocks.flarestack, BlockRenderLayers::stackLayer);
		
		ItemBlockRenderTypes.setRenderLayer(IPContent.Blocks.well, RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(IPContent.Blocks.dummyConveyor, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Blocks.dummyOilOre, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Blocks.dummyPipe, RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(IPContent.Multiblock.distillationtower, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Multiblock.pumpjack, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Multiblock.cokerunit, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Multiblock.hydrotreater, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Multiblock.derrick, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Multiblock.oiltank, RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.crudeOil, RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.diesel, RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.diesel_sulfur, RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.gasoline, RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.lubricant, RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.napalm, RenderType.translucent());
		
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.crudeOil.getFlowing(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.diesel.getFlowing(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.diesel_sulfur.getFlowing(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.gasoline.getFlowing(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.lubricant.getFlowing(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(IPContent.Fluids.napalm.getFlowing(), RenderType.translucent());
	}
	
	public static boolean lubeLayer(RenderType t){
		return t == RenderType.translucent();
	}
	
	public static boolean stackLayer(RenderType t){
		return t == RenderType.cutout();
	}
	
	public static boolean solidCutout(RenderType t){
		return t == RenderType.solid() || t == RenderType.cutout();
	}
}
