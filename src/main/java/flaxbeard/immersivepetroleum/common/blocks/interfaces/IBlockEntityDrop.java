package flaxbeard.immersivepetroleum.common.blocks.interfaces;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public interface IBlockEntityDrop{
	@Nonnull
	List<ItemStack> getBlockEntityDrop(LootContext context);
	
	default ItemStack getFirstBlockEntityDrop(){
		return getBlockEntityDrop(null).get(0);
	}
}
