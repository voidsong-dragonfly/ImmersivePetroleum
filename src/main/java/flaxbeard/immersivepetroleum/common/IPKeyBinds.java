package flaxbeard.immersivepetroleum.common;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class IPKeyBinds{
	public static final KeyMapping keybind_preview_flip = new KeyMapping("key.immersivepetroleum.projector.flip", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.categories.immersivepetroleum");
	
	@SubscribeEvent
	public static void registerKeybind(RegisterKeyMappingsEvent event){
		keybind_preview_flip.setKeyConflictContext(KeyConflictContext.IN_GAME);
		
		event.register(keybind_preview_flip);
	}
}
