package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;

public class WellTileEntity extends IPTileEntityBase implements ITickableTileEntity{
	
	static final int PIPE_WORTH = 6;
	static final int DEFAULT_PIPELENGTH = PIPE_WORTH * 64;
	
	/** It's supposed to be never null nor empty. If it is then something's wrong. */
	@Nonnull
	public List<ColumnPos> tappedIslands = new ArrayList<>();
	
	/** Amount of pipe left over from 1 IE Pipe */
	public int pipe = 0;
	/** Current length of the virtual well pipe */
	public int pipeLength = 0;
	/** Extra pipes needed to make the bend and connect to other "inputs" */
	public int additionalPipes = 0;
	public boolean drillingCompleted;
	
	private Fluid spillFType = Fluids.EMPTY;
	@Nullable
	private int spillHeight = -1;
	
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
		
		nbt.putString("spillftype", this.spillFType.getRegistryName().toString());
		nbt.putInt("spillheight", this.spillHeight);
		
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
		
		try{
			this.spillFType = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("spillftype")));
		}catch(ResourceLocationException rle){
			this.spillFType = Fluids.EMPTY;
		}
		this.spillHeight = nbt.getInt("spillheight");
		
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
			if(this.spill && this.spillFType != Fluids.EMPTY){
				BlockPos pPos = this.spillHeight > -1 ? new BlockPos(this.pos.getX(), this.spillHeight, this.pos.getZ()) : this.pos.up();
				DerrickTileEntity.spawnSpillParticles(this.world, pPos, this.spillFType, 10, -0.25F);
			}
		}else{
			if(this.drillingCompleted){
				if(this.tappedIslands.size() > 0){
					if(this.world.getGameTime() % 5 == 0){
						boolean spill = false;
						
						int height = -1;
						Fluid fType = Fluids.EMPTY;
						
						TileEntity teHigh = getWorldNonnull().getTileEntity(getPos().up());
						if(teHigh instanceof WellPipeTileEntity){
							Pair<Boolean, BlockPos> result = ((WellPipeTileEntity) teHigh).hasValidConnection();
							
							// Don't stop spilling even if the pumpjack is ontop, because it is "not designed" to handle the high pressure
							if(!result.getLeft() || getWorldNonnull().getTileEntity(result.getRight()) instanceof PumpjackTileEntity){
								for(ColumnPos cPos:this.tappedIslands){
									ReservoirIsland island = ReservoirHandler.getIsland(getWorldNonnull(), cPos);
									
									// One is enough to trigger spilling
									if(island != null && island.getPressure(getWorldNonnull(), cPos.x, cPos.z) > 0.0){
										fType = island.getType().getFluid();
										height = result.getRight().getY();
										spill = true;
										break;
									}
								}
							}
							
						}else{
							ColumnPos cPos = this.tappedIslands.get(0);
							ReservoirIsland island = ReservoirHandler.getIsland(getWorldNonnull(), cPos);
							
							if(island != null && island.getPressure(getWorldNonnull(), cPos.x, cPos.z) > 0.0){
								spill = true;
								fType = island.getType().getFluid();
								height = this.pos.getY() + 1;
							}
						}
						
						if(spill != this.spill){
							this.spill = spill;
							
							if(this.spill){
								this.spillHeight = height;
								this.spillFType = fType;
							}else{
								this.spillHeight = -1;
								this.spillFType = Fluids.EMPTY;
							}
							
							markDirty();
						}
					}
					
					if(this.spill){
						for(ColumnPos cPos:this.tappedIslands){
							ReservoirIsland island = ReservoirHandler.getIsland(getWorldNonnull(), cPos);
							
							if(island != null){
								// Already unpressurized islands are left alone by default
								island.extractWithPressure(getWorldNonnull(), cPos.x, cPos.z);
							}
						}
					}
				}
			}
		}
	}
	
	public void usePipe(){
		if(this.drillingCompleted){
			return;
		}
		
		this.pipe -= 1;
		this.pipeLength += 1;
		
		if(this.pipeLength >= pipeMaxLength()){
			this.drillingCompleted = true;
		}
		
		markDirty();
	}
	
	@Override
	public void markDirty(){
		super.markDirty();
		
		BlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.notifyNeighborsOfStateChange(pos, state.getBlock());
	}
}
