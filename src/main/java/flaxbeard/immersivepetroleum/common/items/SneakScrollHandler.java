package flaxbeard.immersivepetroleum.common.items;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT)
public class SneakScrollHandler{
	private static boolean sneaking = false;
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(event.side == LogicalSide.CLIENT && event.player != null && event.player == Minecraft.getInstance().getCameraEntity()){
			if(event.phase == Phase.END){
				sneaking = event.player.isCrouching();
			}
		}
	}
	
	@SubscribeEvent
	public static void handleScroll(InputEvent.MouseScrollingEvent event){
		double delta = event.getScrollDelta();
		
		if(sneaking && delta != 0.0){
			Player player = MCUtil.getPlayer();
			
			DebugItem.ClientInputHandler.onSneakScrolling(event, player, delta, sneaking);
			ProjectorItem.ClientInputHandler.onSneakScrolling(event, player, delta, sneaking);
		}
	}
}
