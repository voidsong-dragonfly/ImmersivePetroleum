package flaxbeard.immersivepetroleum.common.blocks.multiblocks.util;

import net.minecraft.nbt.CompoundTag;

public interface IReadWriteNBT{
	public CompoundTag writeNBT();
	public void readNBT(CompoundTag nbt);
}
