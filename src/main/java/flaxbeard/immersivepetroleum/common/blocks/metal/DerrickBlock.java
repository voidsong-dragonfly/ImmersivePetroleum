package flaxbeard.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DerrickBlock extends IPMetalMultiblock<DerrickTileEntity>{
	public DerrickBlock(){
		super(IPTileTypes.DERRICK);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(!player.getItemInHand(hand).isEmpty()){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof DerrickTileEntity){
				DerrickTileEntity derrick = (DerrickTileEntity) te;
				BlockPos tPos = derrick.posInMultiblock;
				Direction facing = derrick.getFacing();
				
				// Locations that don't require sneaking to avoid the GUI
				
				// Power input
				if(DerrickTileEntity.Energy_IN.contains(tPos) && hit.getDirection() == Direction.UP){
					return InteractionResult.FAIL;
				}
				
				if(DerrickTileEntity.Redstone_IN.contains(tPos) && (derrick.getIsMirrored() ? hit.getDirection() == facing.getClockWise() : hit.getDirection() == facing.getCounterClockWise())){
					return InteractionResult.FAIL;
				}
			}
		}
		
		return super.use(state, world, pos, player, hand, hit);
	}
	
	@Override
	public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof DerrickTileEntity){
			return ((DerrickTileEntity) te).isLadder();
		}
		return false;
	}
}
