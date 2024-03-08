package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPCommonTickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@Deprecated(forRemoval = true)
public class PumpjackTileEntity /*extends PoweredMultiblockBlockEntity<PumpjackTileEntity, MultiblockRecipe>*/ implements IPCommonTickableTile, IEBlockInterfaces.IBlockBounds{
	/** Template-Location of the Energy Input Port. (0, 1, 5) */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(0, 1, 5));
	
	/** Template-Location of the Redstone Input Port. (2, 1, 5) */
	public static final Set<MultiblockFace> Energy_IN = ImmutableSet.of(new MultiblockFace(2, 1, 5, RelativeBlockFace.UP));
	
	/** Template-Location of the Eastern Fluid Output Port. (2, 0, 2) */
	public static final BlockPos East_Port = new BlockPos(2, 0, 2);
	
	/** Template-Location of the Western Fluid Output Port. (0, 0, 2) */
	public static final BlockPos West_Port = new BlockPos(0, 0, 2);
	
	/**
	 * Template-Location of the Bottom Fluid Output Port. (1, 0, 0) <b>(Also Master Block)</b>
	 */
	public static final BlockPos Down_Port = new BlockPos(1, 0, 0);
	
	public static final FluidTank FAKE_TANK = new FluidTank(0);
	public boolean wasActive = false;
	public float activeTicks = 0;
	public PumpjackTileEntity(BlockEntityType<?> type, BlockPos pWorldPosition, BlockState pBlockState){
		//super(PumpjackMultiblock.INSTANCE, 16000, true, type, pWorldPosition, pBlockState);
	}
	
	public void readCustomNBT(CompoundTag nbt, boolean descPacket){
		//super.readCustomNBT(nbt, descPacket);
		boolean lastActive = this.wasActive;
		this.wasActive = nbt.getBoolean("wasActive");
		if(!this.wasActive && lastActive){
			this.activeTicks++;
		}
	}
	
	public void writeCustomNBT(CompoundTag nbt, boolean descPacket){
		//super.writeCustomNBT(nbt, descPacket);
		nbt.putBoolean("wasActive", this.wasActive);
	}
	
	@Override
	public void tickClient(){
		/*
		if(!isDummy() && this.wasActive){
			this.activeTicks++;
		}
		*/
	}
	
	@Override
	public void tickServer(){
		/*
		if(isDummy()){
			return;
		}
		
		super.tickServer();
		
		boolean active = false;
		if(!isRSDisabled()){
			BlockEntity teLow = this.getLevelNonnull().getBlockEntity(this.worldPosition.below());
			
			if(teLow instanceof WellPipeTileEntity pipe){
				WellTileEntity well = pipe.getWell();
				
				if(well != null){
					int consumption = IPServerConfig.EXTRACTION.pumpjack_consumption.get();
					int extracted = this.energyStorage.extractEnergy(consumption, true);
					
					if(extracted >= consumption){
						// Does any island still have pressure?
						boolean foundPressurizedIsland = false;
						for(ColumnPos cPos:well.tappedIslands){
							ReservoirIsland island = ReservoirHandler.getIsland(this.level, cPos);
							
							if(island != null && island.getPressure(getLevelNonnull(), cPos.x(), cPos.z()) > 0.0F){
								foundPressurizedIsland = true;
								break;
							}
						}
						
						// Skip if there is (Simulates pumpjack not being able to handle high pressures)
						if(!foundPressurizedIsland){
							int extractSpeed = IPServerConfig.EXTRACTION.pumpjack_speed.get();
							
							Direction portEast_facing = getIsMirrored() ? getFacing().getCounterClockWise() : getFacing().getClockWise();
							Direction portWest_facing = getIsMirrored() ? getFacing().getClockWise() : getFacing().getCounterClockWise();
							
							BlockPos portEast_pos = getBlockPosForPos(East_Port).relative(portEast_facing);
							BlockPos portWest_pos = getBlockPosForPos(West_Port).relative(portWest_facing);
							
							IFluidHandler portEast_output = FluidUtil.getFluidHandler(this.level, portEast_pos, portEast_facing.getOpposite()).orElse(null);
							IFluidHandler portWest_output = FluidUtil.getFluidHandler(this.level, portWest_pos, portWest_facing.getOpposite()).orElse(null);
							
							for(ColumnPos cPos:well.tappedIslands){
								ReservoirIsland island = ReservoirHandler.getIsland(this.level, cPos);
								if(island != null){
									FluidStack fluid = new FluidStack(island.getFluid(), island.extract(extractSpeed, FluidAction.SIMULATE));
									
									if(portEast_output != null){
										int accepted = portEast_output.fill(fluid, FluidAction.SIMULATE);
										if(accepted > 0){
											int drained = portEast_output.fill(FluidHelper.copyFluid(fluid, Math.min(fluid.getAmount(), accepted)), FluidAction.EXECUTE);
											island.extract(drained, FluidAction.EXECUTE);
											fluid = FluidHelper.copyFluid(fluid, fluid.getAmount() - drained);
											active = true;
										}
									}
									
									if(portWest_output != null && fluid.getAmount() > 0){
										int accepted = portWest_output.fill(fluid, FluidAction.SIMULATE);
										if(accepted > 0){
											int drained = portWest_output.fill(FluidHelper.copyFluid(fluid, Math.min(fluid.getAmount(), accepted)), FluidAction.EXECUTE);
											island.extract(drained, FluidAction.EXECUTE);
											active = true;
										}
									}
								}
							}
							
							if(active){
								this.energyStorage.extractEnergy(consumption, false);
								this.activeTicks++;
							}
						}
					}
				}
			}
		}
		
		if(active != this.wasActive){
			this.setChanged();
			this.markContainingBlockForUpdate(null);
		}
		
		this.wasActive = active;
		*/
	}
	
	public Set<MultiblockFace> getEnergyPos(){
		return Energy_IN;
	}
	
	public Set<BlockPos> getRedstonePos(){
		return Redstone_IN;
	}
	
	public boolean isInWorldProcessingMachine(){
		return false;
	}
	
	public void doProcessOutput(ItemStack output){
	}
	
	public void doProcessFluidOutput(FluidStack output){
	}
	
	public void onProcessFinish(Object process){
	}
	
	public boolean additionalCanProcessCheck(Object process){
		return false;
	}
	
	public float getMinProcessDistance(Object process){
		return 0;
	}
	
	public int getMaxProcessPerTick(){
		return 1;
	}
	
	public int getProcessQueueMaxLength(){
		return 1;
	}
	
	public boolean isStackValid(int slot, ItemStack stack){
		return true;
	}
	
	public int getSlotLimit(int slot){
		return 64;
	}
	
	public int[] getOutputSlots(){
		return null;
	}
	
	public int[] getOutputTanks(){
		return new int[]{1};
	}
	
	public void doGraphicalUpdates(){
//		this.setChanged();
//		this.markContainingBlockForUpdate(null);
	}
	
	public MultiblockRecipe findRecipeForInsertion(ItemStack inserting){
		return null;
	}
	
	protected MultiblockRecipe getRecipeForId(Level level, ResourceLocation id){
		return null;
	}
	
	public NonNullList<ItemStack> getInventory(){
		return null;
	}
	
	public IFluidTank[] getInternalTanks(){
		return null;
	}
	
	public <C> Object getCapability(@Nonnull Object capability, @Nullable Direction side){
		return null;
	}
	
	@Override
	public VoxelShape getBlockBounds(CollisionContext ctx){
		return null;
	}
}
