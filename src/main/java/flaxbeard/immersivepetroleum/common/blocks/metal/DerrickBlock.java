package flaxbeard.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class DerrickBlock extends IPMetalMultiblock<DerrickTileEntity>{
	public DerrickBlock(){
		super("derrick", () -> IPTileTypes.DERRICK.get());
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(!player.getHeldItem(hand).isEmpty()){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof DerrickTileEntity){
				DerrickTileEntity derrick = (DerrickTileEntity) te;
				BlockPos tPos = derrick.posInMultiblock;
				Direction facing = derrick.getFacing();
				
				// Locations that don't require sneaking to avoid the GUI
				
				// Power input
				if(DerrickTileEntity.Energy_IN.contains(tPos) && hit.getFace() == Direction.UP){
					return ActionResultType.FAIL;
				}
				
				if(DerrickTileEntity.Redstone_IN.contains(tPos) && (derrick.getIsMirrored() ? hit.getFace() == facing.rotateY() : hit.getFace() == facing.rotateYCCW())){
					return ActionResultType.FAIL;
				}
			}
		}
		
		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof DerrickTileEntity){
			return ((DerrickTileEntity) te).isLadder();
		}
		return false;
	}
}
