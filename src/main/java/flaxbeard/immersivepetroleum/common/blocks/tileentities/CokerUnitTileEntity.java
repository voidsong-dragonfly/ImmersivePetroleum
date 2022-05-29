package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.common.IPMenuTypes;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import flaxbeard.immersivepetroleum.common.gui.IPMenuProvider;
import flaxbeard.immersivepetroleum.common.multiblocks.CokerUnitMultiblock;
import flaxbeard.immersivepetroleum.common.util.FluidHelper;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class CokerUnitTileEntity extends PoweredMultiblockBlockEntity<CokerUnitTileEntity, CokerUnitRecipe> implements IPMenuProvider<CokerUnitTileEntity>, IBlockBounds, IPServerTickableTile, IPClientTickableTile{
	
	public static enum Inventory{
		/** Inventory Item Input */
		INPUT,
		/** Inventory Fluid Input (Filled Bucket) */
		INPUT_FILLED,
		/** Inventory Fluid Input (Empty Bucket) */
		INPUT_EMPTY,
		/** Inventory Fluid Output (Empty Bucket) */
		OUTPUT_EMPTY,
		/** Inventory Fluid Output (Filled Bucket) */
		OUTPUT_FILLED;
		
		public int id(){
			return ordinal();
		}
	}
	
	/** Input Fluid Tank<br> */
	public static final int TANK_INPUT = 0;
	
	/** Output Fluid Tank<br> */
	public static final int TANK_OUTPUT = 1;
	
	/** Coker Chamber A<br> */
	public static final int CHAMBER_A = 0;
	
	/** Coker Chamber B<br> */
	public static final int CHAMBER_B = 1;
	
	/** Template-Location of the Chamber A Item Output */
	public static final BlockPos Chamber_A_OUT = new BlockPos(2, 2, 2);
	
	/** Template-Location of the Chamber B Item Output */
	public static final BlockPos Chamber_B_OUT = new BlockPos(6, 2, 2);
	
	/** Template-Location of the Fluid Input Port. (6 0 0)<br> */
	public static final BlockPos Fluid_IN = new BlockPos(6, 0, 0);
	
	/** Template-Location of the Fluid Output Port. (3 0 4)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(3, 0, 4);
	
	/** Template-Location of the Item Input Port. (4 0 0)<br> */
	public static final BlockPos Item_IN = new BlockPos(4, 0, 0);
	
	/** Template-Location of the Energy Input Ports.<br><pre>1 1 0<br>2 1 0<br>3 1 0</pre><br> */
	public static final Set<MultiblockFace> Energy_IN = ImmutableSet.of(
			new MultiblockFace(1, 1, 0, RelativeBlockFace.UP),
			new MultiblockFace(2, 1, 0, RelativeBlockFace.UP),
			new MultiblockFace(3, 1, 0, RelativeBlockFace.UP)
	);

	/** Template-Location of the Redstone Input Port. (6 1 4)<br> */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(6, 1, 4));
	
	public final NonNullList<ItemStack> inventory = NonNullList.withSize(Inventory.values().length, ItemStack.EMPTY);
	public final FluidTank[] bufferTanks = {new FluidTank(16000), new FluidTank(16000)};
	public final CokingChamber[] chambers = {new CokingChamber(64, 8000), new CokingChamber(64, 8000)};
	public CokerUnitTileEntity(BlockEntityType<CokerUnitTileEntity> type, BlockPos pWorldPosition, BlockState pBlockState){
		super(CokerUnitMultiblock.INSTANCE, 24000, true, type, pWorldPosition, pBlockState);
		bufferTanks[TANK_INPUT].setValidator(fs -> CokerUnitRecipe.hasRecipeWithInput(fs, true));
	}
	
	@Override
	public void readCustomNBT(CompoundTag nbt, boolean descPacket){
		super.readCustomNBT(nbt, descPacket);
		
		this.bufferTanks[TANK_INPUT].readFromNBT(nbt.getCompound("tank0"));
		this.bufferTanks[TANK_OUTPUT].readFromNBT(nbt.getCompound("tank1"));
		
		this.chambers[CHAMBER_A].readFromNBT(nbt.getCompound("chamber0"));
		this.chambers[CHAMBER_B].readFromNBT(nbt.getCompound("chamber1"));
		
		if(!descPacket){
			readInventory(nbt.getCompound("inventory"));
		}
	}
	
	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean descPacket){
		super.writeCustomNBT(nbt, descPacket);
		
		nbt.put("tank0", this.bufferTanks[TANK_INPUT].writeToNBT(new CompoundTag()));
		nbt.put("tank1", this.bufferTanks[TANK_OUTPUT].writeToNBT(new CompoundTag()));
		
		nbt.put("chamber0", this.chambers[CHAMBER_A].writeToNBT(new CompoundTag()));
		nbt.put("chamber1", this.chambers[CHAMBER_B].writeToNBT(new CompoundTag()));
		
		if(!descPacket){
			nbt.put("inventory", writeInventory(this.inventory));
		}
	}
	
	protected void readInventory(CompoundTag nbt){
		NonNullList<ItemStack> list = NonNullList.create();
		ContainerHelper.loadAllItems(nbt, list);
		
		for(int i = 0;i < this.inventory.size();i++){
			ItemStack stack = ItemStack.EMPTY;
			if(i < list.size()){
				stack = list.get(i);
			}
			
			this.inventory.set(i, stack);
		}
	}
	
	protected CompoundTag writeInventory(NonNullList<ItemStack> list){
		return ContainerHelper.saveAllItems(new CompoundTag(), list);
	}
	
	@Override
	public void doGraphicalUpdates(){
		updateMasterBlock(null, true);
	}
	
	@Override
	public CokerUnitRecipe findRecipeForInsertion(ItemStack inserting){
		return null;
	}
	
	@Override
	public boolean additionalCanProcessCheck(MultiblockProcess<CokerUnitRecipe> process){
		return false;
	}
	
	@Override
	public void doProcessOutput(ItemStack output){
	}

	private final MultiblockCapability<IItemHandler> insertionHandler = MultiblockCapability.make(
			this, be -> be.insertionHandler, CokerUnitTileEntity::master,
			new ResettableCapability<>(new IEInventoryHandler(1, this, 0, new boolean[]{true}, new boolean[8]))
	);
	private final MultiblockCapability<IFluidHandler> fluidOutHandler = MultiblockCapability.make(
			this, be -> be.fluidOutHandler, CokerUnitTileEntity::master,
			registerFluidOutput(bufferTanks[TANK_OUTPUT])
	);
	private final MultiblockCapability<IFluidHandler> fluidInHandler = MultiblockCapability.make(
			this, be -> be.fluidInHandler, CokerUnitTileEntity::master,
			registerFluidInput(bufferTanks[TANK_INPUT])
	);
	
	@Override
	public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing){
		if ((facing == null || this.posInMultiblock.equals(Item_IN)) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return insertionHandler.getAndCast();
		} else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing == getFacing())){
			if (this.posInMultiblock.equals(Fluid_OUT)){
				return fluidOutHandler.getAndCast();
			} else if (this.posInMultiblock.equals(Fluid_IN)) {
				return fluidInHandler.getAndCast();
			}
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public void doProcessFluidOutput(FluidStack output){
	}
	
	@Override
	public void onProcessFinish(MultiblockProcess<CokerUnitRecipe> process){
	}
	
	@Override
	public void tickClient(){
		if(isDummy() || isRSDisabled()){
			return;
		}
		
		boolean debug = false;
		for(int i = 0;i < this.chambers.length;i++){
			if(debug || this.chambers[i].getState() == CokingState.DUMPING){
				BlockPos cOutPos = getBlockPosForPos(i == 0 ? Chamber_A_OUT : Chamber_B_OUT);
				Vec3 origin = new Vec3(cOutPos.getX() + 0.5, cOutPos.getY() + 0.125, cOutPos.getZ() + 0.5);
				for(int j = 0;j < 10;j++){
					double rX = (Math.random() - 0.5) * 0.4;
					double rY = (Math.random() - 0.5) * 0.5;
					double rdx = (Math.random() - 0.5) * 0.05;
					double rdy = (Math.random() - 0.5) * 0.05;
					
					level.addParticle(ParticleTypes.SMOKE,
							origin.x + rX, origin.y, origin.z + rY,
							rdx, -(Math.random() * 0.06 + 0.11), rdy);
				}
			}
		}
	}
	
	@Override
	public void tickServer(){
		if(isDummy() || isRSDisabled()){
			return;
		}
		
		boolean update = false;
		
		ItemStack inputStack = getInventory(Inventory.INPUT);
		FluidStack inputFluid = this.bufferTanks[TANK_INPUT].getFluid();
		
		if(!inputStack.isEmpty() && inputFluid.getAmount() > 0 && CokerUnitRecipe.hasRecipeWithInput(inputStack, inputFluid)){
			CokerUnitRecipe recipe = CokerUnitRecipe.findRecipe(inputStack, inputFluid);
			
			if(recipe != null && inputStack.getCount() >= recipe.inputItem.getCount() && inputFluid.getAmount() >= recipe.inputFluid.getAmount()){
				for(int i = 0;i < this.chambers.length;i++){
					CokingChamber chamber = this.chambers[i];
					boolean skipNext = false;
					
					switch(chamber.getState()){
						case STANDBY:{
							if(chamber.setRecipe(recipe)){
								update = true;
								skipNext = true;
							}
							break;
						}
						case PROCESSING:{
							int acceptedStack = chamber.addStack(copyStack(inputStack, recipe.inputItem.getCount()), true);
							if(acceptedStack >= recipe.inputItem.getCount()){
								acceptedStack = Math.min(acceptedStack, inputStack.getCount());
								
								chamber.addStack(copyStack(inputStack, acceptedStack), false);
								inputStack.shrink(acceptedStack);
								
								skipNext = true;
								update = true;
							}
							break;
						}
						default: break;
					}
					
					if(skipNext){
						break;
					}
				}
			}
		}
		
		for(int i = 0;i < this.chambers.length;i++){
			update |= this.chambers[i].tick(this, i);
		}
		
		if(!getInventory(Inventory.INPUT_FILLED).isEmpty() && this.bufferTanks[TANK_INPUT].getFluidAmount() < this.bufferTanks[TANK_INPUT].getCapacity()){
			ItemStack container = Utils.drainFluidContainer(this.bufferTanks[TANK_INPUT], getInventory(Inventory.INPUT_FILLED), getInventory(Inventory.INPUT_EMPTY));
			if(!container.isEmpty()){
				if(!getInventory(Inventory.INPUT_EMPTY).isEmpty() && ItemHandlerHelper.canItemStacksStack(getInventory(Inventory.INPUT_EMPTY), container)){
					getInventory(Inventory.INPUT_EMPTY).grow(container.getCount());
				}else if(getInventory(Inventory.INPUT_EMPTY).isEmpty()){
					setInventory(Inventory.INPUT_EMPTY, container.copy());
				}
				
				getInventory(Inventory.INPUT_FILLED).shrink(1);
				if(getInventory(Inventory.INPUT_FILLED).getCount() <= 0){
					setInventory(Inventory.INPUT_FILLED, ItemStack.EMPTY);
				}
				
				update = true;
			}
		}
		
		if(this.bufferTanks[TANK_OUTPUT].getFluidAmount() > 0){
			if(!getInventory(Inventory.OUTPUT_EMPTY).isEmpty()){
				ItemStack filledContainer = FluidHelper.fillFluidContainer(this.bufferTanks[TANK_OUTPUT], getInventory(Inventory.OUTPUT_EMPTY), getInventory(Inventory.OUTPUT_FILLED), null);
				if(!filledContainer.isEmpty()){
					
					if(getInventory(Inventory.OUTPUT_FILLED).getCount() == 1 && !FluidHelper.isFluidContainerFull(filledContainer)){
						setInventory(Inventory.OUTPUT_FILLED, filledContainer.copy());
					}else{
						if(!getInventory(Inventory.OUTPUT_FILLED).isEmpty() && ItemHandlerHelper.canItemStacksStack(getInventory(Inventory.OUTPUT_FILLED), filledContainer)){
							getInventory(Inventory.OUTPUT_FILLED).grow(filledContainer.getCount());
						}else if(getInventory(Inventory.OUTPUT_FILLED).isEmpty()){
							setInventory(Inventory.OUTPUT_FILLED, filledContainer.copy());
						}
						
						getInventory(Inventory.OUTPUT_EMPTY).shrink(1);
						if(getInventory(Inventory.OUTPUT_EMPTY).getCount() <= 0){
							setInventory(Inventory.OUTPUT_EMPTY, ItemStack.EMPTY);
						}
					}
					
					update = true;
				}
			}
			
			BlockPos outPos = getBlockPosForPos(Fluid_OUT).relative(getFacing().getOpposite());
			update |= FluidUtil.getFluidHandler(this.level, outPos, getFacing()).map(out -> {
				if(this.bufferTanks[TANK_OUTPUT].getFluidAmount() > 0){
					FluidStack fs = FluidHelper.copyFluid(this.bufferTanks[TANK_OUTPUT].getFluid(), 100, true);
					int accepted = out.fill(fs, FluidAction.SIMULATE);
					if(accepted > 0){
						int drained = out.fill(FluidHelper.copyFluid(fs, Math.min(fs.getAmount(), accepted), true), FluidAction.EXECUTE);
						this.bufferTanks[TANK_OUTPUT].drain(FluidHelper.copyFluid(fs, drained), FluidAction.EXECUTE);
						return true;
					}
				}
				return false;
			}).orElse(false);
		}
		
		if(update){
			updateMasterBlock(null, true);
		}
		
		updateComparatorOutput();
	}
	
	int updateDelay = 0;
	int lastCompared = 0;
	private void updateComparatorOutput(){
		boolean update = false;
		
		ItemStack stack = getInventory(Inventory.INPUT);
		if(!stack.isEmpty()){
			int compared = Mth.clamp(Mth.floor(stack.getCount() / (float) Math.min(getSlotLimit(Inventory.INPUT.id()), stack.getMaxStackSize()) * 15), 0, 15);
			if(compared != lastCompared){
				lastCompared = compared;
				update = true;
			}
		}else if(lastCompared != 0){
			lastCompared = 0;
			update = true;
		}
		
		if(update){
			getRedstonePos().forEach(pos -> {
				BlockPos p = getBlockPosForPos(pos);
				level.updateNeighborsAt(p, level.getBlockState(p).getBlock());
			});
		}
	}
	
	@Override
	public int getComparatorInputOverride(){
		if(this.isRedstonePos()){
			CokerUnitTileEntity master = master();
			if(master != null){
				return master.lastCompared;
			}
		}
		return 0;
	}
	
	private ItemStack copyStack(ItemStack stack, int amount){
		ItemStack copy = stack.copy();
		copy.setCount(amount);
		return copy;
	}
	
	public ItemStack getInventory(Inventory inv){
		return this.inventory.get(inv.id());
	}
	
	public ItemStack setInventory(Inventory inv, ItemStack stack){
		return this.inventory.set(inv.id(), stack);
	}
	
	@Override
	public NonNullList<ItemStack> getInventory(){
		return this.inventory;
	}
	
	@Override
	public int getSlotLimit(int slot){
		return 64;
	}
	
	@Override
	protected CokerUnitRecipe getRecipeForId(Level level, ResourceLocation id){
		return CokerUnitRecipe.recipes.get(id);
	}
	
	@Override
	public Set<MultiblockFace> getEnergyPos(){
		return Energy_IN;
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return Redstone_IN;
	}
	
	@Override
	public IFluidTank[] getInternalTanks(){
		return this.bufferTanks;
	}
	
	@Override
	public int[] getOutputSlots(){
		return new int[0];
	}
	
	@Override
	public int[] getOutputTanks(){
		return new int[]{TANK_OUTPUT};
	}
	
	@Override
	public int getMaxProcessPerTick(){
		return 1;
	}
	
	@Override
	public int getProcessQueueMaxLength(){
		return 2;
	}
	
	@Override
	public float getMinProcessDistance(MultiblockProcess<CokerUnitRecipe> process){
		return 1.0F;
	}
	
	@Override
	public int getComparatedSize(){
		return 1;
	}
	
	@Override
	public CokerUnitTileEntity getGuiMaster(){
		return master();
	}
	
	@Override
	public boolean canUseGui(Player player){
		return this.formed;
	}
	
	/** Locations that don't require sneaking to avoid the GUI */
	public boolean skipGui(BlockHitResult hit){
		Direction facing = getFacing();
		
		// Conveyor locations
		if(this.posInMultiblock.getY() == 0 && this.posInMultiblock.getZ() == 2 && hit.getDirection() == Direction.UP){
			return true;
		}
		
		// All power input sockets
		if(CokerUnitTileEntity.Energy_IN.stream().anyMatch((t) -> t.posInMultiblock() == this.posInMultiblock) && hit.getDirection() == facing){
			return true;
		}
		
		// Redstone controller input
		if(CokerUnitTileEntity.Redstone_IN.contains(this.posInMultiblock) && hit.getDirection() == facing.getOpposite()){
			return true;
		}
		
		// Fluid I/O Ports
		if(this.posInMultiblock.equals(CokerUnitTileEntity.Fluid_IN) || this.posInMultiblock.equals(CokerUnitTileEntity.Fluid_OUT)){
			return true;
		}
		
		// Item input port
		if(this.posInMultiblock.equals(CokerUnitTileEntity.Item_IN)){
			return true;
		}
		
		return false;
	}

	@Nonnull
	@Override
	public BEContainerIP<? super CokerUnitTileEntity, ?> getContainerTypeIP(){
		return IPMenuTypes.COKER;
	}

	@Override
	public boolean isInWorldProcessingMachine(){
		return false;
	}
	
	@Override
	public boolean isStackValid(int slot, ItemStack stack){
		return true;
	}
	
	public boolean isLadder(){
		int bX = posInMultiblock.getX();
		int bY = posInMultiblock.getY();
		int bZ = posInMultiblock.getZ();
		
		// Primary Ladder
		if((bX == 0 && bZ == 2) && (bY >= 3 && bY <= 12)){
			return true;
		}
		
		// Secondary Ladder
		if((bX == 1 && bZ == 2) && (bY >= 13 && bY <= 17)){
			return true;
		}
		
		return false;
	}
	
	private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(CokerUnitTileEntity::getShape);
	public static boolean updateShapes = false;
	@Override
	public VoxelShape getBlockBounds(CollisionContext ctx){
		if(updateShapes){
			updateShapes = false;
			SHAPES = CachedShapesWithTransform.createForMultiblock(CokerUnitTileEntity::getShape);
		}
		
		return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	private static List<AABB> getShape(BlockPos posInMultiblock){
		int bX = posInMultiblock.getX();
		int bY = posInMultiblock.getY();
		int bZ = posInMultiblock.getZ();
		
		List<AABB> main = new ArrayList<>();
		
		// Beams
		if(bY >= 3 && bY <= 12){
			// Vertical Corners
			if(bX == 0 && bZ == 0){
				main.add(new AABB(0.125, 0.0, 0.125, 0.375, 1.0, 0.375)); // Corner Beam -X-Z
			}else if(bX == 0 && bZ == 4){
				main.add(new AABB(0.125, 0.0, 0.625, 0.375, 1.0, 0.875)); // Corner Beam -X+Z
			}else if(bX == 8 && bZ == 0){
				main.add(new AABB(0.625, 0.0, 0.125, 0.875, 1.0, 0.375)); // Corner Beam +X-Z
			}else if(bX == 8 && bZ == 4){
				main.add(new AABB(0.625, 0.0, 0.625, 0.875, 1.0, 0.875)); // Corner Beam +X+Z
			}
			
			// Vertical Center
			if(bX == 4 && bZ == 4){
				main.add(new AABB(0.375, 0.0, 0.625, 0.625, 1.0, 0.875)); // Center Beam +Z
			}else if(bY >= 4 && bX == 4 && bZ == 0){
				main.add(new AABB(0.375, 0.0, 0.125, 0.625, 1.0, 0.375)); // Center Beam -Z
			}
			
			// Horiontal
			if(bY == 5 || bY == 10){
				if(bX > 0 && bX < 8){
					if(bZ == 0){
						main.add(new AABB(0.0, 0.125, 0.125, 1.0, 0.375, 0.375)); // Horizontal Beam -Z
					}else if(bZ == 4){
						main.add(new AABB(0.0, 0.125, 0.625, 1.0, 0.375, 0.875)); // Horizontal Beam +Z
					}
				}else{
					if(bX == 0 && bZ == 0){
						main.add(new AABB(0.125, 0.125, 0.125, 0.375, 0.375, 1.0)); // Beam Intersection -X
						main.add(new AABB(0.125, 0.125, 0.125, 1.0, 0.375, 0.375)); // Beam Intersection -Z
					}else if(bX == 0 && bZ == 4){
						main.add(new AABB(0.125, 0.125, 0.125, 0.375, 0.375, 0.875)); // Beam Intersection -X
						main.add(new AABB(0.125, 0.125, 0.625, 1.0, 0.375, 0.875)); // Beam Intersection +Z
					}else if(bX == 8 && bZ == 0){
						main.add(new AABB(0.625, 0.125, 0.125, 0.875, 0.375, 1.0)); // Beam Intersection +X
						main.add(new AABB(0.125, 0.125, 0.125, 0.875, 0.375, 0.375)); // Beam Intersection -Z
					}else if(bX == 8 && bZ == 4){
						main.add(new AABB(0.0, 0.125, 0.625, 0.875, 0.375, 0.875)); // Beam Intersection +Z
						main.add(new AABB(0.625, 0.125, 0.0, 0.875, 0.375, 0.875)); // Beam Intersection +X
					}
					
					if(bX == 0 && (bZ == 1 || bZ == 3)){
						main.add(new AABB(0.125, 0.125, 0.0, 0.375, 0.375, 1.0)); // Horizontal Beam -X
					}
					
					if(bX == 8 && (bZ > 0 && bZ < 4)){
						main.add(new AABB(0.625, 0.125, 0.0, 0.875, 0.375, 1.0)); // Horizontal Beam +X
					}
				}
			}
		}
		
		// Ground layer slabs
		if(bY == 0){
			if((bZ == 1 || bZ == 3) || ((bX == 5 || bX == 7) && bZ == 0) || ((bX == 1 || (bX >= 5 && bX <= 7)) && bZ == 4)){
				main.add(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
			}
		}
		
		// Fluid output box bottom
		if(bY == 0 && bZ == 4 && (bX == 2 || bX == 3)){
			main.add(new AABB(0.0, 0.0, -0.25, 1.0, 1.0, 1.0));
		}
		// Fluid output box top
		if(bY == 1 && bZ == 4 && (bX == 2 || bX == 3)){
			main.add(new AABB(0.0, 0.0, -0.25, 1.0, 0.625, 1.0));
		}
		
		// Redstone Controller
		if(bY == 0 && bX == 6 && bZ == 4){
			main.add(new AABB(0.75, 0.5, 0.625, 0.875, 1.0, 0.875));
			main.add(new AABB(0.125, 0.5, 0.625, 0.25, 1.0, 0.875));
		}else if(bY == 1 && bX == 6 && bZ == 4){
			main.add(new AABB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
		}
		
		// Power Sockets
		if(bY == 1 && bZ == 0 && (bX>=1 && bX<=3)){
			main.add(new AABB(0.0, 0.0, 0.25, 1.0, 1.0, 1.25));
			main.add(new AABB(0.25, 0.25, 0.0, 0.75, 0.75, 0.5));
		}
		if(bY == 0 && bZ == 0 && (bX>=1 && bX<=3)){
			main.add(new AABB(0.0, 0.0, 0.25, 1.0, 1.0, 1.25));
		}
		
		// Slopes
		if(bY == 3){
			if(bZ == 0){
				if(bX == 3){
					main.add(new AABB(0.0, 0.0, 0.0625, 0.25, 0.25, 1.0));
					main.add(new AABB(0.25, 0.0, 0.0625, 0.5, 0.5, 1.0));
					main.add(new AABB(0.50, 0.0, 0.0625, 0.75, 0.75, 1.0));
					main.add(new AABB(0.75, 0.0, 0.0625, 1.0, 1.0, 1.0));
				}
				if(bX == 4){
					main.add(new AABB(0.0, 0.0, 0.0625, 1.0, 1.0, 1.0));
				}
				if(bX == 5){
					main.add(new AABB(0.0, 0.0, 0.0625, 0.25, 1.0, 1.0));
					main.add(new AABB(0.25, 0.0, 0.0625, 0.5, 0.75, 1.0));
					main.add(new AABB(0.50, 0.0, 0.0625, 0.75, 0.5, 1.0));
					main.add(new AABB(0.75, 0.0, 0.0625, 1.0, 0.25, 1.0));
				}
			}else if(bX == 4 && bZ == 3){
				main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.25));
				main.add(new AABB(0.0, 0.0, 0.25, 1.0, 0.75, 0.5));
				main.add(new AABB(0.0, 0.0, 0.5, 1.0, 0.5, 0.75));
				main.add(new AABB(0.0, 0.0, 0.75, 1.0, 0.25, 1.0));
			}
		}
		
		// First and Second Platform Shape
		if((bY == 7 || bY == 12) && !(bX == 0 && bZ == 2)){
			if(!(bX > 0 && bX < 8 && bZ > 0 && bZ < 4) || (bX==4 && bZ>=1 && bZ<=3)){
				main.add(new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
			}
		}
		
		// Top Platform
		if(bY == 17){
			if((bX >= 1 && bX <= 7) && (bZ == 1 || bZ == 3) || (bX == 7 && bZ == 2)){
				main.add(new AABB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
			}
		}
		
		// Primary Ladder
		if(bX == 0 && bZ == 2){
			if(bY >= 3 && bY <= 12){
				main.add(new AABB(1.005, 0.0, 0.125, 1.005, 1.0, 0.875));
				
				if(bY >= 5){
					main.add(new AABB(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0));
					if(!(bY==8 || bY==9)){
						main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.0625));
						main.add(new AABB(0.0, 0.0, 0.9375, 1.0, 1.0, 1.0));
					}
				}
			}
		}
		
		// Secondary Ladder
		if(bX == 1 && bZ == 2){
			if(bY >= 13 && bY <= 17){
				main.add(new AABB(0.875, 0.0, 0.125, 0.9375, 1.0, 0.875));
				
				if(bY >= 15){ // Cage
					main.add(new AABB(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0));
					main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.0625));
					main.add(new AABB(0.0, 0.0, 0.9375, 1.0, 1.0, 1.0));
				}
			}
		}
		
		// All Pipes
		{
			if(bX == 2 && bZ == 4){
				if(bY == 1){
					main.add(new AABB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75)); // Pipe Y
					main.add(new AABB(0.125, 0.875, 0.875, 0.875, 1.0, 0.125)); // Pipe Connector +Y
				}
				if(bY >= 3 && bY <= 6){
					main.add(new AABB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75)); // Pipe Y
					if(bY==3 || bY==5 || bY==6){
						main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125)); // Pipe Connector -Y
					}
					if(bY==4 || bY==5 || bY==6){
						main.add(new AABB(0.125, 0.875, 0.875, 0.875, 1.0, 0.125)); // Pipe Connector +Y
					}
				}
			}
			
			if(bX == 6 && bZ == 0){
				if(bY >= 1 && bY <= 6 && bY != 2){
					main.add(new AABB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75)); // Pipe Y
				}
				
				if(bY == 1){
					main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125)); // Pipe Connector -Y
					main.add(new AABB(0.125, 0.875, 0.875, 0.875, 1.0, 0.125)); // Pipe Connector +Y
				}
				if(bY == 3){
					main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125)); // Pipe Connector -Y
				}
				if(bY == 6){
					main.add(new AABB(0.125, 0.875, 0.875, 0.875, 1.0, 0.125)); // Pipe Connector +Y
				}
			}
			
			// Pipes in slabs
			if(bY == 7){
				switch(bX){
					case 2:{
						if(bZ == 4){
							main.add(new AABB(0.875, 0.125, 0.875, 1.0, 0.875, 0.125)); // Pipe Connector +X
							main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125)); // Pipe Connector -Y
							main.add(new AABB(0.25, 0.125, 0.75, 0.875, 0.875, 0.25)); // Pipe Bend -Y +X
						}
						break;
					}
					case 3:{
						if(bZ == 4){
							main.add(new AABB(0.0, 0.25, 0.75, 1.0, 0.75, 0.25)); // Pipe X
							main.add(new AABB(0.0, 0.125, 0.875, 0.125, 0.875, 0.125)); // Pipe Connector -X
						}
						break;
					}
					case 4:{
						if(bZ == 4){
							main.add(new AABB(0.0, 0.25, 0.75, 1.0, 0.75, 0.25)); // Pipe X
						}
						break;
					}
					case 5:{
						if(bZ == 4){
							main.add(new AABB(0.0, 0.25, 0.75, 1.0, 0.75, 0.25)); // Pipe X
							main.add(new AABB(0.875, 0.125, 0.875, 1.0, 0.875, 0.125)); // Pipe Connector +X
						}
						break;
					}
					case 6:{
						if(bZ == 0){
							main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125)); // Pipe Connector -Y
							main.add(new AABB(0.875, 0.125, 0.875, 1.0, 0.875, 0.125)); // Pipe Connector +X
							main.add(new AABB(0.25, 0.125, 0.75, 0.875, 0.75, 0.25)); // Pipe Bend 					
						}else if(bZ == 4){
							main.add(new AABB(0.0, 0.125, 0.875, 0.125, 0.875, 0.125)); // Pipe Connector -X
							main.add(new AABB(0.125, 0.25, 0.75, 0.75, 0.75, 0.125)); // Pipe Bend -X -Z
						}
						break;
					}
					case 7:{
						if(bZ == 0){
							main.add(new AABB(0.0, 0.125, 0.875, 0.125, 0.875, 0.125)); // Pipe Connector -X
							main.add(new AABB(0.875, 0.125, 0.875, 1.0, 0.875, 0.125)); // Pipe Connector +X
							main.add(new AABB(0.0, 0.25, 0.75, 1.0, 0.75, 0.25)); // Pipe X
						}
						break;
					}
					case 8:{
						if(bZ == 0){
							main.add(new AABB(0.0, 0.125, 0.875, 0.125, 0.875, 0.125)); // Pipe Connector -X
							main.add(new AABB(0.125, 0.125, 0.875, 0.875, 0.875, 1.0)); // Pipe Connector +Z
							main.add(new AABB(0.625, 0.0, 0.125, 0.875, 1.0, 0.375)); // Vertical Corner Beam +X-Z
							main.add(new AABB(0.125, 0.25, 0.875, 0.75, 0.75, 0.25)); // Pipe Bend -X +Z
						}else if(bZ == 1){
							main.add(new AABB(0.125, 0.125, 0.125, 0.875, 0.875, 0.0)); // Pipe Connector -Z
							main.add(new AABB(0.125, 0.125, 0.875, 0.875, 0.875, 1.0)); // Pipe Connector +Z
							main.add(new AABB(0.25, 0.25, 0.0, 0.75, 0.75, 1.0)); // Pipe X
						}else if(bZ == 2){
							main.add(new AABB(0.125, 0.125, 0.125, 0.875, 0.875, 0.0)); // Pipe Connector -Z
							main.add(new AABB(0.25, 0.25, 0.125, 0.75, 0.875, 0.75)); // Pipe Bend -Y +X
						}else if(bZ == 4){
							main.add(new AABB(0.625, 0.0, 0.625, 0.875, 1.0, 0.875)); // Vertical Corner Beam +X+Z
						}
						break;
					}
				}
			}
			
			// Vertical Pipe to the one below
			if(bX == 8 && bZ == 2){
				if(bY >= 8 && bY <= 13){
					main.add(new AABB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75)); // Pipe Y
				}
				if(bY == 8){
					main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125)); // Pipe Connector -Y
				}
				if(bY == 13){
					main.add(new AABB(0.125, 0.875, 0.875, 0.875, 1.0, 0.125)); // Pipe Connector +Y
				}
			}
			
			// Horizontal Pipe to the one below
			if(bY == 14){
				if(bX >= 3 && bX <= 6 && bZ == 2){
					main.add(new AABB(0.0, 0.25, 0.75, 1.0, 0.75, 0.25)); // Pipe X
					
					if(bX == 6){
						main.add(new AABB(0.875, 0.125, 0.875, 1.0, 0.875, 0.125)); // Pipe Connector +X
					}
				}
				if(bX == 7 && bZ == 2){
					main.add(new AABB(0.0, 0.125, 0.875, 0.125, 0.875, 0.125)); // Pipe Connector -X
					main.add(new AABB(0.875, 0.125, 0.875, 1.0, 0.875, 0.125)); // Pipe Connector +X
					main.add(new AABB(0.0, 0.25, 0.75, 1.0, 0.75, 0.25)); // Pipe X
				}
				if(bX == 8 && bZ == 2){
					main.add(new AABB(0.0, 0.125, 0.875, 0.125, 0.875, 0.125)); // Pipe Connector -X
					main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125)); // Pipe Connector -Y
					main.add(new AABB(0.125, 0.125, 0.75, 0.75, 0.75, 0.25)); // Pipe Bend -Y +X
				}
			}
			
			// Top 2 Vertical Pipes
			if(bY >= 13 && bY <= 22){
				if(bX == 3 && bZ == 2){
					main.add(new AABB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75).move(-0.25, 0, 0)); // Pipe Y
					if(bY == 13){
						main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125).move(-0.25, 0, 0)); // Pipe Connector -Y
					}
				}
				
				if(bX == 5 && bZ == 2){
					main.add(new AABB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75).move(0.25, 0, 0)); // Pipe Y
					if(bY == 13){
						main.add(new AABB(0.125, 0.0, 0.875, 0.875, 0.125, 0.125).move(0.25, 0, 0)); // Pipe Connector -Y
					}
				}
			}
		}
		
		// Frame below top platform
		{
			// When viewed from the side with the power inputs
			if(bZ == 1){
				// Right
				if(bX == 2){
					switch(bY){
						case 13:{
							main.add(new AABB(0.0625, 0.0, 0.25, 0.3125, 1.0, 0.5625));
							
							main.add(new AABB(0.0625, 0.0, 0.0, 0.3125, 0.25, 1.0));
							main.add(new AABB(0.0, 0.0, 0.25, 1.0, 0.25, 0.5));
							break;
						}
						case 14:{
							main.add(new AABB(0.0625, 0.0, 0.3125, 0.3125, 1.0, 0.625));
							break;
						}
						case 15:{
							main.add(new AABB(0.0625, 0.0, 0.375, 0.3125, 1.0, 0.6875));
							
							main.add(new AABB(0.0625, 0.25, 0.375, 0.3125, 0.5, 1.0));
							main.add(new AABB(0.0625, 0.25, 0.4375, 1.0, 0.5, 0.6875));
							break;
						}
						case 16:{
							main.add(new AABB(0.0625, 0.0, 0.4375, 0.3125, 1.0, 0.75));
							break;
						}
						case 17:{
							main.add(new AABB(0.0625, 0.0, 0.5, 0.3125, 1.0, 0.8125));
							break;
						}
					}
				}
				
				// Middle
				if(bX == 4){
					switch(bY){
						case 13:{
							main.add(new AABB(0.375, 0.0, 0.25, 0.625, 1.0, 0.5625));
							
							main.add(new AABB(0.375, 0.0, 0.0, 0.625, 0.25, 1.0));
							main.add(new AABB(0.0, 0.0, 0.25, 1.0, 0.25, 0.5));
							break;
						}
						case 14:{
							main.add(new AABB(0.375, 0.0, 0.3125, 0.625, 1.0, 0.625));
							break;
						}
						case 15:{
							main.add(new AABB(0.375, 0.0, 0.375, 0.625, 1.0, 0.6875));
							
							main.add(new AABB(0.0, 0.25, 0.4375, 1.0, 0.5, 0.6875));
							main.add(new AABB(0.375, 0.25, 0.5, 0.625, 0.5, 1.0));
							break;
						}
						case 16:{
							main.add(new AABB(0.375, 0.0, 0.4375, 0.625, 1.0, 0.75));
							break;
						}
						case 17:{
							main.add(new AABB(0.375, 0.0, 0.5, 0.625, 1.0, 0.8125));
							break;
						}
					}
				}
				
				// Left
				if(bX == 6){
					switch(bY){
						case 13:{
							main.add(new AABB(0.6875, 0.0, 0.25, 0.9375, 1.0, 0.5625));
							
							main.add(new AABB(0.6875, 0.0, 0.0, 0.9375, 0.25, 1.0));
							main.add(new AABB(0.0, 0.0, 0.25, 1.0, 0.25, 0.5));
							break;
						}
						case 14:{
							main.add(new AABB(0.6875, 0.0, 0.3125, 0.9375, 1.0, 0.625));
							break;
						}
						case 15:{
							main.add(new AABB(0.6875, 0.0, 0.375, 0.9375, 1.0, 0.6875));
							
							main.add(new AABB(0.6875, 0.25, 0.375, 0.9375, 0.5, 1.0));
							main.add(new AABB(0.0, 0.25, 0.4375, 0.75, 0.5, 0.6875));
							break;
						}
						case 16:{
							main.add(new AABB(0.6875, 0.0, 0.4375, 0.9375, 1.0, 0.75));
							break;
						}
						case 17:{
							main.add(new AABB(0.6875, 0.0, 0.5, 0.9375, 1.0, 0.8125));
							break;
						}
					}
				}
			}
			
			// When viewed from the side with the redstone controller
			if(bZ == 3){
				// Left
				if(bX == 2){
					switch(bY){
						case 13:{
							main.add(new AABB(0.0625, 0.0, 0.4375, 0.3125, 1.0, 0.75));
							
							main.add(new AABB(0.0625, 0.0, 0.0, 0.3125, 0.25, 1.0));
							main.add(new AABB(0.0, 0.0, 0.5, 1.0, 0.25, 0.75));
							break;
						}
						case 14:{
							main.add(new AABB(0.0625, 0.0, 0.375, 0.3125, 1.0, 0.6875));
							break;
						}
						case 15:{
							main.add(new AABB(0.0625, 0.0, 0.3125, 0.3125, 1.0, 0.625));
							
							main.add(new AABB(0.0625, 0.25, 0.0, 0.3125, 0.5, 0.5));
							main.add(new AABB(0.0625, 0.25, 0.3125, 1.0, 0.5, 0.5625));
							break;
						}
						case 16:{
							main.add(new AABB(0.0625, 0.0, 0.25, 0.3125, 1.0, 0.5625));
							break;
						}
						case 17:{
							main.add(new AABB(0.0625, 0.0, 0.1875, 0.3125, 1.0, 0.5));
							break;
						}
					}
				}
				
				// Middle
				if(bX == 4){
					switch(bY){
						case 13:{
							main.add(new AABB(0.375, 0.0, 0.4375, 0.625, 1.0, 0.75));
							
							main.add(new AABB(0.375, 0.0, 0.0, 0.625, 0.25, 1.0));
							main.add(new AABB(0.0, 0.0, 0.5, 1.0, 0.25, 0.75));
							break;
						}
						case 14:{
							main.add(new AABB(0.375, 0.0, 0.375, 0.625, 1.0, 0.6875));
							break;
						}
						case 15:{
							main.add(new AABB(0.375, 0.0, 0.3125, 0.625, 1.0, 0.625));
							
							main.add(new AABB(0.0, 0.25, 0.3125, 1.0, 0.5, 0.5625));
							main.add(new AABB(0.375, 0.25, 0.0, 0.625, 0.5, 0.5));
							break;
						}
						case 16:{
							main.add(new AABB(0.375, 0.0, 0.25, 0.625, 1.0, 0.5625));
							break;
						}
						case 17:{
							main.add(new AABB(0.375, 0.0, 0.1875, 0.625, 1.0, 0.5));
							break;
						}
					}
				}
				
				// Right
				if(bX == 6){
					switch(bY){
						case 13:{
							main.add(new AABB(0.6875, 0.0, 0.4375, 0.9375, 1.0, 0.75));
							
							main.add(new AABB(0.6875, 0.0, 0.0, 0.9375, 0.25, 1.0));
							main.add(new AABB(0.0, 0.0, 0.5, 1.0, 0.25, 0.75));
							break;
						}
						case 14:{
							main.add(new AABB(0.6875, 0.0, 0.375, 0.9375, 1.0, 0.6875));
							break;
						}
						case 15:{
							main.add(new AABB(0.6875, 0.0, 0.3125, 0.9375, 1.0, 0.625));
							
							main.add(new AABB(0.6875, 0.25, 0.0, 0.9375, 0.5, 0.5));
							main.add(new AABB(0.0, 0.25, 0.3125, 0.75, 0.5, 0.5625));
							break;
						}
						case 16:{
							main.add(new AABB(0.6875, 0.0, 0.25, 0.9375, 1.0, 0.5625));
							break;
						}
						case 17:{
							main.add(new AABB(0.6875, 0.0, 0.1875, 0.9375, 1.0, 0.5));
							break;
						}
					}
				}
			}
		}
		
		// Use default cube shape if nessesary
		if(main.isEmpty()){
			main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
		}
		return main;
	}
	
	// STATIC CLASSES
	
	public static enum CokingState{
		/** Wait for Input */
		STANDBY,
		
		/** Process materials into the result */
		PROCESSING,
		
		/** Draining residual fluids from processing materials */
		DRAIN_RESIDUE,
		
		/** Filling up the chamber with fluid, with the amount required by the recipe */
		FLOODING,
		
		/** Dumping the result below the chamber outout and voiding the flushing fluids */
		DUMPING;
		
		public int id(){
			return ordinal();
		}
	}
	
	public static class CokingChamber{
		@Nullable
		CokerUnitRecipe recipe = null;
		CokingState state = CokingState.STANDBY;
		FluidTank tank;
		
		/** Total capacity. inputAmount + outputAmount, should not go above this */
		int capacity;
		/** This has a ratio of X:1 to the input amount. (X amount of items always adds 1) */
		int inputAmount = 0;
		/** This has a ratio of 1:1 to the output amount. */
		int outputAmount = 0;
		
		int timer = 0;
		
		public CokingChamber(int itemCapacity, int fluidCapacity){
			this.capacity = itemCapacity;
			this.tank = new FluidTank(fluidCapacity);
		}
		
		public CokingChamber readFromNBT(CompoundTag nbt){
			this.tank.readFromNBT(nbt.getCompound("tank"));
			this.timer = nbt.getInt("timer");
			this.inputAmount = nbt.getInt("input");
			this.outputAmount = nbt.getInt("output");
			this.state = CokingState.values()[nbt.getInt("state")];
			
			if(nbt.contains("recipe", Tag.TAG_STRING)){
				try{
					this.recipe = CokerUnitRecipe.recipes.get(new ResourceLocation(nbt.getString("recipe")));
				}catch(ResourceLocationException e){
					ImmersivePetroleum.log.error("Tried to load a coking recipe with an invalid name", e);
				}
			}else{
				this.recipe = null;
			}
			
			return this;
		}
		
		public CompoundTag writeToNBT(CompoundTag nbt){
			nbt.put("tank", this.tank.writeToNBT(new CompoundTag()));
			nbt.putInt("timer", this.timer);
			nbt.putInt("input", this.inputAmount);
			nbt.putInt("output", this.outputAmount);
			nbt.putInt("state", state.id());
			
			if(this.recipe != null){
				nbt.putString("recipe", this.recipe.getId().toString());
			}
			
			return nbt;
		}
		
		/** Returns true when the recipe has been set, false if it already is set and the chamber is working */
		public boolean setRecipe(@Nullable CokerUnitRecipe recipe){
			if(state == CokingState.STANDBY){
				this.recipe = recipe;
				return true;
			}
			
			return false;
		}
		
		/** Always returns 0 if the recipe hasnt been set yet, otherwise it pretty much does what you'd expect it to */
		public int addStack(@Nonnull ItemStack stack, boolean simulate){
			if(this.recipe != null && !stack.isEmpty() && this.recipe.inputItem.test(stack)){
				int capacity = getCapacity() * recipe.inputItem.getCount();
				int current = getTotalAmount() * recipe.inputItem.getCount();
				
				if(simulate){
					return Math.min(capacity - current, stack.getCount());
				}
				
				int filled = capacity - current;
				if(stack.getCount() < filled){
					filled = stack.getCount();
				}
				this.inputAmount++;
				
				return filled;
			}
			
			return 0;
		}
		
		public CokingState getState(){
			return this.state;
		}
		
		public int getCapacity(){
			return this.capacity;
		}
		
		public int getInputAmount(){
			return this.inputAmount;
		}
		
		public int getOutputAmount(){
			return this.outputAmount;
		}
		
		/** returns the combined I/O Amount */
		public int getTotalAmount(){
			return this.inputAmount + this.outputAmount;
		}
		
		public int getTimer(){
			return this.timer;
		}
		
		@Nullable
		public CokerUnitRecipe getRecipe(){
			return this.recipe;
		}
		
		/** Expected input. (DO NOT EDIT THE RETURNED STACK) */
		public ItemStack getInputItem(){
			if(this.recipe == null){
				return ItemStack.EMPTY;
			}
			return this.recipe.inputItem.getMatchingStacks()[0];
		}
		
		/** Expected output. (DO NOT EDIT THE RETURNED STACK) */
		public ItemStack getOutputItem(){
			if(this.recipe == null){
				return ItemStack.EMPTY;
			}
			
			return this.recipe.outputItem.get();
		}
		
		public FluidTank getTank(){
			return this.tank;
		}
		
		/** returns true when the coker should update, false otherwise */
		public boolean tick(CokerUnitTileEntity cokerunit, int chamberId){
			switch(this.state){
				case STANDBY:{
					if(this.recipe != null){
						this.state = CokingState.PROCESSING;
						return true;
					}
					break;
				}
				case PROCESSING:{
					if(this.inputAmount > 0 && !getInputItem().isEmpty() && (this.tank.getCapacity() - this.tank.getFluidAmount()) >= this.recipe.outputFluid.getAmount()){
						if(cokerunit.energyStorage.getEnergyStored() >= this.recipe.getTotalProcessEnergy()){
							cokerunit.energyStorage.extractEnergy(this.recipe.getTotalProcessEnergy(), false);
							
							this.timer++;
							if(this.timer >= (this.recipe.getTotalProcessTime() * this.recipe.inputItem.getCount())){
								this.timer = 0;
								
								this.tank.fill(Utils.copyFluidStackWithAmount(this.recipe.outputFluid.getMatchingFluidStacks().get(0), this.recipe.outputFluid.getAmount(), false), FluidAction.EXECUTE);
								this.inputAmount--;
								this.outputAmount++;
								
								if(this.inputAmount <= 0){
									this.state = CokingState.DRAIN_RESIDUE;
								}
							}
							
							return true;
						}
					}
					break;
				}
				case DRAIN_RESIDUE:{
					if(this.tank.getFluidAmount() > 0){
						FluidTank buffer = cokerunit.bufferTanks[TANK_OUTPUT];
						FluidStack drained = this.tank.drain(25, FluidAction.SIMULATE);
						
						int accepted = buffer.fill(drained, FluidAction.SIMULATE);
						if(accepted > 0){
							int amount = Math.min(drained.getAmount(), accepted);
							
							this.tank.drain(amount, FluidAction.EXECUTE);
							buffer.fill(Utils.copyFluidStackWithAmount(drained, amount, false), FluidAction.EXECUTE);
							
							return true;
						}
					}else{
						this.state = CokingState.FLOODING;
						return true;
					}
					break;
				}
				case FLOODING:{
					this.timer++;
					if(this.timer >= 2){
						this.timer = 0;
						
						int max = getTotalAmount() * this.recipe.inputFluid.getAmount();
						if(this.tank.getFluidAmount() < max){
							FluidStack accepted = cokerunit.bufferTanks[TANK_INPUT].drain(this.recipe.inputFluid.getAmount(), FluidAction.SIMULATE);
							if(accepted.getAmount() >= this.recipe.inputFluid.getAmount()){
								cokerunit.bufferTanks[TANK_INPUT].drain(this.recipe.inputFluid.getAmount(), FluidAction.EXECUTE);
								this.tank.fill(accepted, FluidAction.EXECUTE);
							}
						}else if(this.tank.getFluidAmount() >= max){
							this.state = CokingState.DUMPING;
							return true;
						}
					}
					break;
				}
				case DUMPING:{
					boolean update = false;
					
					this.timer++;
					if(this.timer >= 5){ // Output speed will always be fixed
						this.timer = 0;
						
						if(this.outputAmount > 0){
							Level world = cokerunit.getLevelNonnull();
							int amount = Math.min(this.outputAmount, 1);
							ItemStack copy = this.recipe.outputItem.get().copy();
							copy.setCount(amount);
							
							// Drop item(s) at the designated chamber output location
							BlockPos itemOutPos = cokerunit.getBlockPosForPos(chamberId == 0 ? Chamber_A_OUT : Chamber_B_OUT);
							Vec3 center = new Vec3(itemOutPos.getX() + 0.5, itemOutPos.getY() - 0.5, itemOutPos.getZ() + 0.5);
							ItemEntity ent = new ItemEntity(cokerunit.getLevelNonnull(), center.x, center.y, center.z, copy);
							ent.setDeltaMovement(0.0, 0.0, 0.0); // Any movement has the potential to end with the stack bouncing all over the place
							world.addFreshEntity(ent);
							this.outputAmount -= amount;
							
							update = true;
						}
					}
					
					// Void washing fluid
					if(this.tank.getFluidAmount() > 0){
						this.tank.drain(25, FluidAction.EXECUTE);
						
						update = true;
					}
					
					if(this.outputAmount <= 0 && this.tank.isEmpty()){
						this.recipe = null;
						this.state = CokingState.STANDBY;
						
						update = true;
					}
					
					if(update){
						return true;
					}
					break;
				}
			}
			
			return false;
		}
	}
}