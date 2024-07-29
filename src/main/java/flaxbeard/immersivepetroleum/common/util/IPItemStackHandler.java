package flaxbeard.immersivepetroleum.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class IPItemStackHandler extends ItemStackHandler{
	private static final Runnable EMPTY_RUN = () -> {};
	
	@Nonnull
	private Runnable onChange = EMPTY_RUN;
	public IPItemStackHandler(int invSize, IItemHandler other){
		super(invSize);
		if(other instanceof IPItemStackHandler)
			for(int i = 0; i < Math.min(getSlots(), other.getSlots()); ++i)
				setStackInSlot(i, other.getStackInSlot(i));
	}
	
	public void setTile(BlockEntity tile){
		this.onChange = tile != null ? tile::setChanged : EMPTY_RUN;
	}
	
	public void setInventoryForUpdate(Container inv){
		this.onChange = inv != null ? inv::setChanged : EMPTY_RUN;
	}
	
	protected void onContentsChanged(int slot){
		super.onContentsChanged(slot);
		this.onChange.run();
	}
	
	public NonNullList<ItemStack> getContainedItems(){
		return this.stacks;
	}
}
