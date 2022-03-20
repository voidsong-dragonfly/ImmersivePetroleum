package flaxbeard.immersivepetroleum.client;

import java.io.IOException;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// See ShaderUtil
//@EventBusSubscriber(value = Dist.CLIENT, modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPShaders{

	private static ShaderInstance lineShader;
	private static ShaderInstance alphaShader;
	
	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException{
		event.registerShader(new ShaderInstance(event.getResourceManager(), rl("rendertype_translucent_lines"), DefaultVertexFormat.POSITION_COLOR), s -> {
			lineShader = s;
		});
		
		event.registerShader(new ShaderInstance(event.getResourceManager(), rl("alpha"), DefaultVertexFormat.POSITION_COLOR), s -> {
			alphaShader = s;
		});
	}
	
	public static ShaderInstance getTranslucentLineShader(){
		return lineShader;
	}
	
	public static ShaderInstance getProjectionStaticShader(){
		return alphaShader;
	}
	
	private static ResourceLocation rl(String path){
		return new ResourceLocation(ImmersivePetroleum.MODID, path);
	}
}
