package flaxbeard.immersivepetroleum.common;

import blusunrize.immersiveengineering.api.Lib;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class ExternalModContent{
	public static RegistryObject<Fluid> IE_CONCRETE_FLUID;
	
	public static final void init(){
		IE_CONCRETE_FLUID = RegistryObject.of(new ResourceLocation(Lib.MODID, "concrete"), ForgeRegistries.FLUIDS);
		// TODO IEBlocks.MetalDevices.sampleDrill for CommonEventHandler.handlePickupItem
	}
}
