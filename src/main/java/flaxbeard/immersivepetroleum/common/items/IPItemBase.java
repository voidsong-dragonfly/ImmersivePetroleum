package flaxbeard.immersivepetroleum.common.items;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.world.item.Item;

public class IPItemBase extends Item{
	/** For basic items */
	public IPItemBase(){
		this(new Item.Properties().tab(ImmersivePetroleum.creativeTab));
	}
	
	/** For items that require special attention */
	public IPItemBase(Item.Properties properties){
		super(properties);
	}
}
