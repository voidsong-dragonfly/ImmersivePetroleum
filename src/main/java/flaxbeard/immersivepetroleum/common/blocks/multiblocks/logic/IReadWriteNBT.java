package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import net.minecraft.nbt.CompoundTag;

public interface IReadWriteNBT{
	public CompoundTag writeNBT();
	public void readNBT(CompoundTag nbt);
}
