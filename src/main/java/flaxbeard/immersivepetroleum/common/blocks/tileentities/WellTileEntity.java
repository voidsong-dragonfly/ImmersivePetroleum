package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import blusunrize.immersiveengineering.api.IEProperties;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.stone.WellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class WellTileEntity extends IPTileEntityBase implements ITickableTileEntity{
	boolean spill = false;
	public WellTileEntity(){
		super(IPTileTypes.WELL.get());
	}
	
	@Override
	protected void writeCustom(CompoundNBT compound){
		compound.putBoolean("spill", this.spill);
	}
	
	@Override
	protected void readCustom(BlockState state, CompoundNBT compound){
		this.spill = compound.getBoolean("spill");
	}
	
	@Override
	public void tick(){
		if(this.world.isRemote){
			if(this.spill){
				DerrickTileEntity.spawnOilSpillParticles(this.world, this.pos, 10, 1.5F);
			}
		}else{
			if(this.world.getGameTime() % 10 == 0){
				BlockPos above = this.pos.offset(Direction.UP);
				BlockState aState = this.world.getBlockState(above);
				
				boolean last = this.spill;
				if(getBlockState().get(WellBlock.CAPPED) || (aState.getBlock() == IPContent.Multiblock.derrick && !aState.get(IEProperties.MULTIBLOCKSLAVE))){
					this.spill = false;
				}else{
					this.spill = true;
				}
				
				if(this.spill != last){
					markDirty();
					
					BlockState state = world.getBlockState(pos);
					world.notifyBlockUpdate(pos, state, state, 3);
					world.notifyNeighborsOfStateChange(pos, state.getBlock());
				}
			}
			
			if(this.spill){
				// TODO (Oil * pressure) per Tick = spill! (Void)
//				if(pressure > threshold){
//					void(Amount * Pressure);
//				}
			}
		}
	}
}
