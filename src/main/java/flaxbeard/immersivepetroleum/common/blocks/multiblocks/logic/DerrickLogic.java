package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import java.util.function.Function;

import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.DerrickShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

// TODO
public class DerrickLogic implements IMultiblockLogic<DerrickLogic.State>, IServerTickableComponent<DerrickLogic.State>, IClientTickableComponent<DerrickLogic.State>{
	public static final int REQUIRED_WATER_AMOUNT = 125;
	public static final int REQUIRED_CONCRETE_AMOUNT = 125;
	
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
	public static final MultiblockFace Energy_IN = new MultiblockFace(2, 1, 0, RelativeBlockFace.UP);
	
	/** Template-Location of the Redstone Input Port. (0 1 1)<br> */
	public static final BlockPos Redstone_IN = new BlockPos(0, 1, 1);
	
	@Override
	public State createInitialState(IInitialMultiblockContext<DerrickLogic.State> capabilitySource){
		return new DerrickLogic.State(capabilitySource);
	}
	
	@Override
	public void tickClient(IMultiblockContext<DerrickLogic.State> context){
	}
	
	@Override
	public void tickServer(IMultiblockContext<DerrickLogic.State> context){
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return DerrickShape.GETTER;
	}
	
	// TODO
	public static class State implements IMultiblockState{
		
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(16000);
		public final RSState rsState = RSState.enabledByDefault();
		
		public int timer = 0;
		public int rotation = 0;
		public boolean drilling;
		public boolean spilling;
		public final FluidTank tank = new FluidTank(8000, this::acceptsFluid);
		public final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
		
		/** Stores the current derrick configuration. */
		@Nullable
		public PipeConfig.Grid gridStorage;
		
		private Fluid fluidSpilled = Fluids.EMPTY;
		private int clientFlow;
		
		public State(IInitialMultiblockContext<State> context){
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
			this.tank.readFromNBT(nbt.getCompound("tank"));
			
			ContainerHelper.loadAllItems(nbt, this.inventory);
		}
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
			nbt.put("tank", this.tank.writeToNBT(new CompoundTag()));
			
			ContainerHelper.saveAllItems(nbt, this.inventory);
		}
		
		// Only accept as much Concrete and Water as needed
		private boolean acceptsFluid(FluidStack fs){
			// TODO Copy over from DerrickTileEntity.acceptsFluid(FluidStack)
			return false;
		}
	}
}
