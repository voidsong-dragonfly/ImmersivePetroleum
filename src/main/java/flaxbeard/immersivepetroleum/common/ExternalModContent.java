package flaxbeard.immersivepetroleum.common;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ExternalModContent{
	// Blocks
	private static DeferredHolder<Block, Block> IE_REDSTONE_ENGINEERING_BLOCK;
	
	// Items
	private static DeferredHolder<Item, Item> IE_ITEM_PIPE;
	private static DeferredHolder<Item, Item> IE_ITEM_BUCKSHOT;
	private static DeferredHolder<Item, Item> IE_ITEM_EMPTY_SHELL;
	
	// Fluids
	private static DeferredHolder<Fluid, Fluid> IE_FLUID_CONCRETE;
	
	public static void init(){
		IE_REDSTONE_ENGINEERING_BLOCK = getBlock(ResourceUtils.ie("rs_engineering"));
		
		IE_ITEM_PIPE = getItem(ResourceUtils.ie("fluid_pipe"));
		IE_ITEM_BUCKSHOT = getItem(ResourceUtils.ie("buckshot"));
		IE_ITEM_EMPTY_SHELL = getItem(ResourceUtils.ie("empty_shell"));
		IE_FLUID_CONCRETE = getFluid(ResourceUtils.ie("concrete"));
	}
	
	private static DeferredHolder<Block, Block> getBlock(ResourceLocation name){
		return DeferredHolder.create(BuiltInRegistries.BLOCK.key(), name);
	}
	
	private static DeferredHolder<Item, Item> getItem(ResourceLocation name){
		return DeferredHolder.create(BuiltInRegistries.ITEM.key(), name);
	}
	
	private static DeferredHolder<Fluid, Fluid> getFluid(ResourceLocation name){
		return DeferredHolder.create(BuiltInRegistries.FLUID.key(), name);
	}
	
	
	public static Fluid getIEFluid_Concrete(){
		return IE_FLUID_CONCRETE.get();
	}
	
	public static FluidStack getIEFluid_Concrete(int amount){
		return new FluidStack(IE_FLUID_CONCRETE.get(), amount);
	}
	
	public static Block getIEBlock_RedstoneEngineering(){
		return IE_REDSTONE_ENGINEERING_BLOCK.get();
	}
	
	public static Item getIEItem_Buckshot(){
		return IE_ITEM_BUCKSHOT.get();
	}
	
	public static Item getIEItem_EmptyShell(){
		return IE_ITEM_EMPTY_SHELL.get();
	}
	
	public static Item getIEItem_Pipe(){
		return IE_ITEM_PIPE.get();
	}
	
	public static boolean isIEBlock_RedstoneEngineering(Block block){
		return block.equals(getIEBlock_RedstoneEngineering());
	}
	
	public static boolean isIEItem_Buckshot(ItemStack stack){
		return isIEItem_Buckshot(stack.getItem());
	}
	
	public static boolean isIEItem_Buckshot(Item item){
		return item.equals(getIEItem_Buckshot());
	}
	
	public static boolean isIEItem_EmptyShell(ItemStack stack){
		return isIEItem_EmptyShell(stack.getItem());
	}
	
	public static boolean isIEItem_EmptyShell(Item item){
		return item.equals(getIEItem_EmptyShell());
	}
	
	public static boolean isIEItem_Pipe(ItemStack stack){
		return isIEItem_Pipe(stack.getItem());
	}
	
	public static boolean isIEItem_Pipe(Item item){
		return item.equals(getIEItem_Pipe());
	}
	
	private ExternalModContent(){
	}
}
