package flaxbeard.immersivepetroleum.common.cfg;

import java.util.List;

import flaxbeard.immersivepetroleum.api.energy.FuelHandler;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ConfigUtils{
	
	public static void addGeneratorFuel(List<? extends String> list){
		for(int i = 0;i < list.size();i++){
			String str = list.get(i);
			
			if(!str.isEmpty()){
				// Splits the string with "," regardless of how many " " there are, from 0 up to infinity
				String[] split = str.split(", {0,}");
				
				if(split.length < 3){
					throw new IllegalArgumentException("Missing values in \"" + str + "\".");
				}
				
				ResourceLocation fluidRL = null;
				int mbPerTick = 0;
				int fluxPerTick = 0;
				
				try{
					fluidRL = new ResourceLocation(split[0].trim());
				}catch(ResourceLocationException e){
					throw new IllegalArgumentException(e);
				}
				
				try{
					mbPerTick = Integer.valueOf(split[1].trim());
					if(mbPerTick < 0){
						throw new IllegalArgumentException("Negative value for fuel mB/tick for generator fuel " + (i + 1));
					}
				}catch(NumberFormatException e){
					throw new IllegalArgumentException("Invalid value for fuel mB/tick for generator fuel " + (i + 1), e);
				}
				
				try{
					fluxPerTick = Integer.valueOf(split[2].trim());
					if(fluxPerTick < 0){
						throw new IllegalArgumentException("Negative value for fuel RF/tick for generator fuel " + (i + 1));
					}
				}catch(NumberFormatException e){
					throw new IllegalArgumentException("Invalid value for fuel RF/tick for generator fuel " + (i + 1), e);
				}
				
				if(!BuiltInRegistries.FLUID.containsKey(fluidRL)){
					throw new RuntimeException("\"" + fluidRL + "\" did not resolve into a valid fluid. (" + fluidRL + ")");
				}
				
				FuelHandler.registerPortableGeneratorFuel(fluidRL, fluxPerTick, mbPerTick);
			}
		}
	}
	
	public static void addBoatFuel(List<? extends String> list){
		for(int i = 0;i < list.size();i++){
			String str = list.get(i);
			
			if(!str.isEmpty()){
				// Splits the string with "," regardless of how many " " there are, from 0 up to infinity
				String[] split = str.split(", {0,}");
				
				if(split.length < 2){
					throw new IllegalArgumentException("Missing values in \"" + str + "\".");
				}
				
				ResourceLocation fluidRL = null;
				int mbPerTick = 0;
				
				try{
					fluidRL = new ResourceLocation(split[0].trim());
				}catch(ResourceLocationException e){
					throw new IllegalArgumentException(e);
				}
				
				try{
					mbPerTick = Integer.valueOf(split[1].trim());
					if(mbPerTick < 0){
						throw new IllegalArgumentException("Negative value for fuel mB/tick for boat fuel " + (i + 1));
					}
				}catch(NumberFormatException e){
					throw new IllegalArgumentException("Invalid value for fuel mB/tick for boat fuel " + (i + 1), e);
				}
				
				if(!BuiltInRegistries.FLUID.containsKey(fluidRL)){
					throw new RuntimeException("\"" + fluidRL + "\" did not resolve into a valid fluid. (" + fluidRL + ")");
				}
				
				FuelHandler.registerMotorboatFuel(fluidRL, mbPerTick);
			}
		}
	}
}
