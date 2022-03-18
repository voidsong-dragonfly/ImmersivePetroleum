package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

// This should ideally be split into client- and server-tickable interfaces at some point
public interface TickableBE {
	void tick();

	static <T extends BlockEntity & TickableBE>BlockEntityTicker<T> makeTicker() {
		return (level, pos, state, be) -> be.tick();
	}
}
