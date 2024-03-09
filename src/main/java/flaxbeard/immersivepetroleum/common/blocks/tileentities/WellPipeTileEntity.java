package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import org.apache.commons.lang3.tuple.Pair;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WellPipeTileEntity extends IPTileEntityBase{
	public WellPipeTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(IPTileTypes.WELL_PIPE.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	protected void writeCustom(CompoundTag nbt){
	}
	
	@Override
	protected void readCustom(CompoundTag nbt){
	}
	
	/** Returns null if there is no connection to the Well. Used by Pumpjack. */
	public WellTileEntity getWell(){
		for(int y = this.worldPosition.getY() - 1;y >= this.level.getMinBuildHeight();y--){
			BlockEntity teLow = this.level.getBlockEntity(new BlockPos(this.worldPosition.getX(), y, this.worldPosition.getZ()));
			
			if(teLow instanceof WellTileEntity well){
				return well;
			}
			
			if(!(teLow instanceof WellPipeTileEntity)){
				break;
			}
		}
		
		return null;
	}
	
	/** Returns the location of the missing pipe. Used for spill effect. */
	public BlockPos checkForMissingPipe(){
		for(int y = this.worldPosition.getY() + 1;y < this.level.getMaxBuildHeight();y++){
			BlockPos pos = new BlockPos(this.worldPosition.getX(), y, this.worldPosition.getZ());
			BlockEntity teHigh = this.level.getBlockEntity(pos);
			
			if(!(teHigh instanceof WellPipeTileEntity)){
				return pos;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if a Derrick or Pumpjack are "connected" to the Well and Where.
	 */
	public Pair<Boolean, BlockPos> hasValidConnection(){
		BlockPos pos = null;
		for(int y = this.worldPosition.getY() + 1;y < this.level.getMaxBuildHeight();y++){
			pos = new BlockPos(this.worldPosition.getX(), y, this.worldPosition.getZ());
			BlockEntity teHigh = this.level.getBlockEntity(pos);
			
			if(teHigh instanceof MultiblockBlockEntityMaster<?> master){
				Object state = master.getHelper().getState();
				if(state instanceof PumpjackLogic.State || state instanceof DerrickLogic.State){
					return Pair.of(true, pos);
				}
			}
			
			if(!(teHigh instanceof WellPipeTileEntity)){
				break;
			}
		}
		
		return Pair.of(false, pos);
	}
}
