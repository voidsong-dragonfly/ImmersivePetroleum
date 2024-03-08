package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import java.util.function.Function;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.OilTankShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;

// TODO
public class OilTankLogic implements IMultiblockLogic<OilTankLogic.State>, IServerTickableComponent<OilTankLogic.State>, IClientTickableComponent<OilTankLogic.State>{
	
	@Override
	public void tickClient(IMultiblockContext<OilTankLogic.State> context){
	}
	
	@Override
	public void tickServer(IMultiblockContext<OilTankLogic.State> context){
	}
	
	@Override
	public State createInitialState(IInitialMultiblockContext<OilTankLogic.State> capabilitySource){
		return null;
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return OilTankShape.GETTER;
	}
	
	// TODO
	public static class State implements IMultiblockState{
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
		}
	}
}
