package flaxbeard.immersivepetroleum.common.util.compat.crafttweaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openzen.zencode.java.ZenCodeType.Constructor;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker.api.util.Many;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Using a builder is stupid..
 * @author TwistedGate
 */
//@ZenRegister
@Name("mods.immersivepetroleum.DistillationTower")
@Deprecated(forRemoval = true)
public class DistillationRecipeTweaker{
	
	@Method
	public static boolean remove(String name){
		List<ResourceLocation> test = DistillationRecipe.recipes.keySet().stream()
				.filter(loc -> loc.getPath().contains(name))
				.collect(Collectors.toList());
		
		if(test.size() > 1){
			//CraftTweakerAPI.logError("§cMultiple results for \"%s\"§r", name);
		}else if(test.size() == 1){
			ResourceLocation id = test.get(0);
			if(DistillationRecipe.recipes.containsKey(id)){
				DistillationRecipe.recipes.remove(id);
				return true;
			}else{
				//CraftTweakerAPI.logError("§c%s does not exist, or was already removed.§r", id);
			}
		}else{
			//CraftTweakerAPI.logInfo("\"%s\" does not exist or could not be found.", name);
		}
		
		return false;
	}
	
	@Method
	public static void removeAll(){
		DistillationRecipe.recipes.clear();
	}
	
	//@ZenRegister
	@Name("mods.immersivepetroleum.DistillationBuilder")
	@Deprecated(forRemoval = true)
	public static class DistillationRecipeBuilder{
		
		private boolean isValid = true;
		
		private List<Tuple<ItemStack, Double>> byproducts = new ArrayList<>();
		private List<FluidStack> fluidOutputs = new ArrayList<>();
		private FluidTagInput inputFluidTag = null;
		private int fluxEnergy = 2048;
		private int timeTicks = 1;
		
		@Constructor
		public DistillationRecipeBuilder(){
			boolean yes = true;
			if(yes){
				throw new IllegalArgumentException("Test throw.");
			}
		}
		
		@Method
		public DistillationRecipeBuilder setOutputFluids(IFluidStack[] fluidsOutput){
			if(fluidsOutput == null || fluidsOutput.length == 0){
				//CraftTweakerAPI.logError("§cDistillationBuilder output fluids can not be null!§r");
				this.isValid = false;
			}else{
				this.fluidOutputs = Arrays.asList(fluidsOutput).stream().map(f -> f.getInternal()).collect(Collectors.toList());
			}
			return this;
		}
		
		@Method
		public DistillationRecipeBuilder setInputFluid(Many<KnownTag<Fluid>> tag){
			if(tag == null){
				//CraftTweakerAPI.logError("§cDistillationBuilder expected fluidtag as input fluid!§r");
				this.isValid = false;
			}else if(tag.getAmount() < 1){
				//CraftTweakerAPI.logError("§cDistillationBuilder fluidtag amount must atleast be 1mB!§r");
				this.isValid = false;
			}else{
				this.inputFluidTag = new FluidTagInput(tag.getData().getTagKey(), tag.getAmount());
//				this.inputFluidAmount = amount;
			}
			return this;
		}
		
		@Method
		public DistillationRecipeBuilder addByproduct(IItemStack item, int chance){
			return addByproduct(item, chance / 100D);
		}
		
		@Method
		public DistillationRecipeBuilder addByproduct(IItemStack item, double chance){
			if(item == null){
				//CraftTweakerAPI.logError("§cByproduct item can not be null!§r");
				this.isValid = false;
			}else{
				// Clamping between 0.0 - 1.0
				chance = Math.max(Math.min(chance, 1.0), 0.0);
				
				this.byproducts.add(new Tuple<ItemStack, Double>(item.getInternal(), chance));
			}
			return this;
		}
		
		@Method
		public DistillationRecipeBuilder setEnergyAndTime(int flux, int ticks){
			setEnergy(flux);
			setTime(ticks);
			return this;
		}
		
		@Method
		public DistillationRecipeBuilder setEnergy(int flux){
			if(flux < 1){
				//CraftTweakerAPI.logError("§cEnergy usage must be atleast 1 flux/tick!§r");
				this.isValid = false;
			}else{
				this.fluxEnergy = flux;
			}
			return this;
		}
		
		@Method
		public DistillationRecipeBuilder setTime(int ticks){
			if(ticks < 1){
				//CraftTweakerAPI.logError("§cProcessing time must be atleast 1 tick!§r");
				this.isValid = false;
			}else{
				this.timeTicks = ticks;
			}
			return this;
		}
		
		@Method
		public void build(String name){
			if(name.isEmpty()){
				//CraftTweakerAPI.logError("§cDistillation name can not be empty string!§r");
				this.isValid = false;
				throw new IllegalArgumentException("Distillation recipe name can not be empty string!");
			}
			
			if(this.inputFluidTag == null){
				//CraftTweakerAPI.logError("§cOutput fluid tag should not be null!§r");
				this.isValid = false;
			}
			
			if(this.isValid){
				ItemStack[] outStacks = new ItemStack[this.byproducts.size()];
				double[] chances = new double[this.byproducts.size()];
				if(!this.byproducts.isEmpty()){
					for(int i = 0;i < this.byproducts.size();i++){
						outStacks[i] = this.byproducts.get(i).getA();
						chances[i] = this.byproducts.get(i).getB().doubleValue();
					}
				}
				
				FluidStack[] fluidOutStacks = new FluidStack[0];
				if(!this.fluidOutputs.isEmpty()){
					fluidOutStacks = this.fluidOutputs.toArray(new FluidStack[0]);
				}
				
				ResourceLocation id = ResourceUtils.ct("distillationtower/" + name);
				
				DistillationRecipe recipe = new DistillationRecipe(id, fluidOutStacks, outStacks, this.inputFluidTag, this.fluxEnergy, this.timeTicks, chances);
				DistillationRecipe.recipes.put(id, recipe);
			}
		}
	}
}
