package flaxbeard.immersivepetroleum.common.blocks;

import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class IPBlockBase extends Block{
	public IPBlockBase(Block.Properties props){
		super(props);
	}
	
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new IPBlockItemBase(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
	}
	
	@Deprecated
	public IPBlockBase(String name, Block.Properties props){
		super(props);
		throw new UnsupportedOperationException();
	}
	
	@Deprecated
	protected BlockItem createBlockItem(){
		throw new UnsupportedOperationException();
//		return new IPBlockItemBase(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
	}
}
