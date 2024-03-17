package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.util.Utils;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.CokerUnitShape;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.util.IReadWriteNBT;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

// TODO
public class CokerUnitLogic implements IMultiblockLogic<CokerUnitLogic.State>, IServerTickableComponent<CokerUnitLogic.State>, IClientTickableComponent<CokerUnitLogic.State>{
	
	public enum Inventory{
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
	public static final BlockPos Chamber_A_OUT = new BlockPos(2, 0, 2);
	
	/** Template-Location of the Chamber B Item Output */
	public static final BlockPos Chamber_B_OUT = new BlockPos(6, 0, 2);
	
	/** Template-Location of the Fluid Input Port. (2 0 4)<br> */
	public static final BlockPos Fluid_IN = new BlockPos(2, 0, 4);
	
	/** Template-Location of the Fluid Output Port. (5 0 4)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(5, 0, 4);
	
	/** Template-Location of the Item Input Port. (3 0 4)<br> */
	public static final BlockPos Item_IN = new BlockPos(3, 0, 4);
	
	/** Template-Location of the Energy Input Ports.<br><pre>1 1 0<br>2 1 0<br>3 1 0</pre><br> */
	public static final MultiblockFace[] Energy_IN = new MultiblockFace[]{
		new MultiblockFace(6, 1, 4, RelativeBlockFace.FRONT),
		new MultiblockFace(7, 1, 4, RelativeBlockFace.FRONT)
	};
	
	/** Template-Location of the Redstone Input Port. (6 1 4)<br> */
	public static final BlockPos Redstone_IN = new BlockPos(1, 1, 4);
	
	@Override
	public State createInitialState(IInitialMultiblockContext<CokerUnitLogic.State> capabilitySource){
		return new CokerUnitLogic.State(capabilitySource);
	}
	
	@Override
	public void tickClient(IMultiblockContext<CokerUnitLogic.State> context){
	}
	
	@Override
	public void tickServer(IMultiblockContext<CokerUnitLogic.State> context){
		final CokerUnitLogic.State state = context.getState();
		final IMultiblockLevel level = context.getLevel();
		final boolean rsEnabled = state.rsState.isEnabled(context);
		
		boolean update = false;
		if(rsEnabled){
			
		}
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return CokerUnitShape.GETTER;
	}
	
	// TODO
	public static class State implements IMultiblockState{
		
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(24000);
		public final RSState rsState = RSState.enabledByDefault();
		
		public final NonNullList<ItemStack> inventory = NonNullList.withSize(Inventory.values().length, ItemStack.EMPTY);
		public final BufferTanks bufferTanks = new BufferTanks();
		public final Chambers chambers = new Chambers();
		public State(IInitialMultiblockContext<State> context){
		}
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
			nbt.put("buffertanks", this.bufferTanks.writeNBT());
			nbt.put("chambers", this.chambers.writeNBT());
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
			this.bufferTanks.readNBT(nbt.getCompound("buffertanks"));
			this.chambers.readNBT(nbt.getCompound("chambers"));
		}
	}
	
	public static record BufferTanks(FluidTank input, FluidTank output){
		public BufferTanks(){
			this(new FluidTank(16000), new FluidTank(16000));
		}
		
		public void readNBT(CompoundTag nbt){
			this.input.readFromNBT(nbt.getCompound("input"));
			this.output.readFromNBT(nbt.getCompound("output"));
		}
		
		public CompoundTag writeNBT(){
			CompoundTag nbt = new CompoundTag();
			nbt.put("input", this.input.writeToNBT(new CompoundTag()));
			nbt.put("output", this.output.writeToNBT(new CompoundTag()));
			return nbt;
		}
	}
	
	public static record Chambers(CokingChamber primary, CokingChamber secondary) implements IReadWriteNBT{
		public Chambers(){
			this(new CokingChamber(64, 8000), new CokingChamber(64, 8000));
		}
		
		protected void tick(){
			this.primary.tick(null, CHAMBER_A);
			this.secondary.tick(null, CHAMBER_B);
		}
		
		@Override
		public void readNBT(CompoundTag nbt){
			this.primary.readFromNBT(nbt.getCompound("primary"));
			this.secondary.readFromNBT(nbt.getCompound("secondary"));
		}
		
		@Override
		public CompoundTag writeNBT(){
			CompoundTag nbt = new CompoundTag();
			nbt.put("primary", this.primary.writeToNBT(new CompoundTag()));
			nbt.put("secondary", this.secondary.writeToNBT(new CompoundTag()));
			return nbt;
		}
	}
	
	public static enum CokingState{
		/** Wait for Input */
		STANDBY,
		
		/** Process materials into the result */
		PROCESSING,
		
		/** Draining residual fluids from processing materials */
		DRAIN_RESIDUE,
		
		/** Filling up the chamber with fluid, with the amount required by the recipe */
		FLOODING,
		
		/** Dumping the result below the chamber output and voiding the flushing fluids */
		DUMPING;
		
		public int id(){
			return ordinal();
		}
	}
	
	public static class CokingChamber{
		@Nullable
		RecipeHolder<CokerUnitRecipe> recipe = null;
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
			nbt.putInt("state", this.state.id());
			
			if(this.recipe != null){
				nbt.putString("recipe", this.recipe.id().toString());
			}
			
			return nbt;
		}
		
		/** Returns true when the recipe has been set, false if it already is set and the chamber is working */
		public boolean setRecipe(@Nullable RecipeHolder<CokerUnitRecipe> recipe){
			if(state == CokingState.STANDBY){
				this.recipe = recipe;
				return true;
			}
			
			return false;
		}
		
		/** Always returns 0 if the recipe hasnt been set yet, otherwise it pretty much does what you'd expect it to */
		public int addStack(@Nonnull ItemStack stack, boolean simulate){
			if(this.recipe != null && !stack.isEmpty() && this.recipe.value().inputItem.test(stack)){
				int capacity = getCapacity() * recipe.value().inputItem.getCount();
				int current = getTotalAmount() * recipe.value().inputItem.getCount();
				
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
		
		private boolean setStage(CokingState state){
			if(this.state != state){
				this.state = state;
				return true;
			}
			return false;
		}
		
		@Nullable
		public RecipeHolder<CokerUnitRecipe> getRecipe(){
			return this.recipe;
		}
		
		/** Expected input. */
		public ItemStack getInputItem(){
			if(this.recipe == null){
				return ItemStack.EMPTY;
			}
			return this.recipe.value().inputItem.getMatchingStacks()[0];
		}
		
		/** Expected output. */
		public ItemStack getOutputItem(){
			if(this.recipe == null){
				return ItemStack.EMPTY;
			}
			
			return this.recipe.value().outputItem.get().copy();
		}
		
		public FluidTank getTank(){
			return this.tank;
		}
		
		/** returns true when the coker should update, false otherwise */
		public boolean tick(CokerUnitTileEntity cokerunit, int chamberId){
			if(this.recipe == null){
				return setStage(CokingState.STANDBY);
			}
			
			switch(this.state){
				case STANDBY -> {
					if(this.recipe != null){
						return setStage(CokingState.PROCESSING);
					}
				}
				case PROCESSING -> {
					if(this.inputAmount > 0 && !getInputItem().isEmpty() && (this.tank.getCapacity() - this.tank.getFluidAmount()) >= this.recipe.outputFluid.getAmount()){
						if(cokerunit.energyStorage.getEnergyStored() >= this.recipe.value().getTotalProcessEnergy() / this.recipe.value().getTotalProcessTime()){
							cokerunit.energyStorage.extractEnergy(this.recipe.value().getTotalProcessEnergy() / this.recipe.value().getTotalProcessTime(), false);
							
							this.timer++;
							if(this.timer >= (this.recipe.value().getTotalProcessTime() * this.recipe.value().inputItem.getCount())){
								this.timer = 0;
								
								this.tank.fill(Utils.copyFluidStackWithAmount(this.recipe.value().outputFluid, this.recipe.value().outputFluid.getAmount(), false), FluidAction.EXECUTE);
								this.inputAmount--;
								this.outputAmount++;
								
								if(this.inputAmount <= 0){
									setStage(CokingState.DRAIN_RESIDUE);
								}
							}
							
							return true;
						}
					}
				}
				case DRAIN_RESIDUE -> {
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
						return setStage(CokingState.FLOODING);
					}
				}
				case FLOODING -> {
					this.timer++;
					if(this.timer >= 2){
						this.timer = 0;
						
						int max = getTotalAmount() * this.recipe.value().inputFluid.getAmount();
						if(this.tank.getFluidAmount() < max){
							FluidStack accepted = cokerunit.bufferTanks[TANK_INPUT].drain(this.recipe.value().inputFluid.getAmount(), FluidAction.SIMULATE);
							if(accepted.getAmount() >= this.recipe.value().inputFluid.getAmount()){
								cokerunit.bufferTanks[TANK_INPUT].drain(this.recipe.value().inputFluid.getAmount(), FluidAction.EXECUTE);
								this.tank.fill(accepted, FluidAction.EXECUTE);
							}
						}else if(this.tank.getFluidAmount() >= max){
							return setStage(CokingState.DUMPING);
						}
					}
				}
				case DUMPING -> {
					boolean update = false;
					
					this.timer++;
					if(this.timer >= 5){ // Output speed will always be fixed
						this.timer = 0;
						
						if(this.outputAmount > 0){
							Level world = cokerunit.getLevelNonnull();
							int amount = Math.min(this.outputAmount, 1);
							ItemStack copy = this.recipe.value().outputItem.get().copy();
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
						setStage(CokingState.STANDBY);
						
						update = true;
					}
					
					if(update){
						return true;
					}
				}
			}
			
			return false;
		}
	}
}
