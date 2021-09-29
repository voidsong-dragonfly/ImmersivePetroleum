package flaxbeard.immersivepetroleum.common.cfg;

import java.util.function.Predicate;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPClientConfig{
	public static final Miscellaneous MISCELLANEOUS;
	public static final GridColors GRID_COLORS;
	
	public static final ForgeConfigSpec ALL;
	
	static{
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		GRID_COLORS = new GridColors(builder);
		MISCELLANEOUS = new Miscellaneous(builder);
		ALL = builder.build();
	}
	
	public static class GridColors{
		public final ConfigValue<String> pipe_normal_color;
		public final ConfigValue<String> pipe_perforated_color;
		public final ConfigValue<String> pipe_perforated_fixed_color;
		GridColors(ForgeConfigSpec.Builder builder){
			builder.push("GridColors");
			
			Predicate<Object> hexValidator = obj -> {
				if(obj != null && obj instanceof String){
					String str = (String) obj;
					if(str.length() == 6){
						try{
							Integer.valueOf(str, 16);
							return true;
						}catch(NumberFormatException e){
						}
					}
					ImmersivePetroleum.log.error("\"{}\" is not a valid RGB Hex color.", str);
				}
				
				return false;
			};
			
			pipe_normal_color = builder
					.comment("Normal pipe color. (Hex RGB)")
					.define("normal_pipe_color", "A5A5A5", hexValidator);
			
			pipe_perforated_color = builder
					.comment("Perforated pipe color. (Hex RGB)")
					.define("perforated_pipe_color", "54FF54", hexValidator);
			
			pipe_perforated_fixed_color = builder
					.comment("Perforated pipe color. (Hex RGB)")
					.define("fixed_perforated_pipe_color", "FF515A", hexValidator);
			
			builder.pop();
		}
	}
	
	public static class Miscellaneous{
		public final BooleanValue sample_displayBorder;
		Miscellaneous(ForgeConfigSpec.Builder builder){
			builder.push("Miscellaneous");
			
			sample_displayBorder = builder
					.comment("Unused for now!", "Display chunk border while holding Core Samples, default=true")
					.define("sample_displayBorder", true);
			
			builder.pop();
		}
	}
	
	@SubscribeEvent
	public static void onConfigChange(ModConfigEvent ev){
		
	}
}
