package flaxbeard.immersivepetroleum.client;

import java.io.IOException;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

// See ShaderUtil
@EventBusSubscriber(value = Dist.CLIENT, modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPShaders{

	private static ShaderInstance lineShader;
	private static ShaderInstance projection_noise;

	public static Uniform projection_alpha;
	public static Uniform projection_time;
	public static void projNoise(float alpha, float time){
		if(IPShaders.projection_alpha != null) IPShaders.projection_alpha.set(alpha);
		if(IPShaders.projection_time != null)  IPShaders.projection_time.set(time);
	}
	
	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException{
//		event.registerShader(new ShaderInstance(event.getResourceManager(), rl("rendertype_translucent_lines"), DefaultVertexFormat.POSITION_COLOR), s -> {
//			lineShader = s;
//		});
		
		event.registerShader(new ShaderInstance(event.getResourceManager(), rl("projection_noise"), DefaultVertexFormat.POSITION_COLOR_TEX), s -> {
			ImmersivePetroleum.log.info("projection_noise shader loaded.");
			projection_noise = s;
			
			projection_alpha = projection_noise.getUniform("alpha");
			projection_time = projection_noise.getUniform("time");
			
			ImmersivePetroleum.log.info("projection_alpha = " + projection_alpha);
			ImmersivePetroleum.log.info("projection_time = " + projection_time);
		});
	}
	
	public static ShaderInstance getTranslucentLineShader(){
		return lineShader;
	}
	
	public static ShaderInstance getProjectionStaticShader(){
		return projection_noise;
	}
	
	private static ResourceLocation rl(String path){
		return new ResourceLocation(ImmersivePetroleum.MODID, path);
	}
}
