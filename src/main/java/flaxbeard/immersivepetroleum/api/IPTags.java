package flaxbeard.immersivepetroleum.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.google.common.base.Preconditions;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class IPTags{
	private static final Map<SetTag.Named<Block>, Tag.Named<Item>> toItemTag = new HashMap<>();
	
	public static class Blocks{
		public static final Tag.Named<Block> asphalt = createBlockTag(forgeLoc("asphalt"));
		public static final Tag.Named<Block> petcoke = createBlockTag(forgeLoc("storage_blocks/petcoke"));
	}
	
	public static class Items{
		public static final Tag.Named<Item> bitumen = createItemWrapper(forgeLoc("bitumen"));
		public static final Tag.Named<Item> petcoke = createItemWrapper(forgeLoc("coal_petcoke"));
		public static final Tag.Named<Item> petcokeDust = createItemWrapper(forgeLoc("dusts/coal_petcoke"));
		public static final Tag.Named<Item> petcokeStorage = createItemWrapper(forgeLoc("storage_blocks/coal_petcoke"));
	}
	
	public static class Fluids{
		public static final Tag.Named<Fluid> crudeOil = createFluidWrapper(forgeLoc("crude_oil"));
		public static final Tag.Named<Fluid> diesel = createFluidWrapper(forgeLoc("diesel"));
		public static final Tag.Named<Fluid> diesel_sulfur = createFluidWrapper(forgeLoc("diesel_sulfur"));
		public static final Tag.Named<Fluid> gasoline = createFluidWrapper(forgeLoc("gasoline"));
		public static final Tag.Named<Fluid> lubricant = createFluidWrapper(forgeLoc("lubricant"));
		public static final Tag.Named<Fluid> napalm = createFluidWrapper(forgeLoc("napalm"));
	}
	
	public static class Utility{
		public static final Tag.Named<Fluid> burnableInFlarestack = createFluidWrapper(modLoc("burnable_in_flarestack"));
	}
	
	public static Tag.Named<Item> getItemTag(Tag.Named<Block> blockTag){
		Preconditions.checkArgument(toItemTag.containsKey(blockTag));
		return toItemTag.get(blockTag);
	}
	
	private static Tag.Named<Block> createBlockTag(ResourceLocation name){
		Tag.Named<Block> blockTag = createBlockWrapper(name);
		toItemTag.put(blockTag, createItemWrapper(name));
		return blockTag;
	}
	
	public static void forAllBlocktags(BiConsumer<Named<Block>, Named<Item>> out){
		for(Entry<Named<Block>, Named<Item>> entry:toItemTag.entrySet())
			out.accept(entry.getKey(), entry.getValue());
	}
	
	private static Tag.Named<Block> createBlockWrapper(ResourceLocation name){
		return BlockTags.bind(name.toString());
	}
	
	private static Tag.Named<Item> createItemWrapper(ResourceLocation name){
		return ItemTags.bind(name.toString());
	}
	
	private static Tag.Named<Fluid> createFluidWrapper(ResourceLocation name){
		return FluidTags.bind(name.toString());
	}
	
	private static ResourceLocation forgeLoc(String path){
		return new ResourceLocation("forge", path);
	}
	
	private static ResourceLocation modLoc(String path){
		return new ResourceLocation(ImmersivePetroleum.MODID, path);
	}
}
