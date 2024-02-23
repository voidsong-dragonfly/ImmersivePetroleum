package flaxbeard.immersivepetroleum.common.blocks.metal;

import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockDummy extends IPBlockBase{
	public BlockDummy(){
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion().pushReaction(PushReaction.BLOCK));
	}
	
	@Override
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new BlockItem(this, new Item.Properties());
	}
}
