package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import flaxbeard.immersivepetroleum.common.particle.FluidParticleData;
import flaxbeard.immersivepetroleum.common.util.FluidHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author TwistedGate
 */
public class DerrickTileEntity extends PoweredMultiblockTileEntity<DerrickTileEntity, MultiblockRecipe> implements IInteractionObjectIE, IBlockBounds{
	public enum Inventory{
		/** Item Pipe Input */
		INPUT;
		
		public int id(){
			return ordinal();
		}
	}
	
	public static final FluidTank DUMMY_TANK = new FluidTank(0);
	
	/** Template-Location of the Fluid Input Port. (2 0 4)<br> */
	public static final BlockPos Fluid_IN = new BlockPos(2, 0, 4);
	
	/** Template-Location of the Fluid Output Port. (4 0 2)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(4, 0, 2);
	
	/** Template-Location of the Energy Input Ports.<br><pre>2 1 0</pre><br> */
	public static final Set<BlockPos> Energy_IN = ImmutableSet.of(new BlockPos(2, 1, 0));
	
	/** Template-Location of the Redstone Input Port. (0 1 1)<br> */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(0, 1, 1));
	
	public FluidTank tank = new FluidTank(8000, this::acceptsFluid);
	
	public NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	public boolean drilling, spilling;
	public int timer = 0;
	
	private Fluid fluidSpilled = Fluids.EMPTY;
	
	/** Stores the current derrick configuration. */
	@Nullable
	public PipeConfig.Grid gridStorage;
	
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
		
		try{
			this.fluidSpilled = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("spillingfluid")));
		}catch(ResourceLocationException rle){
			this.fluidSpilled = Fluids.EMPTY;
		}
		
		this.tank.readFromNBT(nbt.getCompound("tank"));
		
		if(nbt.contains("grid", NBT.TAG_COMPOUND)){
			this.gridStorage = PipeConfig.Grid.fromCompound(nbt.getCompound("grid"));
		}
		
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
		
		nbt.putString("spillingfluid", this.fluidSpilled.getRegistryName().toString());
		
		nbt.put("tank", this.tank.writeToNBT(new CompoundNBT()));
		
		if(this.gridStorage != null){
			nbt.put("grid", this.gridStorage.toCompound());
		}
		
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
		WellTileEntity well = getOrCreateWell(false);
		if(well == null)
			return false;
		
		int realPipeLength = (getPos().getY() - 1) - well.getPos().getY();
		int concreteNeeded = (CONCRETE.getAmount() * (realPipeLength - well.pipeLength));
		
		if(ExternalModContent.isIEConcrete(fs) && concreteNeeded > 0){
			FluidStack tFluidStack = this.tank.getFluid();
			
			if(ExternalModContent.isIEConcrete(tFluidStack) && tFluidStack.getAmount() >= concreteNeeded){
				return false;
			}
			
			if(concreteNeeded < fs.getAmount()){
				return false;
			}
			
			return true;
		}
		
		return fs.getFluid() == Fluids.WATER && concreteNeeded <= 0;
	}
	
	static final int POWER = 512;
	static final FluidStack WATER = new FluidStack(Fluids.WATER, 125);
	static final FluidStack CONCRETE = ExternalModContent.ieConcreteFluidStack(125);
	
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
				spawnSpillParticles(world, this.pos, this.fluidSpilled, 5, 15.75F);
			}
			
			return;
		}
		
		if(this.world.isAreaLoaded(this.getPos(), 2)){
			boolean forceUpdate = true;
			boolean lastDrilling = this.drilling;
			boolean lastSpilling = this.spilling;
			this.drilling = this.spilling = false;
			
			// Check if lower than 64 and stop working, then also display a message as to why in GUI
			if(this.pos.getY() < 64){
				if(this.fluidSpilled == Fluids.EMPTY){
					this.fluidSpilled = Fluids.WATER;
				}
				this.spilling = true;
			}else{
				if(!isRSDisabled()){
					if(this.energyStorage.extractEnergy(POWER, true) >= POWER){
						WellTileEntity well = getOrCreateWell(this.inventory.get(0) != ItemStack.EMPTY);
						
						if(well != null){
							if(well.pipeLength < well.pipeMaxLength()){
								if(well.pipe <= 0 && getInventory(Inventory.INPUT) != ItemStack.EMPTY){
									ItemStack stack = getInventory(Inventory.INPUT);
									if(stack.getCount() > 0){
										stack.shrink(1);
										well.pipe = WellTileEntity.PIPE_WORTH;
										
										if(stack.getCount() <= 0){
											setInventory(Inventory.INPUT, ItemStack.EMPTY);
										}
										
										well.markDirty();
									}
								}else if(well.pipe > 0){
									final BlockPos dPos = getPos();
									final BlockPos wPos = well.getPos();
									int realPipeLength = ((dPos.getY() - 1) - wPos.getY());
									
									if(well.pipeLength < realPipeLength){
										if(this.tank.drain(CONCRETE, FluidAction.SIMULATE).getAmount() >= CONCRETE.getAmount()){
											this.energyStorage.extractEnergy(POWER, false);
											
											if(this.timer-- <= 0){
												this.timer = 10;
												
												int min = Math.min(well.pipeLength, realPipeLength);
												for(int i = 1;i < min;i++){
													BlockPos current = new BlockPos(dPos.getX(), dPos.getY() - i, dPos.getZ());
													BlockState state = getWorldNonnull().getBlockState(current);
													if(state.getBlock() != IPContent.Blocks.wellPipe){
														getWorldNonnull().destroyBlock(current, false);
														getWorldNonnull().setBlockState(current, IPContent.Blocks.wellPipe.getDefaultState());
													}
												}
												
												int y = dPos.getY() - 1;
												for(;y > wPos.getY();y--){
													BlockPos current = new BlockPos(dPos.getX(), y, dPos.getZ());
													BlockState state = getWorldNonnull().getBlockState(current);
													
													if(state.getBlock() == Blocks.BEDROCK || state.getBlock() == IPContent.Blocks.well){
														break;
													}else if(state.getBlock() != IPContent.Blocks.wellPipe){
														getWorldNonnull().destroyBlock(current, false);
														getWorldNonnull().setBlockState(current, IPContent.Blocks.wellPipe.getDefaultState());
														
														this.tank.drain(CONCRETE, FluidAction.EXECUTE);
														
														well.usePipe();
														break;
													}
												}
											}
											
											this.drilling = true;
										}
									}else{
										if(this.tank.drain(WATER, FluidAction.SIMULATE).getAmount() >= WATER.getAmount()){
											this.tank.drain(WATER, FluidAction.EXECUTE);
											this.energyStorage.extractEnergy(POWER, false);
											
											if(this.timer-- <= 0){
												this.timer = 10;
												
												int min = Math.min(well.pipeLength, realPipeLength);
												for(int i = 1;i < min;i++){
													BlockPos current = new BlockPos(dPos.getX(), dPos.getY() - i, dPos.getZ());
													BlockState state = getWorldNonnull().getBlockState(current);
													if(state.getBlock() != IPContent.Blocks.wellPipe){
														getWorldNonnull().destroyBlock(current, false);
														getWorldNonnull().setBlockState(current, IPContent.Blocks.wellPipe.getDefaultState());
													}
												}
												
												well.usePipe();
											}
											
											this.drilling = true;
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
				}
			}
			
			if(forceUpdate || (lastDrilling != this.drilling) || (lastSpilling != this.spilling)){
				updateMasterBlock(null, true);
			}
		}
	}
	
	
	private WellTileEntity wellCache = null;
	/**
	 * Create or Get the {@link WellTileEntity}.
	 * 
	 * @param popList Set to true, to try and populate the
	 *        {@link WellTileEntity#tappedIslands} list.
	 * @return WellTileEntity or possibly null
	 */
	public WellTileEntity getOrCreateWell(boolean popList){
		if((this.wellCache == null) || (this.wellCache != null && this.wellCache.isRemoved())){
			World world = this.getWorldNonnull();
			WellTileEntity well = null;
			
			// TODO !Replace "y >= 0" in 1.18 with something that can go negative
			for(int y = getPos().getY() - 1;y >= 0;y--){
				BlockPos current = new BlockPos(this.getPos().getX(), y, this.getPos().getZ());
				BlockState state = world.getBlockState(current);
				
				if(state.getBlock() == IPContent.Blocks.well){
					well = (WellTileEntity) world.getTileEntity(current);
					break;
				}else if(state.getBlock() == Blocks.BEDROCK){
					world.setBlockState(current, IPContent.Blocks.well.getDefaultState());
					well = (WellTileEntity) world.getTileEntity(current);
					break;
				}
			}
			
			if(popList && well != null && well.tappedIslands.isEmpty()){
				if(this.gridStorage != null){
					transferGridDataToWell(well);
				}else{
					well.tappedIslands.add(new ColumnPos(this.pos.getX(), this.pos.getZ()));
					well.markDirty();
				}
			}
			
			if(well != null){
				this.wellCache = well;
			}
			return this.wellCache;
		}
		
		return this.wellCache;
	}
	
	public void transferGridDataToWell(@Nonnull WellTileEntity well){
		if(well != null){
			int additionalPipes = 0;
			List<ColumnPos> list = new ArrayList<>();
			PipeConfig.Grid grid = this.gridStorage;
			for(int j = 0;j < grid.getHeight();j++){
				for(int i = 0;i < grid.getWidth();i++){
					int type = grid.get(i, j);
					
					if(type > 0){
						switch(type){
							case PipeConfig.PIPE_PERFORATED:
							case PipeConfig.PIPE_PERFORATED_FIXED:{
								int x = i - (grid.getWidth() / 2);
								int z = j - (grid.getHeight() / 2);
								ColumnPos pos = new ColumnPos(this.pos.getX() + x, this.pos.getZ() + z);
								list.add(pos);
							}
							case PipeConfig.PIPE_NORMAL:{
								additionalPipes++;
							}
						}
					}
				}
			}
			
			well.tappedIslands = list;
			well.additionalPipes = additionalPipes;
			well.markDirty();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void spawnSpillParticles(World world, BlockPos pos, Fluid fluid, int particles, float yOffset){
		if(fluid == null || fluid == Fluids.EMPTY){
			return;
		}
		
		for(int i = 0;i < particles;i++){
			float xa = (world.rand.nextFloat() - .5F) / 2F;
			float ya = 0.75F + (world.rand.nextFloat() * 0.25F);
			float za = (world.rand.nextFloat() - .5F) / 2F;
			
			float rx = (world.rand.nextFloat() - .5F) * 0.5F;
			float rz = (world.rand.nextFloat() - .5F) * 0.5F;
			
			double x = (pos.getX() + 0.5) + rx;
			double y = (pos.getY() + yOffset);
			double z = (pos.getZ() + 0.5) + rz;
			
			world.addParticle(new FluidParticleData(fluid), x, y, z, xa, ya, za);
		}
	}
	
	@Override
	public void disassemble(){
		if(this.formed && !this.world.isRemote){
			// Do this even if this is the master itself
			DerrickTileEntity master = master();
			
			World world = master.getWorldNonnull();
			BlockPos dPos = master.getPos();
			// TODO !Replace "y >= 0" in 1.18 with something that can go negative
			for(int y = getPos().getY() - 1;y >= 0;y--){
				BlockPos current = new BlockPos(dPos.getX(), y, dPos.getZ());
				TileEntity teLow = world.getTileEntity(current);
				
				if(teLow instanceof WellTileEntity && !((WellTileEntity) teLow).drillingCompleted){
					world.setBlockState(current, Blocks.BEDROCK.getDefaultState());
					break;
				}
			}
		}
		
		// Calling it after just to be on the safe side
		super.disassemble();
	}
	
	public ItemStack getInventory(Inventory inv){
		return this.inventory.get(inv.id());
	}
	
	public ItemStack setInventory(Inventory inv, ItemStack stack){
		return this.inventory.set(inv.id(), stack);
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
		return new IFluidTank[]{this.tank};
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
					return new IFluidTank[]{master.tank};
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
				
				if(master == null || master.tank.getFluidAmount() >= master.tank.getCapacity()){
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
