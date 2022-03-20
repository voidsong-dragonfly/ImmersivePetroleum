package flaxbeard.immersivepetroleum.common.blocks;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.TickableBE;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class IPBlockBase extends Block{
	public IPBlockBase(Block.Properties props){
		super(props);
	}
	
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new IPBlockItemBase(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
	}
	
	@Nullable
	public static <E extends BlockEntity & TickableBE, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> actual, Supplier<BlockEntityType<E>> expected
	) {
		return createTickerHelper(actual, expected.get(), TickableBE.makeTicker());
	}

	// From vanilla
	@Nullable
	public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker
	) {
		return expected == actual ? (BlockEntityTicker<A>)ticker : null;
	}
}
