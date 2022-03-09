package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import org.apache.commons.lang3.tuple.Pair;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class WellPipeTileEntity extends IPTileEntityBase{
	public WellPipeTileEntity(){
		super(IPTileTypes.WELL_PIPE.get());
	}
	
	@Override
	protected void writeCustom(CompoundNBT nbt){
	}
	
	@Override
	protected void readCustom(BlockState state, CompoundNBT nbt){
	}
	
	/** Returns null if there is no connection to the Well. Used by Pumpjack. */
	public WellTileEntity getWell(){
		// TODO !Replace "y >= 0" in 1.18 with something that can go negative
		for(int y = this.pos.getY() - 1;y >= 0;y--){
			TileEntity teLow = this.world.getTileEntity(new BlockPos(this.pos.getX(), y, this.pos.getZ()));
			
			if(teLow instanceof WellTileEntity){
				return (WellTileEntity) teLow;
			}
			
			if(!(teLow instanceof WellPipeTileEntity)){
				break;
			}
		}
		
		return null;
	}
	
	/** Returns the location of the missing pipe. Used for spill effect. */
	public BlockPos checkForMissingPipe(){
		for(int y = this.pos.getY() + 1;y < this.world.getHeight();y++){
			BlockPos pos = new BlockPos(this.pos.getX(), y, this.pos.getZ());
			TileEntity teHigh = this.world.getTileEntity(pos);
			
			if(!(teHigh instanceof WellPipeTileEntity)){
				return pos;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if a Derrick or Pumpjack are "connected" to the Well and
	 * Where.
	 */
	public Pair<Boolean, BlockPos> hasValidConnection(){
		BlockPos pos = null;
		for(int y = this.pos.getY() + 1;y < this.world.getHeight();y++){
			pos = new BlockPos(this.pos.getX(), y, this.pos.getZ());
			TileEntity teHigh = this.world.getTileEntity(pos);
			
			if((teHigh instanceof PumpjackTileEntity && ((PumpjackTileEntity) teHigh).offsetToMaster.equals(BlockPos.ZERO)) || (teHigh instanceof DerrickTileEntity && ((DerrickTileEntity) teHigh).offsetToMaster.equals(BlockPos.ZERO))){
				return Pair.of(true, pos);
			}
			
			if(!(teHigh instanceof WellPipeTileEntity)){
				break;
			}
		}
		
		return Pair.of(false, pos);
	}
}
