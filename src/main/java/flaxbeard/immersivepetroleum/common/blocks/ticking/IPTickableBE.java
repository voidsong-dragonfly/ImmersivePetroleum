package flaxbeard.immersivepetroleum.common.blocks.ticking;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

// This should ideally be split into client- and server-tickable interfaces at some point. ~malte0811
/**
 * @deprecated Will be removed when stuff in {@link AutoLubricatorTileEntity#tick()} has been sorted out!
 */
@Deprecated(forRemoval = true)
public interface IPTickableBE {
	@Deprecated(forRemoval = true)
	void tick();
	
	@Deprecated(forRemoval = true)
	static <T extends BlockEntity & IPTickableBE>BlockEntityTicker<T> makeTicker() {
		return (level, pos, state, be) -> be.tick();
	}
}
