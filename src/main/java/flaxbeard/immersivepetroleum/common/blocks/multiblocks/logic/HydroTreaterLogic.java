package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import java.util.function.Function;

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
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.HydroTreaterShape;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.util.IReadWriteNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

// TODO
public class HydroTreaterLogic implements IMultiblockLogic<HydroTreaterLogic.State>, IServerTickableComponent<HydroTreaterLogic.State>, IClientTickableComponent<HydroTreaterLogic.State>{
	/** Primary Fluid Input Tank<br> */
	public static final int TANK_INPUT_A = 0;
	
	/** Secondary Fluid Input Tank<br> */
	public static final int TANK_INPUT_B = 1;
	
	/** Output Fluid Tank<br> */
	public static final int TANK_OUTPUT = 2;
	
	/** Template-Location of the Fluid Input Port. (1 0 3)<br> */
	public static final BlockPos Fluid_IN_A = new BlockPos(1, 0, 3);
	
	/** Template-Location of the Fluid Input Port. (2 2 1)<br> */
	public static final BlockPos Fluid_IN_B = new BlockPos(2, 2, 1);
	
	/** Template-Location of the Fluid Output Port. (0 1 2)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(0, 1, 2);
	
	/** Template-Location of the Item Output Port. (0 0 2)<br> */
	public static final BlockPos Item_OUT = new BlockPos(0, 0, 2);
	
	/** Template-Location of the Energy Input Ports. (2 2 3)<br> */
	public static final MultiblockFace Energy_IN = new MultiblockFace(2, 2, 3, RelativeBlockFace.UP);
	
	/** Template-Location of the Redstone Input Port. (0 1 3)<br> */
	public static final BlockPos Redstone_IN = new BlockPos(0, 1, 3);
	
	@Override
	public State createInitialState(IInitialMultiblockContext<HydroTreaterLogic.State> capabilitySource){
		return new HydroTreaterLogic.State(capabilitySource);
	}
	
	@Override
	public void tickClient(IMultiblockContext<HydroTreaterLogic.State> context){
	}
	
	@Override
	public void tickServer(IMultiblockContext<HydroTreaterLogic.State> context){
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return HydroTreaterShape.GETTER;
	}
	
	// TODO
	public static class State implements IMultiblockState{
		
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(8000);
		public final RSState rsState = RSState.enabledByDefault();
		
		public final Tanks tanks = new Tanks();
		public State(IInitialMultiblockContext<State> context){
		}
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
			nbt.put("tanks", this.tanks.writeNBT());
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
			this.tanks.readNBT(nbt.getCompound("tanks"));
		}
	}
	
	public static record Tanks(FluidTank primary, FluidTank secondary, FluidTank output) implements IReadWriteNBT{
		public Tanks(){
			this(new FluidTank(12000), new FluidTank(12000), new FluidTank(12000));
		}
		
		@Override
		public void readNBT(CompoundTag nbt){
			this.primary.readFromNBT(nbt.getCompound("primary"));
			this.secondary.readFromNBT(nbt.getCompound("secondary"));
			this.output.readFromNBT(nbt.getCompound("output"));
		}
		
		@Override
		public CompoundTag writeNBT(){
			CompoundTag nbt = new CompoundTag();
			nbt.put("primary", this.primary.writeToNBT(new CompoundTag()));
			nbt.put("secondary", this.secondary.writeToNBT(new CompoundTag()));
			nbt.put("output", this.output.writeToNBT(new CompoundTag()));
			return nbt;
		}
	}
}
