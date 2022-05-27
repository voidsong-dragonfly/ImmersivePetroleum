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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class IPTags{
	private static final Map<TagKey<Block>, TagKey<Item>> toItemTag = new HashMap<>();
	
	public static class Blocks{
		public static final TagKey<Block> asphalt = createBlockTag(forgeLoc("asphalt"));
		public static final TagKey<Block> petcoke = createBlockTag(forgeLoc("storage_blocks/petcoke"));
	}
	
	public static class Items{
		public static final TagKey<Item> bitumen = createItemWrapper(forgeLoc("bitumen"));
		public static final TagKey<Item> petcoke = createItemWrapper(forgeLoc("coal_petcoke"));
		public static final TagKey<Item> petcokeDust = createItemWrapper(forgeLoc("dusts/coal_petcoke"));
		public static final TagKey<Item> petcokeStorage = createItemWrapper(forgeLoc("storage_blocks/coal_petcoke"));
	}
	
	public static class Fluids{
		public static final TagKey<Fluid> crudeOil = createFluidWrapper(forgeLoc("crude_oil"));
		public static final TagKey<Fluid> diesel = createFluidWrapper(forgeLoc("diesel"));
		public static final TagKey<Fluid> diesel_sulfur = createFluidWrapper(forgeLoc("diesel_sulfur"));
		public static final TagKey<Fluid> gasoline = createFluidWrapper(forgeLoc("gasoline"));
		public static final TagKey<Fluid> lubricant = createFluidWrapper(forgeLoc("lubricant"));
		public static final TagKey<Fluid> napalm = createFluidWrapper(forgeLoc("napalm"));
	}
	
	public static class Utility{
		public static final TagKey<Fluid> burnableInFlarestack = createFluidWrapper(modLoc("burnable_in_flarestack"));
	}
	
	public static TagKey<Item> getItemTag(TagKey<Block> blockTag){
		Preconditions.checkArgument(toItemTag.containsKey(blockTag));
		return toItemTag.get(blockTag);
	}
	
	private static TagKey<Block> createBlockTag(ResourceLocation name){
		TagKey<Block> blockTag = createBlockWrapper(name);
		toItemTag.put(blockTag, createItemWrapper(name));
		return blockTag;
	}
	
	public static void forAllBlocktags(BiConsumer<TagKey<Block>, TagKey<Item>> out){
		for(Entry<TagKey<Block>, TagKey<Item>> entry:toItemTag.entrySet())
			out.accept(entry.getKey(), entry.getValue());
	}
	
	private static TagKey<Block> createBlockWrapper(ResourceLocation name){
		return BlockTags.create(name);
	}
	
	private static TagKey<Item> createItemWrapper(ResourceLocation name){
		return ItemTags.create(name);
	}
	
	private static TagKey<Fluid> createFluidWrapper(ResourceLocation name){
		return FluidTags.create(name);
	}
	
	private static ResourceLocation forgeLoc(String path){
		return new ResourceLocation("forge", path);
	}
	
	private static ResourceLocation modLoc(String path){
		return new ResourceLocation(ImmersivePetroleum.MODID, path);
	}
}
