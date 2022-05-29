package flaxbeard.immersivepetroleum.common;

import blusunrize.immersiveengineering.api.Lib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ExternalModContent{
	public static RegistryObject<Item> IE_ITEM_PIPE;
	
	public static RegistryObject<Fluid> IE_FLUID_CONCRETE_FLUID;
	
	@SuppressWarnings("removal")
	public static final void init(){
		IE_FLUID_CONCRETE_FLUID = RegistryObject.of(new ResourceLocation(Lib.MODID, "concrete"), ForgeRegistries.FLUIDS);
		IE_ITEM_PIPE = RegistryObject.of(new ResourceLocation(Lib.MODID, "fluid_pipe"), ForgeRegistries.ITEMS);
		// TODO IEBlocks.MetalDevices.sampleDrill for CommonEventHandler.handlePickupItem??
	}
	
	public static boolean isIEConcrete(FluidStack stack){
		return isIEConcrete(stack.getFluid());
	}
	
	public static boolean isIEConcrete(Fluid fluid){
		return fluid.equals(IE_FLUID_CONCRETE_FLUID.get());
	}
	
	public static boolean isIEPipeItem(ItemStack stack){
		return isIEPipeItem(stack.getItem());
	}
	
	public static boolean isIEPipeItem(Item item){
		return item.equals(IE_ITEM_PIPE.get());
	}
	
	public static FluidStack ieConcreteFluidStack(int amount){
		return new FluidStack(IE_FLUID_CONCRETE_FLUID.get(), amount);
	}
	
	public static ItemStack iePipeItemStack(int stackSize){
		return new ItemStack(IE_ITEM_PIPE.get(), stackSize);
	}
}
