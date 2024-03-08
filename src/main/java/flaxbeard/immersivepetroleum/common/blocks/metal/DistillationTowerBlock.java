package flaxbeard.immersivepetroleum.common.blocks.metal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated
public class DistillationTowerBlock extends Block{// extends IPMetalMultiblock<DistillationTowerTileEntity>{
	public DistillationTowerBlock(){
		super(null);
//		super(IPTileTypes.TOWER);
	}
	
	@Override
	public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity){
		BlockEntity te = world.getBlockEntity(pos);
//		if(te instanceof DistillationTowerTileEntity tower && tower.isLadder()){
//			return true;
//		}
		return false;
	}
}
