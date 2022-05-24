package flaxbeard.immersivepetroleum.client;

import java.io.IOException;

import com.mojang.blaze3d.shaders.AbstractUniform;
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
	private static ShaderInstance translucent_postion_color;

	private static AbstractUniform projection_alpha;
	private static AbstractUniform projection_time;
	public static void projNoise(float alpha, float time){
		IPShaders.projection_alpha.set(alpha);
		IPShaders.projection_time.set(time);
	}
	
	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException{
		event.registerShader(new ShaderInstance(event.getResourceManager(), rl("rendertype_line"), DefaultVertexFormat.POSITION_COLOR), s -> {
			ImmersivePetroleum.log.info("rendertype_line shader loaded.");
			lineShader = s;
		});
		
		event.registerShader(new ShaderInstance(event.getResourceManager(), rl("rendertype_projection"), DefaultVertexFormat.POSITION_COLOR_TEX), s -> {
			ImmersivePetroleum.log.info("rendertype_projection shader loaded.");
			projection_noise = s;
			
			projection_alpha = projection_noise.safeGetUniform("Alpha");
			projection_time = projection_noise.safeGetUniform("Time");
		});
		
		event.registerShader(new ShaderInstance(event.getResourceManager(), rl("rendertype_translucent_postion_color"), DefaultVertexFormat.POSITION_COLOR), s -> {
			ImmersivePetroleum.log.info("rendertype_translucent_postion_color shader loaded.");
			translucent_postion_color = s;
		});
	}
	
	public static ShaderInstance getTranslucentLineShader(){
		return lineShader;
	}
	
	public static ShaderInstance getProjectionStaticShader(){
		return projection_noise;
	}
	
	public static ShaderInstance getTranslucentPostionColorShader(){
		return translucent_postion_color;
	}
	
	private static ResourceLocation rl(String path){
		return new ResourceLocation(ImmersivePetroleum.MODID, path);
	}
}
