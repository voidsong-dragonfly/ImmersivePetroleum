package flaxbeard.immersivepetroleum.common.blocks.metal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated
public class DerrickBlock extends Block{// extends IPMetalMultiblock<DerrickTileEntity>{
	public DerrickBlock(){
		super(null);
//		super(IPTileTypes.DERRICK);
	}
	
	@Override
	public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity){
		BlockEntity te = world.getBlockEntity(pos);
//		if(te instanceof DerrickTileEntity derrick && derrick.isLadder()){
//			return true;
//		}
		return false;
	}
}
