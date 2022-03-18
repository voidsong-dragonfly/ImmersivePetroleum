package flaxbeard.immersivepetroleum.common.blocks;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;

public class IPMetalMultiblock<T extends MultiblockPartBlockEntity<T>> extends MetalMultiblockBlock<T>{
	public IPMetalMultiblock(MultiblockBEType<T> te, Properties props){
		super(te, props);
	}
	
	/*
	public IPMetalMultiblock(String name, Supplier<BlockEntityType<T>> te){
		super(name, te);
		
		// Nessesary hacks
		if(!FMLLoader.isProduction()){
			IEContent.registeredIEBlocks.remove(this);
			Iterator<Item> it = IEContent.registeredIEItems.iterator();
			while(it.hasNext()){
				Item item = it.next();
				if(item instanceof BlockItemIE && ((BlockItemIE) item).getBlock() == this){
					it.remove();
					break;
				}
			}
		}
		
		IPContent.registeredIPBlocks.add(this);
		
		BlockItem bItem = new BlockItemIE(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
		IPContent.registeredIPItems.add(bItem.setRegistryName(getRegistryName()));
	}
	*/
}
