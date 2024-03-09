package flaxbeard.immersivepetroleum.common.util;

import blusunrize.immersiveengineering.api.Lib;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;

/**
 * Small {@link ResourceLocation}-Utility class for Frequently used namespaces
 * 
 * @author TwistedGate
 */
public class ResourceUtils{
	/** Immersive Petroleum namespace */
	public static ResourceLocation ip(String path){
		return new ResourceLocation(ImmersivePetroleum.MODID, path);
	}
	
	/** Immersive Engineering namespace */
	public static ResourceLocation ie(String path){
		return new ResourceLocation(Lib.MODID, path);
	}
	
	/** Craft Tweaker namespace */
	@Deprecated(forRemoval = true)
	public static ResourceLocation ct(String path){
		return new ResourceLocation("crafttweaker", path);
	}
	
	/** NeoForge namespace */
	// TODO Remember to rename this later
	public static ResourceLocation forge(String path){
		return new ResourceLocation(NeoForgeVersion.MOD_ID, path);
	}
	
	/** Minecraft namespace */
	public static ResourceLocation mc(String path){
		return new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, path);
	}
}
