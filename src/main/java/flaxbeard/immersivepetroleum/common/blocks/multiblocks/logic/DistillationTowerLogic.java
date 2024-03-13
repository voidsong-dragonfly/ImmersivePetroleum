package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import java.util.function.Function;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.DistillationTowerShape;
import flaxbeard.immersivepetroleum.common.util.inventory.MultiFluidTankFiltered;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

public class DistillationTowerLogic implements IMultiblockLogic<DistillationTowerLogic.State>, IServerTickableComponent<DistillationTowerLogic.State>, IClientTickableComponent<DistillationTowerLogic.State>{
	
	/** Input Tank ID */
	public static final int TANK_INPUT = 0;
	
	/** Output Tank ID */
	public static final int TANK_OUTPUT = 1;
	
	/** Inventory Fluid Input (Filled Bucket) */
	public static final int INV_0 = 0;
	
	/** Inventory Fluid Input (Empty Bucket) */
	public static final int INV_1 = 1;
	
	/** Inventory Fluid Output (Empty Bucket) */
	public static final int INV_2 = 2;
	
	/** Inventory Fluid Output (Filled Bucket) */
	public static final int INV_3 = 3;
	
	/** Template-Location of the Fluid Input Port. (3 0 3) */
	public static final BlockPos Fluid_IN = new BlockPos(3, 0, 3);
	
	/** Template-Location of the Fluid Output Port. (1 0 3) */
	public static final BlockPos Fluid_OUT = new BlockPos(1, 0, 3);
	
	/** Template-Location of the Item Output Port. (0 0 1) */
	public static final BlockPos Item_OUT = new BlockPos(0, 0, 1);
	
	/** Template-Location of the Energy Input Port. (3 1 3) */
	public static final CapabilityPosition ENERGY_IN = new CapabilityPosition(3, 1, 3, RelativeBlockFace.UP);
	
	/** Template-Location of the Redstone Input Port. (0 1 3) */
	public static final BlockPos REDSTONE_IN = new BlockPos(0, 1, 3);
	
	@Override
	public State createInitialState(IInitialMultiblockContext<State> capabilitySource){
		return new DistillationTowerLogic.State(capabilitySource);
	}
	
	@Override
	public void tickClient(IMultiblockContext<DistillationTowerLogic.State> context){
	}
	
	@Override
	public void tickServer(IMultiblockContext<DistillationTowerLogic.State> context){
		final DistillationTowerLogic.State state = context.getState();
		final IMultiblockLevel level = context.getLevel();
		final boolean rsEnabled = state.rsState.isEnabled(context);
		
		if(state.cooldownTicks > 0){
			state.cooldownTicks--;
		}
		
		boolean update = false;
		if(rsEnabled){
			/*// TODO Recipe processing..
			if(state.energy.getEnergyStored() > 0 && this.processQueue.size() < getProcessQueueMaxLength()){
				if(state.tanks.input.getFluidAmount() > 0){
					
				}
			}
			*/
		}
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return DistillationTowerShape.GETTER;
	}
	
	public static class State implements IMultiblockState{
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(16000);
		public final RSState rsState = RSState.enabledByDefault();
		
		public NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
		public final Tanks tanks = new Tanks();
		public int cooldownTicks = 0;
		public boolean wasActive = false;
		
		public State(IInitialMultiblockContext<State> context){
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
			this.tanks.readNBT(nbt.getCompound("tanks"));
			this.cooldownTicks = nbt.getInt("cooldownTicks");
			
			this.inventory = readInventory(nbt.getCompound("inventory"));
		}
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
			nbt.put("tanks", this.tanks.writeNBT());
			nbt.putInt("cooldownTicks", this.cooldownTicks);
			
			nbt.put("inventory", writeInventory(this.inventory));
		}
		
		protected NonNullList<ItemStack> readInventory(CompoundTag nbt){
			NonNullList<ItemStack> list = NonNullList.create();
			ContainerHelper.loadAllItems(nbt, list);
			
			if(list.size() == 0){ // Incase it loaded none
				list = this.inventory.size() == 4 ? this.inventory : NonNullList.withSize(4, ItemStack.EMPTY);
			}else if(list.size() < 4){ // Padding incase it loaded less than 4
				while(list.size() < 4)
					list.add(ItemStack.EMPTY);
			}
			return list;
		}
		
		protected CompoundTag writeInventory(NonNullList<ItemStack> list){
			return ContainerHelper.saveAllItems(new CompoundTag(), list);
		}
	}
	
	public static record Tanks(MultiFluidTankFiltered input, MultiFluidTankFiltered output) implements IReadWriteNBT{
		public static final int CAPACITY = 24 * FluidType.BUCKET_VOLUME;
		
		public Tanks(){
			this(new MultiFluidTankFiltered(CAPACITY, fs -> DistillationTowerRecipe.findRecipe(fs) != null), new MultiFluidTankFiltered(CAPACITY));
		}
		
		@Override
		public void readNBT(CompoundTag nbt){
			this.input.readFromNBT(nbt.getCompound("input"));
			this.output.readFromNBT(nbt.getCompound("output"));
		}
		
		@Override
		public CompoundTag writeNBT(){
			CompoundTag nbt = new CompoundTag();
			nbt.put("input", this.input.writeToNBT(new CompoundTag()));
			nbt.put("output", this.output.writeToNBT(new CompoundTag()));
			return nbt;
		}
	}
}
