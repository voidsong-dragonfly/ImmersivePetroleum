package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.IEEnums.IOSideConfig;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IInteractionObjectIE;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import flaxbeard.immersivepetroleum.common.particle.FluidParticleData;
import flaxbeard.immersivepetroleum.common.util.FluidHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * @author TwistedGate
 */
public class DerrickTileEntity extends PoweredMultiblockTileEntity<DerrickTileEntity, MultiblockRecipe> implements IInteractionObjectIE, IBlockBounds{
	public static final FluidTank DUMMY_TANK = new FluidTank(0);
	
	/** Template-Location of the Fluid Input Port. (2 0 4)<br> */
	public static final BlockPos Fluid_IN = new BlockPos(2, 0, 4);
	
	/** Template-Location of the Fluid Output Port. (4 0 2)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(4, 0, 2);
	
	/** Template-Location of the Energy Input Ports.<br><pre>2 1 0</pre><br> */
	public static final Set<BlockPos> Energy_IN = ImmutableSet.of(new BlockPos(2, 1, 0));
	
	/** Template-Location of the Redstone Input Port. (0 1 1)<br> */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(0, 1, 1));
	
	public NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
	public FluidTank waterTank = new FluidTank(8000, this::acceptsFluid);
	public boolean drilling, spilling;
	public int timer = 0;
	public DerrickTileEntity(){
		super(DerrickMultiblock.INSTANCE, 16000, true, null);
	}
	
	@Override
	public TileEntityType<?> getType(){
		return IPTileTypes.DERRICK.get();
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.readCustomNBT(nbt, descPacket);
		
		this.drilling = nbt.getBoolean("drilling");
		this.spilling = nbt.getBoolean("spilling");
		this.timer = nbt.getInt("timer");
		
		this.waterTank.readFromNBT(nbt.getCompound("tank"));
		
		if(!descPacket){
			readInventory(nbt.getCompound("inventory"));
		}
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.writeCustomNBT(nbt, descPacket);
		
		nbt.putBoolean("drilling", this.drilling);
		nbt.putBoolean("spilling", this.spilling);
		nbt.putInt("timer", this.timer);
		
		nbt.put("tank", this.waterTank.writeToNBT(new CompoundNBT()));
		
		if(!descPacket){
			nbt.put("inventory", writeInventory(this.inventory));
		}
	}
	
	protected void readInventory(CompoundNBT nbt){
		NonNullList<ItemStack> list = NonNullList.create();
		ItemStackHelper.loadAllItems(nbt, list);
		
		for(int i = 0;i < this.inventory.size();i++){
			ItemStack stack = ItemStack.EMPTY;
			if(i < list.size()){
				stack = list.get(i);
			}
			
			this.inventory.set(i, stack);
		}
	}
	
	protected CompoundNBT writeInventory(NonNullList<ItemStack> list){
		return ItemStackHelper.saveAllItems(new CompoundNBT(), list);
	}
	
	private boolean acceptsFluid(FluidStack fs){
		Fluid fluid = fs.getFluid();
		return fluid == Fluids.WATER || fluid == ExternalModContent.IE_CONCRETE_FLUID.get();
	}
	
	static final int POWER = 512;
	static final int WATER = 125;
	
	@Override
	public void tick(){
		checkForNeedlessTicking();
		if(isDummy()){
			return;
		}
		
		if(this.world.isRemote){
			// Drilling Particles
			if(this.drilling){
				for(int i = 0;i < 10;i++){
					float rx = (this.world.rand.nextFloat() - .5F) * 1.5F;
					float rz = (this.world.rand.nextFloat() - .5F) * 1.5F;
					
					if(!(rx > -0.625 && rx < 0.625) || !(rz > -0.625 && rz < 0.625)){
						float xa = (this.world.rand.nextFloat() - .5F) / 16;
						float ya = 0.01F * this.world.rand.nextFloat();
						float za = (this.world.rand.nextFloat() - .5F) / 16;
						
						double x = (this.pos.getX() + 0.5) + rx;
						double y = (this.pos.getY() + 1.625) + this.world.rand.nextFloat();
						double z = (this.pos.getZ() + 0.5) + rz;
						
						this.world.addParticle(this.world.rand.nextFloat() < 0.5F ? ParticleTypes.SMOKE : ParticleTypes.LARGE_SMOKE, x, y, z, xa, ya, za);
					}
				}
			}
			
			if(this.spilling){
				spawnOilSpillParticles(this.world, this.pos, 5, 15.75F);
			}
			
			return;
		}
		
		if(this.world.isAreaLoaded(this.getPos(), 5)){
			boolean lastDrilling = this.drilling;
			boolean lastSpilling = this.spilling;
			this.drilling = this.spilling = false;
			
			if(!isRSDisabled()){
				WellTileEntity well = null;
				
				TileEntity te = this.getWorldNonnull().getTileEntity(this.pos.down());
				if(te instanceof WellTileEntity){
					well = (WellTileEntity) te;
				}else if(well == null){
					if(this.inventory.get(0) != ItemStack.EMPTY){
						well = getOrCreateWell();
						
						if(well.tappedIslands.isEmpty()){
							well.tappedIslands.add(new ColumnPos(this.pos.getX(), this.pos.getZ()));
							well.markDirty();
						}
					}
				}
				
				if(well != null){
					if(well.pipeLength < well.pipeMaxLength()){
						if(this.energyStorage.getEnergyStored() >= POWER && this.waterTank.getFluidAmount() >= WATER){
							if(well.pipe <= 0){
								if(this.inventory.get(0) != ItemStack.EMPTY){
									ItemStack stack = this.inventory.get(0);
									if(stack.getCount() > 0){
										stack.shrink(1);
										well.pipe = WellTileEntity.PIPE_WORTH;
										
										if(stack.getCount() <= 0){
											this.inventory.set(0, ItemStack.EMPTY);
										}
										
										well.markDirty();
									}
								}
								
							}else if(well.pipe > 0){
								int amtEnergy = this.energyStorage.extractEnergy(POWER, false);
								int amtWater = this.waterTank.drain(WATER, FluidAction.SIMULATE).getAmount();
								
								if(amtEnergy >= POWER && amtWater >= WATER){
									this.energyStorage.extractEnergy(POWER, true);
									this.waterTank.drain(WATER, FluidAction.EXECUTE);
									
									if(this.timer-- <= 0){
										this.timer = 10;
										
										well.pipe -= 1;
										well.pipeLength += 1;
									}
									
									this.drilling = true;
									well.markDirty();
								}
							}
						}
					}else{
						Fluid extractedFluid = Fluids.EMPTY;
						int extractedAmount = 0;
						for(ColumnPos cPos:well.tappedIslands){
							ReservoirIsland island = ReservoirHandler.getIsland(this.world, cPos);
							if(island != null){
								if(extractedFluid == Fluids.EMPTY){
									extractedFluid = island.getType().getFluid();
								}else if(island.getType().getFluid() != extractedFluid){
									continue;
								}
								
								extractedAmount += island.extractWithPressure(getWorldNonnull(), cPos.x, cPos.z);
							}
						}
						
						if(extractedFluid != Fluids.EMPTY && extractedAmount > 0){
							Direction facing = getIsMirrored() ? getFacing().rotateYCCW() : getFacing().rotateY();
							BlockPos outputPos = getBlockPosForPos(Fluid_OUT).offset(facing, 2);
							IFluidHandler output = FluidUtil.getFluidHandler(this.world, outputPos, facing.getOpposite()).orElse(null);
							if(output != null){
								FluidStack fluid = FluidHelper.makePressurizedFluid(extractedFluid, extractedAmount);
								
								int accepted = output.fill(fluid, FluidAction.SIMULATE);
								if(accepted > 0){
									int drained = output.fill(FluidHelper.copyFluid(fluid, Math.min(fluid.getAmount(), accepted), true), FluidAction.EXECUTE);
									if(fluid.getAmount() - drained > 0){
										this.spilling = true;
									}
								}else{
									this.spilling = true;
								}
							}else{
								this.spilling = true;
							}
						}
					}
				}
			}
			
			if((lastDrilling != this.drilling) || (lastSpilling != this.spilling)){
				updateMasterBlock(null, true);
			}
		}
	}
	
	public WellTileEntity getOrCreateWell(){
		World world = this.getWorldNonnull();
		BlockPos pos = getPos().down();
		
		WellTileEntity ret = null;
		
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() != IPContent.Blocks.well){
			boolean isClear = false;
			if(state.getBlock().isAir(state, world, pos)){
				isClear = true;
			}else{
				if(state.getBlockHardness(world, pos) >= 0.0F){
					isClear = world.destroyBlock(pos, true);
				}
			}
			
			if(isClear && world.setBlockState(pos, IPContent.Blocks.well.getDefaultState())){
				ret = (WellTileEntity) world.getTileEntity(pos);
			}
		}else{
			ret = (WellTileEntity) world.getTileEntity(pos);
		}
		
		return ret;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void spawnOilSpillParticles(World world, BlockPos pos, int particles, float heightOffset){
		Fluid fluid = IPContent.Fluids.crudeOil; // Fallback
		ReservoirIsland island = ReservoirHandler.getIsland(world, pos);
		if(island != null){
			fluid = island.getType().getFluid();
		}
		
		for(int i = 0;i < particles;i++){
			float xa = (world.rand.nextFloat() - .5F) / 2;
			float za = (world.rand.nextFloat() - .5F) / 2;
			
			float rx = (world.rand.nextFloat() - .5F) * 0.5F;
			float ya = 0.75F + (world.rand.nextFloat() * 0.25F);
			float rz = (world.rand.nextFloat() - .5F) * 0.5F;
			
			double x = (pos.getX() + 0.5) + rx;
			double y = (pos.getY() + heightOffset);
			double z = (pos.getZ() + 0.5) + rz;
			
			world.addParticle(new FluidParticleData(fluid), x, y, z, xa, ya, za);
		}
	}
	
	@Override
	public IInteractionObjectIE getGuiMaster(){
		return master();
	}
	
	@Override
	public boolean canUseGui(PlayerEntity player){
		return this.formed;
	}
	
	@Override
	public NonNullList<ItemStack> getInventory(){
		return this.inventory;
	}
	
	@Override
	public boolean isStackValid(int slot, ItemStack stack){
		return true;
	}
	
	@Override
	public int getSlotLimit(int slot){
		return 64;
	}
	
	@Override
	public void doGraphicalUpdates(){
		updateMasterBlock(null, true);
	}
	
	@Override
	protected MultiblockRecipe getRecipeForId(ResourceLocation id){
		return null;
	}
	
	@Override
	public Set<BlockPos> getEnergyPos(){
		return Energy_IN;
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return Redstone_IN;
	}
	
	@Override
	public IOSideConfig getEnergySideConfig(Direction facing){
		if(this.formed && this.isEnergyPos() && (facing == null || facing == Direction.UP))
			return IOSideConfig.INPUT;
		
		return IOSideConfig.NONE;
	}
	
	@Override
	public IFluidTank[] getInternalTanks(){
		return new IFluidTank[]{this.waterTank};
	}
	
	@Override
	public MultiblockRecipe findRecipeForInsertion(ItemStack inserting){
		return null;
	}
	
	@Override
	public int[] getOutputSlots(){
		return new int[0];
	}
	
	@Override
	public int[] getOutputTanks(){
		return new int[0];
	}
	
	@Override
	public boolean additionalCanProcessCheck(MultiblockProcess<MultiblockRecipe> process){
		return false;
	}
	
	@Override
	public void doProcessOutput(ItemStack output){
	}
	
	@Override
	public void doProcessFluidOutput(FluidStack output){
	}
	
	@Override
	public void onProcessFinish(MultiblockProcess<MultiblockRecipe> process){
	}
	
	@Override
	public int getMaxProcessPerTick(){
		return 1;
	}
	
	@Override
	public int getProcessQueueMaxLength(){
		return 1;
	}
	
	@Override
	public float getMinProcessDistance(MultiblockProcess<MultiblockRecipe> process){
		return 0;
	}
	
	@Override
	public boolean isInWorldProcessingMachine(){
		return false;
	}
	
	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side){
		DerrickTileEntity master = master();
		if(master != null){
			if(this.posInMultiblock.equals(Fluid_IN)){
				if(side == null || side == getFacing().getOpposite()){
					return new IFluidTank[]{master.waterTank};
				}
			}
			
			if(this.posInMultiblock.equals(Fluid_OUT)){
				if(side == null || (getIsMirrored() ? side == getFacing().rotateYCCW() : side == getFacing().rotateY())){
					return new IFluidTank[]{DUMMY_TANK};
				}
			}
		}
		return new IFluidTank[0];
	}
	
	@Override
	protected boolean canDrainTankFrom(int iTank, Direction side){
		return false;
	}
	
	@Override
	protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource){
		if(this.posInMultiblock.equals(Fluid_IN)){
			if(side == null || side == getFacing().getOpposite()){
				DerrickTileEntity master = master();
				
				if(master == null || master.waterTank.getFluidAmount() >= master.waterTank.getCapacity()){
					return false;
				}
				
				return true;
			}
		}
		return false;
	}
	
	public boolean isLadder(){
		int x = posInMultiblock.getX();
		int y = posInMultiblock.getY();
		int z = posInMultiblock.getZ();
		
		// Primary Ladder
		if((x == 0 && z == 2) && (y >= 0 && y <= 8)){
			return true;
		}
		
		// Secondary Ladder
		if((x == 1 && z == 2) && (y >= 9 && y <= 14)){
			return true;
		}
		
		return false;
	}
	
	private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(DerrickTileEntity::getShape);
	public static boolean updateShapes = false;
	@Override
	public VoxelShape getBlockBounds(ISelectionContext ctx){
		if(updateShapes){
			updateShapes = false;
			SHAPES = CachedShapesWithTransform.createForMultiblock(DerrickTileEntity::getShape);
		}
		return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
		int x = posInMultiblock.getX();
		int y = posInMultiblock.getY();
		int z = posInMultiblock.getZ();
		
		List<AxisAlignedBB> main = new ArrayList<>();
		
		// Base
		if(y == 0){
			if(!(x == 2 && z == 4 || x == 4 && z == 2 || x == 2 && z == 0 || x == 2 && z == 2)){
				main.add(box(0, 0, 0, 16, 8, 16));
			}
		}
		
		// Platform 1 & 2
		if((y == 8 || y == 14) && !(x == 2 && z == 2 || x == 0 && z == 2 || x == 1 && z == 2)){
			main.add(box(0, 8, 0, 16, 16, 16));
		}
		
		if(y == 0 || y == 1){
			// Power Box
			if(z == 0){
				if(x == 1){
					main.add(box(4, 0, 0, 16, 16, 16));
				}else if(x == 3){
					main.add(box(0, 0, 0, 12, 16, 16));
				}
			}
			
			// Fluid Input Box
			if(x == 1 && z == 4){
				main.add(box(8, 8, 0, 16, 24, 16));
			}else if(x == 3 && z == 4){
				main.add(box(0, 8, 0, 8, 24, 16));
			}
			if(y == 1 && x == 2 && z == 4){
				main.add(box(-8, 0, 0, 24, 8, 16));
			}
		}
		
		// Center Pipe and Stuff
		if(x == 2 && z == 2 && y >= 0 && y <= 13){
			if(y == 0){
				main.add(box(-4, 8, -4, 20, 16, 20));
				main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
			}else if(y > 1){
				// Pipe
				main.add(box(4, 0, 4, 12, 16, 12));
			}
			
			if(y == 1){
				main.add(box(-4, 0, -4, 20, 8, 20));
				main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
			}
			if(y == 2){
				main.add(box(0, 0, 0, 16, 8, 16));
			}
		}
		
		// Primary Ladder
		if(x == 0 && z == 2 && (y >= 1 && y <= 8)){
			if(y == 1){
				main.add(box(15, 8, 2, 16, 16, 14));
			}else{
				main.add(box(15, 0, 2, 16, 16, 14));
			}
			
			if(y > 2){
				main.add(box(0, 0, 0, 1, 16, 16));
				main.add(box(0, 0, 0, 16, 16, 1));
				main.add(box(0, 0, 15, 16, 16, 16));
			}
		}
		// Secondary Ladder
		if((x == 1 && z == 2) && (y >= 9 && y <= 14)){
			if(y == 9){
				main.add(box(15, 8, 2, 16, 16, 14));
			}else{
				main.add(box(15, 0, 2, 16, 16, 14));
			}
			
			if(y > 10){
				main.add(box(0, 0, 0, 1, 16, 16));
				main.add(box(0, 0, 0, 16, 16, 1));
				main.add(box(0, 0, 15, 16, 16, 16));
			}
		}
		
		// Supports: from Base to Platform 1
		if(y >= 0 && y <= 8){
			// Corners
			if(x == 0 && z == 0){
				main.add(box(9 + y, 0, 9 + y, 15 + y, 16, 15 + y));
			}else if(x == 4 && z == 0){
				main.add(box(1 - y, 0, 9 + y, 7 - y, 16, 15 + y));
			}else if(x == 0 && z == 4){
				main.add(box(9 + y, 0, 1 - y, 15 + y, 16, 7 - y));
			}else if(x == 4 && z == 4){
				main.add(box(1 - y, 0, 1 - y, 7 - y, 16, 7 - y));
			}
			
			// Centers
			if(y >= 3){
				if(x == 2 && z == 0){
					main.add(box(6, 0, 9 + y, 10, 16, 14 + y));
				}else if(x == 2 && z == 4){
					main.add(box(6, 0, 2 - y, 10, 16, 7 - y));
				}else if(x == 4 && z == 2){
					main.add(box(2 - y, 0, 6, 7 - y, 16, 10));
				}
			}
			
			// First horizontal bars, above base
			if(y == 3){
				if(x >= 1 && x <= 3){
					if(z == 0){
						main.add(box(0, -4, 12, 16, 0, 16));
					}else if(z == 4){
						main.add(box(0, -4, 0, 16, 0, 4));
					}
				}
				if(z >= 1 && z <= 3){
					if(x == 0 && z != 2){
						main.add(box(12, -4, 0, 16, 0, 16));
					}else if(x == 4){
						main.add(box(0, -4, 0, 4, 0, 16));
					}
				}
			}
			
			// Second horizontal bars, below Platform 1
			if(y == 6){
				if(x >= 1 && x <= 3){
					if(z == 0){
						main.add(box(0, 4, 16, 16, 8, 20));
					}else if(z == 4){
						main.add(box(0, 4, -4, 16, 8, 0));
					}
				}
				if(z >= 1 && z <= 3){
					if(x == 0){
						main.add(box(16, 4, 0, 20, 8, 16));
					}else if(x == 4){
						main.add(box(-4, 4, 0, 0, 8, 16));
					}
				}
			}
		}
		
		// Supports: From Platform 1 to Platform 2, up to the very top
		if(y >= 9 && y <= 16){
			double off = y - 9;
			
			// Corners
			if(x == 1 && z == 1){
				main.add(box(2 + off, 0, 2 + off, 7 + off, 16, 7 + off));
			}else if(x == 3 && z == 1){
				main.add(box(9 - off, 0, 2 + off, 14 - off, 16, 7 + off));
			}else if(x == 1 && z == 3){
				main.add(box(2 + off, 0, 9 - off, 7 + off, 16, 14 - off));
			}else if(x == 3 && z == 3){
				main.add(box(9 - off, 0, 9 - off, 14 - off, 16, 14 - off));
			}
			
			// Centers
			if(x == 2 && z == 1){
				main.add(box(6, 0, 2 + off, 10, 16, 7 + off));
			}else if(x == 2 && z == 3){
				main.add(box(6, 0, 9 + off, 10, 16, 14 + off));
			}else if(x == 3 && z == 2){
				main.add(box(10 - off, 0, 6, 14, 16 - off, 10));
			}
			
			// Third horizontal bars, above platform 1
			if(y == 9){
				if(x == 3){
					if(z == 1){
						main.add(box(9, 12, 7, 13, 16, 16));
					}else if(z == 2){
						main.add(box(9, 12, 0, 13, 16, 16));
					}else if(z == 3){
						main.add(box(9, 12, 0, 13, 16, 9));
					}
				}
				
				if(z == 1){
					if(x == 1){
						main.add(box(7, 12, 3, 16, 16, 7));
					}else if(x == 2){
						main.add(box(0, 12, 3, 16, 16, 7));
					}else if(x == 3){
						main.add(box(0, 12, 3, 9, 16, 7));
					}
				}else if(z == 3){
					if(x == 1){
						main.add(box(7, 12, 9, 16, 16, 13));
					}else if(x == 2){
						main.add(box(0, 12, 9, 16, 16, 13));
					}else if(x == 3){
						main.add(box(0, 12, 9, 9, 16, 13));
					}
				}
			}
			
			// Fourth horizontal bars, below platform 2
			if(y == 13){
				if(x == 3){
					if(z == 1){
						main.add(box(5, 4, 11, 9, 8, 16));
					}else if(z == 2){
						main.add(box(5, 4, 0, 9, 8, 16));
					}else if(z == 3){
						main.add(box(5, 4, 0, 9, 8, 5));
					}
				}
				
				if(z == 1){
					if(x == 1){
						main.add(box(11, 4, 7, 16, 8, 11));
						main.add(box(7, 4, 11, 11, 8, 16));
					}else if(x == 2){
						main.add(box(0, 4, 7, 16, 8, 11));
					}else if(x == 3){
						main.add(box(0, 4, 7, 5, 8, 11));
					}
				}else if(z == 3){
					if(x == 1){
						main.add(box(11, 4, 5, 16, 8, 9));
						main.add(box(7, 4, 0, 11, 8, 5));
					}else if(x == 2){
						main.add(box(0, 4, 5, 16, 8, 9));
					}else if(x == 3){
						main.add(box(0, 4, 5, 5, 8, 9));
					}
				}
			}
			
			// Horizontal bars at the very top
			if(y == 16){
				if(z == 1){
					if(x == 1){
						main.add(box(9, 12, 9, 16, 16, 16));
					}else if(x == 2){
						main.add(box(0, 12, 9, 16, 16, 15));
					}else if(x == 3){
						main.add(box(0, 12, 9, 7, 16, 16));
					}
				}
				
				if(z == 2){
					if(x == 1){
						main.add(box(9, 12, 0, 15, 16, 16));
					}else if(x == 3){
						main.add(box(1, 12, 0, 7, 16, 16));
					}
				}
				
				if(z == 3){
					if(x == 1){
						main.add(box(9, 12, 0, 16, 16, 7));
					}else if(x == 2){
						main.add(box(0, 12, 1, 16, 16, 7));
					}else if(x == 3){
						main.add(box(0, 12, 0, 7, 16, 7));
					}
				}
			}
		}
		
		// Use default cube shape if nessesary
		if(main.isEmpty()){
			main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
		}
		return main;
	}
	
	/** Makes a box using texture pixel space (Assuming 16x16 p texture) */
	private static AxisAlignedBB box(double x0, double y0, double z0, double x1, double y1, double z1){
		return new AxisAlignedBB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D);
	}
}
