package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class WellPipeTileEntity extends IPTileEntityBase{
	public WellPipeTileEntity(){
		super(IPTileTypes.WELL_PIPE.get());
	}
	
	@Override
	protected void writeCustom(CompoundNBT compound){
	}
	
	@Override
	protected void readCustom(BlockState state, CompoundNBT compound){
	}
}
