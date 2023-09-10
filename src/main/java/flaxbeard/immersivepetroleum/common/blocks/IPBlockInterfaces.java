package flaxbeard.immersivepetroleum.common.blocks;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class IPBlockInterfaces{
	
	// Im lazy c:
	public interface IPlacementReader{
		public void readOnPlacement(LivingEntity placer, ItemStack stack);
	}
}
