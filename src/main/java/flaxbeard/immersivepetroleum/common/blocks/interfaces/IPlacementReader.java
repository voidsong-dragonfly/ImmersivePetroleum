package flaxbeard.immersivepetroleum.common.blocks.interfaces;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// Im lazy c:
public interface IPlacementReader{
	public void readOnPlacement(LivingEntity placer, ItemStack stack);
}