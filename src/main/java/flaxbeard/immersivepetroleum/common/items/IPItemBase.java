package flaxbeard.immersivepetroleum.common.items;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.world.item.Item;

public class IPItemBase extends Item implements IColouredItem{
	/** For basic items */
	public IPItemBase(){
		this(new Item.Properties());
	}
	
	/** For items that require special attention */
	public IPItemBase(Item.Properties properties){
		super(properties.tab(ImmersivePetroleum.creativeTab));
	}
	
	/** @deprecated */
	public IPItemBase(String name){
		this(name, new Item.Properties());
		throw new UnsupportedOperationException();
	}
	
	/** @deprecated */
	public IPItemBase(String name, Item.Properties properties){
		super(properties.tab(ImmersivePetroleum.creativeTab));
		throw new UnsupportedOperationException();
	}
}
