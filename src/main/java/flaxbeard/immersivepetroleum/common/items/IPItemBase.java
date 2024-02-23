package flaxbeard.immersivepetroleum.common.items;

import flaxbeard.immersivepetroleum.common.IPCreativeTab.IMightShowUpInCreativeTab;
import net.minecraft.world.item.Item;

public class IPItemBase extends Item implements IMightShowUpInCreativeTab{
	/** For basic items */
	public IPItemBase(){
		this(new Item.Properties());
	}
	
	/** For items that require special attention */
	public IPItemBase(Item.Properties properties){
		super(properties);
	}
}
