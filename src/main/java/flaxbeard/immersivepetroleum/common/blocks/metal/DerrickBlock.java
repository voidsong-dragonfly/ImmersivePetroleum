package flaxbeard.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nonnull;

public class DerrickBlock extends IPMetalMultiblock<DerrickTileEntity>{
	public DerrickBlock(){
		super(IPTileTypes.DERRICK);
	}
	
	@Override
	@Nonnull
	public InteractionResult use(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit){
		if(!player.getItemInHand(hand).isEmpty()){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof DerrickTileEntity derrick && derrick.skipGui(hit)){
				return InteractionResult.FAIL;
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
