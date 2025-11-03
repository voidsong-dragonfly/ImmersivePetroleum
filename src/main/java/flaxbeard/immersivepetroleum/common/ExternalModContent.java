package flaxbeard.immersivepetroleum.common;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ExternalModContent{
	
	public static void init(){
		IE.forceClassLoad();
	}
	
	/**
	 * ImmersiveEngineering
	 */
	public static class IE{
		private static Loader loader = new Loader(ResourceUtils::ie);
		
		private static RegistryObject<Block> BLOCK_REDSTONE_ENGINEERING = loader.block("rs_engineering");
		private static RegistryObject<Item> ITEM_HAMMER = loader.item("hammer");
		private static RegistryObject<Item> ITEM_PIPE = loader.item("fluid_pipe");
		private static RegistryObject<Item> ITEM_BUCKSHOT = loader.item("buckshot");
		private static RegistryObject<Item> ITEM_EMPTY_SHELL = loader.item("empty_shell");
		private static RegistryObject<Fluid> FLUID_CONCRETE = loader.fluid("concrete");
		
		public static Fluid fluidConcrete(){
			return FLUID_CONCRETE.get();
		}
		
		public static FluidStack fluidConcrete(int amount){
			return new FluidStack(FLUID_CONCRETE.get(), amount);
		}
		
		public static Block blockRSEngineering(){
			return BLOCK_REDSTONE_ENGINEERING.get();
		}
		
		public static Item itemBuckshot(){
			return ITEM_BUCKSHOT.get();
		}
		
		public static Item itemEmptyShell(){
			return ITEM_EMPTY_SHELL.get();
		}
		
		public static Item itemPipe(){
			return ITEM_PIPE.get();
		}
		
		public static Item itemHammer(){
			return ITEM_HAMMER.get();
		}
		
		public static boolean isConcrete(FluidStack fluid){
			return isConcrete(fluid.getFluid());
		}
		
		public static boolean isConcrete(Fluid fluid){
			return fluidConcrete().equals(fluid);
		}
		
		public static boolean isRedstoneEngineering(Block block){
			return blockRSEngineering().equals(block);
		}
		
		public static boolean isBuckshot(ItemStack stack){
			return isBuckshot(stack.getItem());
		}
		
		public static boolean isBuckshot(Item item){
			return itemBuckshot().equals(item);
		}
		
		public static boolean isEmptyShell(ItemStack stack){
			return isEmptyShell(stack.getItem());
		}
		
		public static boolean isEmptyShell(Item item){
			return itemEmptyShell().equals(item);
		}
		
		public static boolean isPipe(ItemStack stack){
			return isPipe(stack.getItem());
		}
		
		public static boolean isPipe(Item item){
			return itemPipe().equals(item);
		}
		
		public static boolean isHammer(ItemStack stack){
			return isHammer(stack.getItem());
		}
		
		public static boolean isHammer(Item item){
			return itemHammer().equals(item);
		}
		
		private static void forceClassLoad(){
		}
	}
	
	/* Not really the best name for this, but better than nothing */
	private record Loader(Function<String, ResourceLocation> modLoc){
		public RegistryObject<Block> block(String name){
			return RegistryObject.create(this.modLoc.apply(name), ForgeRegistries.BLOCKS);
		}
		
		public RegistryObject<Item> item(String name){
			return RegistryObject.create(this.modLoc.apply(name), ForgeRegistries.ITEMS);
		}
		
		public RegistryObject<Fluid> fluid(String name){
			return RegistryObject.create(this.modLoc.apply(name), ForgeRegistries.FLUIDS);
		}
	}
}
