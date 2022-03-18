package flaxbeard.immersivepetroleum.common.blocks.metal;

import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockDummy extends IPBlockBase{
	private static final Material Material = new Material(MaterialColor.METAL, false, false, true, true, false, false, PushReaction.BLOCK);
	
	public BlockDummy(){
		super(Block.Properties.of(Material).noOcclusion());
	}
	
	@Override
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new BlockItem(this, new Item.Properties());
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
	}
}
