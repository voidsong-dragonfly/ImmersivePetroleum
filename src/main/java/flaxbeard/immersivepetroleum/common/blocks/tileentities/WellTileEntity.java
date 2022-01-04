package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;

import blusunrize.immersiveengineering.api.IEProperties;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraftforge.common.util.Constants.NBT;

public class WellTileEntity extends IPTileEntityBase implements ITickableTileEntity{
	
	static final int PIPE_WORTH = 6;
	static final int DEFAULT_PIPELENGTH = PIPE_WORTH * 64;
	
	public List<ColumnPos> tappedIslands = new ArrayList<>();
	public int pipe = 0;
	public int pipeLength = 0;
	public int additionalPipes = 0;
	public boolean drillingCompleted;
	
	boolean spill = false;
	public WellTileEntity(){
		super(IPTileTypes.WELL.get());
	}
	
	@Override
	protected void writeCustom(CompoundNBT nbt){
		nbt.putBoolean("spill", this.spill);
		nbt.putBoolean("drillingCompleted", this.drillingCompleted);
		
		nbt.putInt("pipe", this.pipe);
		nbt.putInt("pipelength", this.pipeLength);
		nbt.putInt("additionalpipes", this.additionalPipes);
		
		if(!this.tappedIslands.isEmpty()){
			final ListNBT list = new ListNBT();
			this.tappedIslands.forEach(c -> {
				CompoundNBT pos = new CompoundNBT();
				pos.putInt("x", c.x);
				pos.putInt("z", c.z);
				list.add(pos);
			});
			nbt.put("tappedislands", list);
		}
	}
	
	@Override
	protected void readCustom(BlockState state, CompoundNBT nbt){
		this.spill = nbt.getBoolean("spill");
		this.drillingCompleted = nbt.getBoolean("drillingCompleted");
		
		this.pipe = nbt.getInt("pipe");
		this.pipeLength = nbt.getInt("pipelength");
		this.additionalPipes = nbt.getInt("additionalpipes");
		
		if(nbt.contains("tappedislands", NBT.TAG_LIST)){
			ListNBT list = nbt.getList("tappedislands", NBT.TAG_COMPOUND);
			final List<ColumnPos> tmp = new ArrayList<>(list.size());
			list.forEach(n -> {
				CompoundNBT pos = (CompoundNBT) n;
				int x = pos.getInt("x");
				int z = pos.getInt("z");
				tmp.add(new ColumnPos(x, z));
			});
			this.tappedIslands = tmp;
		}
	}
	
	public int pipeMaxLength(){
		return DEFAULT_PIPELENGTH + this.additionalPipes;
	}
	
	@Override
	public void tick(){
		if(this.world.isRemote){
			if(this.spill){
				DerrickTileEntity.spawnOilSpillParticles(this.world, this.pos, 10, 0.75F);
			}
		}else{
			if(this.drillingCompleted){
				ReservoirIsland island = ReservoirHandler.getIsland(getWorldNonnull(), this.pos);
				if(island != null){
					int x = this.pos.getX();
					int z = this.pos.getZ();
					
					if(this.world.getGameTime() % 10 == 0){
						BlockPos above = this.pos.offset(Direction.UP);
						BlockState aState = this.world.getBlockState(above);
						
						boolean last = this.spill;
						if(island.getPressure(getWorldNonnull(), x, z) > 0.0 && !((aState.getBlock() == IPContent.Multiblock.derrick || aState.getBlock() == IPContent.Multiblock.pumpjack) && !aState.get(IEProperties.MULTIBLOCKSLAVE))){
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
						// TODO Spill using the tappedIslands array
						island.extractWithPressure(getWorld(), x, z);
					}
				}
			}
		}
	}
	
	@Override
	public void markDirty(){
		super.markDirty();
		
		BlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.notifyNeighborsOfStateChange(pos, state.getBlock());
	}
}
