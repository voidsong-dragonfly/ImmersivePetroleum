package flaxbeard.immersivepetroleum.common.util;

import blusunrize.immersiveengineering.api.Lib;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public class ResourceUtils{
	public static final ResourceLocation ip(String str){
		return new ResourceLocation(ImmersivePetroleum.MODID, str);
	}
	
	/*// TODO
	public static ResourceLocation ct(String str){
		return new ResourceLocation(CraftTweaker.MODID, str);
	}
	*/
	
	public static ResourceLocation ie(String str){
		return new ResourceLocation(Lib.MODID, str);
	}
	
	public static ResourceLocation forge(String str){
		return new ResourceLocation(ForgeVersion.MOD_ID, str);
	}
	
	public static final ResourceLocation mc(String str){
		return new ResourceLocation("minecraft", str);
	}
}
