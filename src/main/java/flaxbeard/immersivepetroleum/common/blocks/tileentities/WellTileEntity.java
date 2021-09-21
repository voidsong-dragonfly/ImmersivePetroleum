package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import blusunrize.immersiveengineering.api.IEProperties;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
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
				DerrickTileEntity.spawnOilSpillParticles(this.world, this.pos, 10, 0.75F);
			}
		}else{
			ReservoirIsland island = ReservoirHandler.getIsland(getWorldNonnull(), this.pos);
			if(island != null){
				int x = this.pos.getX();
				int z = this.pos.getZ();
				
				if(this.world.getGameTime() % 10 == 0){
					BlockPos above = this.pos.offset(Direction.UP);
					BlockState aState = this.world.getBlockState(above);
					
					boolean last = this.spill;
					if(island.getPressure(getWorldNonnull(), x, z) > 0.0 && !(aState.getBlock() == IPContent.Multiblock.derrick && !aState.get(IEProperties.MULTIBLOCKSLAVE))){
						this.spill = true;
					}else{
						this.spill = false;
					}
					
					if(this.spill != last){
						markDirty();
						
						BlockState state = this.world.getBlockState(this.pos);
						this.world.notifyBlockUpdate(this.pos, state, state, 3);
						this.world.notifyNeighborsOfStateChange(this.pos, state.getBlock());
					}
				}
				
				if(this.spill && island != null){
					island.spill(getWorld(), x, z);
				}
			}
		}
	}
}
