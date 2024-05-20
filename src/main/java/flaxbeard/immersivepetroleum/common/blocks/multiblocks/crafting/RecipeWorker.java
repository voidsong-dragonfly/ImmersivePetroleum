package flaxbeard.immersivepetroleum.common.blocks.multiblocks.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.util.IMultiblockRecipeProcessor;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.util.IReadWriteNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.IFluidTank;

public class RecipeWorker<R extends MultiblockRecipe> implements IReadWriteNBT{
	private int maxQueueSize;
	private List<WorkOrder<R>> queue;
	private final Function<ResourceLocation, RecipeHolder<R>> recipeGetter;
	
	public RecipeWorker(int qSize, Function<ResourceLocation, RecipeHolder<R>> recipeGetter){
		this.maxQueueSize = qSize;
		this.queue = new ArrayList<>(qSize);
		this.recipeGetter = recipeGetter;
	}
	
	public <S extends IMultiblockState> boolean addToQueue(IMultiblockContext<S> callerContext, RecipeHolder<R> recipe){
		if(this.queue.size() < this.maxQueueSize){
			return this.queue.add(new WorkOrder<>(recipe));
		}
		
		return false;
	}
	
	public boolean isEmpty(){
		return this.queue.isEmpty();
	}
	
	public int size(){
		return this.queue.size();
	}
	
	public int getMaxQueueSize(){
		return this.maxQueueSize;
	}
	
	public <S extends IMultiblockState & IMultiblockRecipeProcessor> void tick(IMultiblockContext<S> context){
		S mbState = context.getState();
		
		IFluidTank[] tanks = mbState.getInternalTanks();
		int[] outputTanks = mbState.getOutputTanks();
		IEnergyStorage energy = mbState.getPowerSupply();
		
	}
	
	@Override
	public CompoundTag writeNBT(){
		CompoundTag nbt = new CompoundTag();
		
		ListTag queue = new ListTag();
		this.queue.forEach(p -> queue.add(p.writeNBT()));
		nbt.put("queue", queue);
		
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundTag nbt){
		ListTag queue = nbt.getList("queue", Tag.TAG_COMPOUND);
		if(!queue.isEmpty()){
			// queue.forEach(tag -> );
		}
	}
	
	// #################################################################
	
	protected static class WorkOrder<R extends MultiblockRecipe> {
		protected ResourceLocation recipeId;
		protected R recipe;
		protected int progress;
		public WorkOrder(RecipeHolder<R> holder){
			this.recipeId = holder.id();
			this.recipe = holder.value();
		}
		
		private WorkOrder(){
		}
		
		public CompoundTag writeNBT(){
			CompoundTag nbt = new CompoundTag();
			nbt.putString("id", this.recipeId.toString());
			nbt.putInt("progress", this.progress);
			return nbt;
		}
		
		public void readNBT(CompoundTag nbt, Function<ResourceLocation, RecipeHolder<R>> recipeGetter){
			this.recipeId = new ResourceLocation(nbt.getString("id"));
			this.recipe = recipeGetter.apply(this.recipeId).value();
		}
		
		public ResourceLocation getRecipeId(){
			return this.recipeId;
		}
	}
}
