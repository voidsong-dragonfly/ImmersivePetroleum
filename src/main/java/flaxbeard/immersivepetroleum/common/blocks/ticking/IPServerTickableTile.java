package flaxbeard.immersivepetroleum.common.blocks.ticking;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface IPServerTickableTile{
	void tickServer();
	
	static <T extends BlockEntity & IPServerTickableTile> BlockEntityTicker<T> makeTicker(){
		return (level, pos, state, te) -> te.tickServer();
	}
}
