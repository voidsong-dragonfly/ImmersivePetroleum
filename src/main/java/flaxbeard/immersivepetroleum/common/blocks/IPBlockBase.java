package flaxbeard.immersivepetroleum.common.blocks;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPTickableBE;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class IPBlockBase extends Block{
	public IPBlockBase(Block.Properties props){
		super(props);
	}
	
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new IPBlockItemBase(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
	}
	
	@Nullable
	public static <E extends BlockEntity & IPServerTickableTile & IPClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(boolean isClient, BlockEntityType<A> actual, RegistryObject<BlockEntityType<E>> expected){
		return createTickerHelper(isClient, actual, expected.get());
	}
	
	@Nullable
	public static <E extends BlockEntity & IPServerTickableTile & IPClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(boolean isClient, BlockEntityType<A> actual, BlockEntityType<E> expected){
		if(isClient){
//			if(actual instanceof IPClientTickableTile){
				return createClientTickerHelper(actual, expected);
//			}
		}else{
//			if(actual instanceof IPServerTickableTile){
				return createServerTickerHelper(actual, expected);
//			}
		}
//		return null;
	}
	
	@Nullable
	public static <E extends BlockEntity & IPServerTickableTile, A extends BlockEntity> BlockEntityTicker<A> createServerTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, IPServerTickableTile::makeTicker);
	}
	
	@Nullable
	public static <E extends BlockEntity & IPClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createClientTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, IPClientTickableTile::makeTicker);
	}
	
	/** @deprecated See {@link IPTickableBE} */
	@Nullable
	@Deprecated(forRemoval = true)
	public static <E extends BlockEntity & IPTickableBE, A extends BlockEntity> BlockEntityTicker<A> createGenericTickerHelper(BlockEntityType<A> actual, RegistryObject<BlockEntityType<E>> expected){
		return createTickerHelper(actual, expected.get(), IPTickableBE::makeTicker);
	}
	
	/** @deprecated See {@link IPTickableBE} */
	@Nullable
	@Deprecated(forRemoval = true)
	public static <E extends BlockEntity & IPTickableBE, A extends BlockEntity> BlockEntityTicker<A> createGenericTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, IPTickableBE::makeTicker);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, Supplier<BlockEntityTicker<? super E>> ticker){
		return expected == actual ? (BlockEntityTicker<A>) ticker.get() : null;
	}
}
