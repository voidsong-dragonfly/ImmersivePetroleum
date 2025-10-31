package flaxbeard.immersivepetroleum.common.util;

import blusunrize.immersiveengineering.api.Lib;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public class ResourceUtils{
	public static ResourceLocation ip(String str){
		return ResourceLocation.fromNamespaceAndPath(ImmersivePetroleum.MODID, str);
	}
	
	public static ResourceLocation ct(String str){
		return ResourceLocation.fromNamespaceAndPath(CraftTweakerConstants.MOD_ID, str);
	}
	
	public static ResourceLocation ie(String str){
		return ResourceLocation.fromNamespaceAndPath(Lib.MODID, str);
	}
	
	public static ResourceLocation forge(String str){
		return ResourceLocation.fromNamespaceAndPath(ForgeVersion.MOD_ID, str);
	}
	
	public static ResourceLocation mc(String str){
		return ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, str);
	}
}
