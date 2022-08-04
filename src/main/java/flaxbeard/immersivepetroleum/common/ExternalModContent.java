package flaxbeard.immersivepetroleum.common;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ExternalModContent{
	public static RegistryObject<Item> IE_ITEM_PIPE;
	public static RegistryObject<Item> IE_ITEM_BUCKSHOT;
	public static RegistryObject<Item> IE_ITEM_EMPTY_SHELL;
	
	public static RegistryObject<Fluid> IE_FLUID_CONCRETE_FLUID;
	
	public static void init(){
		IE_ITEM_PIPE = RegistryObject.create(ResourceUtils.ie("fluid_pipe"), ForgeRegistries.ITEMS);
		IE_ITEM_BUCKSHOT = RegistryObject.create(ResourceUtils.ie("buckshot"), ForgeRegistries.ITEMS);
		IE_ITEM_EMPTY_SHELL = RegistryObject.create(ResourceUtils.ie("empty_shell"), ForgeRegistries.ITEMS);
		
		IE_FLUID_CONCRETE_FLUID = RegistryObject.create(ResourceUtils.ie("concrete"), ForgeRegistries.FLUIDS);
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
