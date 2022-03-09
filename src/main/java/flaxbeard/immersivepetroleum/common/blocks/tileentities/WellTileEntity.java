package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.stone.WellPipeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
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
	
	/** Only ever contains the Y component of {@link BlockPos} */
	public List<Integer> phyiscalPipesList = new ArrayList<>();
	
	/** Amount of pipe left over from 1 IE Pipe */
	public int pipes = 0;
	/** Current length of the virtual well pipe */
	public int wellPipeLength = 0;
	/** Extra pipes needed to make the bend and connect to other "inputs" */
	public int additionalPipes = 0;
	public boolean drillingCompleted;
	
	public boolean pastPhyiscalPart;
	
	private boolean selfDestruct;
	private int selfDestructTimer;
	
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
		nbt.putBoolean("drillingcompleted", this.drillingCompleted);
		nbt.putBoolean("pastphyiscalpart", this.pastPhyiscalPart);
		
		nbt.putInt("pipes", this.pipes);
		nbt.putInt("wellpipelength", this.wellPipeLength);
		nbt.putInt("additionalpipes", this.additionalPipes);
		
		nbt.putBoolean("selfdestruct", this.selfDestruct);
		nbt.putInt("selfdestructtimer", this.selfDestructTimer);
		
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
		
		if(!this.phyiscalPipesList.isEmpty()){
			final ListNBT list = new ListNBT();
			this.phyiscalPipesList.forEach(i -> {
				list.add(IntNBT.valueOf(i.intValue()));
			});
			nbt.put("pipeLoc", list);
		}
	}
	
	@Override
	protected void readCustom(BlockState state, CompoundNBT nbt){
		this.spill = nbt.getBoolean("spill");
		this.drillingCompleted = nbt.getBoolean("drillingcompleted");
		this.pastPhyiscalPart = nbt.getBoolean("pastphyiscalpart");
		
		this.pipes = nbt.getInt("pipes");
		this.wellPipeLength = nbt.getInt("wellpipelength");
		this.additionalPipes = nbt.getInt("additionalpipes");
		
		this.selfDestruct = nbt.getBoolean("selfdestruct");
		this.selfDestructTimer = nbt.getInt("selfdestructtimer");
		
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
		
		if(nbt.contains("pipeLoc", NBT.TAG_LIST)){
			ListNBT list = nbt.getList("pipeLoc", NBT.TAG_INT);
			final List<Integer> ints = new ArrayList<>(list.size());
			list.forEach(n -> {
				ints.add(Integer.valueOf(((IntNBT) n).getInt()));
			});
		}
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
			}else{
				if(this.selfDestruct && advanceTimer()){
					// Sucks to be you if this happens =P
					getWorldNonnull().setBlockState(getPos(), Blocks.BEDROCK.getDefaultState());
					
					if(!this.phyiscalPipesList.isEmpty()){
						for(int i = 0;i < this.phyiscalPipesList.size();i++){
							BlockPos pos = getPos();
							pos = new BlockPos(pos.getX(), this.phyiscalPipesList.get(i).intValue(), pos.getZ());
							
							BlockState state = getWorldNonnull().getBlockState(pos);
							
							if(state.getBlock() instanceof WellPipeBlock){
								getWorldNonnull().setBlockState(pos, state.with(WellPipeBlock.BROKEN, true));
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
		
		this.pipes -= 1;
		this.wellPipeLength += 1;
		
		if(this.wellPipeLength >= getMaxPipeLength()){
			this.drillingCompleted = true;
		}
		
		markDirty();
	}
	
	public int getMaxPipeLength(){
		return DEFAULT_PIPELENGTH + this.additionalPipes;
	}
	
	public void startSelfDestructSequence(){
		if(this.drillingCompleted){
			return;
		}
		
		this.selfDestruct = true;
		this.selfDestructTimer = 100;
//		this.selfDestructTimer = 6000; // 5 Minutes
	}
	
	public void abortSelfDestructSequence(){
		if(this.selfDestruct){
			this.selfDestruct = false;
		}
	}
	
	public boolean advanceTimer(){
		if(this.selfDestruct && this.selfDestructTimer-- <= 0){
			return true;
		}
		return false;
	}
	
	@Override
	public void markDirty(){
		super.markDirty();
		
		BlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.notifyNeighborsOfStateChange(pos, state.getBlock());
	}
}
