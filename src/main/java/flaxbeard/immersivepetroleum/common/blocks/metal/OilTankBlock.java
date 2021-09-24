package flaxbeard.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class OilTankBlock extends IPMetalMultiblock<OilTankTileEntity>{
	public OilTankBlock(){
		super("oiltank", () -> IPTileTypes.OILTANK.get());
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof OilTankTileEntity){
			return ((OilTankTileEntity) te).isLadder();
		}
		return false;
	}
}
