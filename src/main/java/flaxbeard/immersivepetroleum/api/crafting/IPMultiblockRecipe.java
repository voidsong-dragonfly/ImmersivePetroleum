package flaxbeard.immersivepetroleum.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;

import java.util.function.DoubleSupplier;

public abstract class IPMultiblockRecipe extends MultiblockRecipe{
	private static final Lazy<ItemStack> DUMMY_OUTPUT = Lazy.of(() -> ItemStack.EMPTY);
	
	private final TimeAndEnergy processingCost;
	
	protected <T extends Recipe<?>> IPMultiblockRecipe(IERecipeTypes.TypeWithClass<T> type, ResourceLocation id, int time, int energy){
		super(DUMMY_OUTPUT, type, id);
		this.processingCost = new TimeAndEnergy(time, energy);
	}
	
	@Deprecated(forRemoval = true)
	protected <T extends Recipe<?>> IPMultiblockRecipe(ItemStack outputDummy, IERecipeTypes.TypeWithClass<T> type, ResourceLocation id){
		super(Lazy.of(() -> outputDummy), type, id);
		this.processingCost = new TimeAndEnergy(1, 1);
	}
	
	@Deprecated(forRemoval = true)
	protected void timeAndEnergy(int time, int energy){
	}
	
	@Override
	public void modifyTimeAndEnergy(DoubleSupplier timeModifier, DoubleSupplier energyModifier){
		this.processingCost.modifyTimeAndEnergy(timeModifier, energyModifier);
	}
	
	@Override
	public int getTotalProcessTime(){
		return this.processingCost.getTime();
	}
	
	@Override
	public int getTotalProcessEnergy(){
		return this.processingCost.getEnergy();
	}
	
	private static class TimeAndEnergy{
		private final int baseTime;
		private final int baseEnergy;
		
		private Lazy<Integer> processingTime;
		private Lazy<Integer> processingEnergy;
		
		public TimeAndEnergy(int baseTime, int baseEnergy){
			this.baseTime = baseTime;
			this.baseEnergy = baseEnergy;
			
			this.processingTime = Lazy.of(() -> this.baseTime);
			this.processingEnergy = Lazy.of(() -> this.baseEnergy);
		}
		
		private void modifyTimeAndEnergy(DoubleSupplier timeModifier, DoubleSupplier energyModifier){
			this.processingTime = Lazy.of(() -> (int) Math.max(1.0D, this.baseTime * timeModifier.getAsDouble()));
			this.processingEnergy = Lazy.of(() -> (int) Math.max(1.0D, this.baseEnergy * energyModifier.getAsDouble()));
		}
		
		public int getTime(){
			return this.processingTime.get();
		}
		
		public int getEnergy(){
			return this.processingEnergy.get();
		}
	}
}
