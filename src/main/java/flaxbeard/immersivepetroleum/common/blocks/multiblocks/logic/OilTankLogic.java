package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.OilTankShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

// TODO
public class OilTankLogic implements IMultiblockLogic<OilTankLogic.State>, IServerTickableComponent<OilTankLogic.State>, IClientTickableComponent<OilTankLogic.State>{
	
	public enum PortState implements StringRepresentable{
		INPUT, OUTPUT;
		
		@Override
		@Nonnull
		public String getSerializedName(){
			return this.toString().toLowerCase(Locale.ENGLISH);
		}
		
		public Component getText(){
			return Component.translatable("desc.immersivepetroleum.info.oiltank." + getSerializedName());
		}
		
		public PortState next(){
			return this == INPUT ? OUTPUT : INPUT;
		}
	}
	
	public enum Port implements StringRepresentable{
		TOP(new BlockPos(2, 2, 3)),
		BOTTOM(new BlockPos(2, 0, 3)),
		DYNAMIC_A(new BlockPos(0, 1, 2)),
		DYNAMIC_B(new BlockPos(4, 1, 2)),
		DYNAMIC_C(new BlockPos(0, 1, 4)),
		DYNAMIC_D(new BlockPos(4, 1, 4));
		
		public static final Port[] DYNAMIC_PORTS = {DYNAMIC_A, DYNAMIC_B, DYNAMIC_C, DYNAMIC_D};
		
		public final BlockPos posInMultiblock;
		Port(BlockPos posInMultiblock){
			this.posInMultiblock = posInMultiblock;
		}
		
		public boolean matches(BlockPos posInMultiblock){
			return posInMultiblock.equals(this.posInMultiblock);
		}
		
		@Override
		@Nonnull
		public String getSerializedName(){
			return this.toString().toLowerCase(Locale.ENGLISH);
		}
		
		static Set<BlockPos> toSet(Port... ports){
			ImmutableSet.Builder<BlockPos> builder = ImmutableSet.builder();
			for(Port port:ports){
				builder.add(port.posInMultiblock);
			}
			return builder.build();
		}
	}
	
	/**
	 * Template-Location of the Redstone Input Port. (2 2 5 & 2 2 2)<br>
	 */
	public static final BlockPos[] Redstone_IN = new BlockPos[]{new BlockPos(2, 2, 5), new BlockPos(2, 2, 2)};
	
	@Override
	public State createInitialState(IInitialMultiblockContext<OilTankLogic.State> capabilitySource){
		return new OilTankLogic.State(capabilitySource);
	}
	
	@Override
	public void tickClient(IMultiblockContext<OilTankLogic.State> context){
	}
	
	@Override
	public void tickServer(IMultiblockContext<OilTankLogic.State> context){
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return OilTankShape.GETTER;
	}
	
	// TODO
	public static class State implements IMultiblockState{
		public final RSState rsState = RSState.enabledByDefault();
		
		public final FluidTank tank = new FluidTank(1024 * FluidType.BUCKET_VOLUME/*, f -> !f.getFluid().getAttributes().isGaseous()*/);
		public final EnumMap<Port, PortState> portConfig = new EnumMap<>(Port.class);
		public State(IInitialMultiblockContext<State> context){
		}
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
		}
	}
}
