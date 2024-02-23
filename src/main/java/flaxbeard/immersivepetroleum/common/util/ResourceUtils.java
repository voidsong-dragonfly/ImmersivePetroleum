package flaxbeard.immersivepetroleum.common.util;

import blusunrize.immersiveengineering.api.Lib;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;

public class ResourceUtils{
	public static ResourceLocation ip(String str){
		return new ResourceLocation(ImmersivePetroleum.MODID, str);
	}
	
	public static ResourceLocation ct(String str){
		return new ResourceLocation("crafttweaker", str);
	}
	
	public static ResourceLocation ie(String str){
		return new ResourceLocation(Lib.MODID, str);
	}
	
	// TODO Remember to rename this later
	public static ResourceLocation forge(String str){
		return new ResourceLocation(NeoForgeVersion.MOD_ID, str);
	}
	
	public static ResourceLocation mc(String str){
		return new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, str);
	}
}
