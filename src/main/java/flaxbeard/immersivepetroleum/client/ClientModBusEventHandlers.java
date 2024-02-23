package flaxbeard.immersivepetroleum.client;

import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.render.AutoLubricatorRenderer;
import flaxbeard.immersivepetroleum.client.render.DerrickRenderer;
import flaxbeard.immersivepetroleum.client.render.MotorboatRenderer;
import flaxbeard.immersivepetroleum.client.render.MultiblockDistillationTowerRenderer;
import flaxbeard.immersivepetroleum.client.render.MultiblockPumpjackRenderer;
import flaxbeard.immersivepetroleum.client.render.OilTankRenderer;
import flaxbeard.immersivepetroleum.client.render.SeismicSurveyBarrelRenderer;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.entity.IPEntityTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientModBusEventHandlers{
	@SubscribeEvent
	public static void registerRenders(EntityRenderersEvent.RegisterRenderers ev){
		registerBERender(ev, IPTileTypes.TOWER.master(), MultiblockDistillationTowerRenderer::new);
		registerBERender(ev, IPTileTypes.PUMP.master(), MultiblockPumpjackRenderer::new);
		registerBERender(ev, IPTileTypes.OILTANK.master(), OilTankRenderer::new);
		registerBERender(ev, IPTileTypes.DERRICK.master(), DerrickRenderer::new);
		
		registerBERender(ev, IPTileTypes.AUTOLUBE.get(), AutoLubricatorRenderer::new);
		registerBERender(ev, IPTileTypes.SEISMIC_SURVEY.get(), SeismicSurveyBarrelRenderer::new);
		
		registerEntityRenderingHandler(ev, IPEntityTypes.MOTORBOAT, MotorboatRenderer::new);
		registerEntityRenderingHandler(ev, IPEntityTypes.MOLOTOV, ThrownItemRenderer::new);
	}
	
	private static <T extends BlockEntity> void registerBERender(RegisterRenderers ev, BlockEntityType<T> type, Supplier<BlockEntityRenderer<T>> factory){
		ev.registerBlockEntityRenderer(type, ctx -> factory.get());
	}
	
	private static <T extends Entity, T2 extends T> void registerEntityRenderingHandler(EntityRenderersEvent.RegisterRenderers ev, Supplier<EntityType<T2>> type, EntityRendererProvider<T> renderer){
		ev.registerEntityRenderer(type.get(), renderer);
	}
}
