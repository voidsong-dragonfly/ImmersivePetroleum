package flaxbeard.immersivepetroleum.common.util.compat.crafttweaker;

import java.util.ArrayList;
import java.util.List;

import org.openzen.zencode.java.ZenCodeType.Constructor;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;

import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

@ZenRegister
@Name("mods.immersivepetroleum.ReservoirRegistry")
public class ReservoirTweaker{
	
	@Method
	public static boolean remove(String name){
			List<ResourceLocation> test = ReservoirType.map.keySet().stream()
					.filter(loc -> loc.getPath().contains(name)).toList();
		
		if(test.size() > 1){
			//CraftTweakerAPI.logError("§cMultiple results for \"%s\"§r", name);
		}else if(test.size() == 1){
			ResourceLocation id = test.get(0);
			if(ReservoirType.map.containsKey(id)){
				ReservoirType.map.remove(id);
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
		ReservoirType.map.clear();
	}
	
	@ZenRegister
	@Name("mods.immersivepetroleum.ReservoirBuilder")
	public static class ReservoirBuilder{
		
		private boolean isValid = true;
		
		private final IFluidStack iFluidStack;
		private final int minSize;
		private final int maxSize;
		private final int traceAmount;
		private final int weight;
		
		private final List<ResourceLocation> dimWhitelist = new ArrayList<>();
		private final List<ResourceLocation> dimBlacklist = new ArrayList<>();
		private final List<ResourceLocation> bioWhitelist = new ArrayList<>();
		private final List<ResourceLocation> bioBlacklist = new ArrayList<>();
		
		@Constructor
		public ReservoirBuilder(IFluidStack fluid, int minSize, int maxSize, int traceAmount, int weight){
			if(fluid == null){
				//CraftTweakerAPI.logError("§cReservoir fluid can not be null!§r");
				this.isValid = false;
			}
			if(minSize <= 0){
				//CraftTweakerAPI.logError("§cReservoir minSize has to be at least 1mb!§r");
				this.isValid = false;
			}
			if(maxSize < minSize){
				//CraftTweakerAPI.logError("§cReservoir maxSize can not be smaller than minSize!§r");
				this.isValid = false;
			}
			if(weight <= 1){
				//CraftTweakerAPI.logError("§cReservoir weight has to be greater than or equal to 1!§r");
				this.isValid = false;
			}
			
			this.iFluidStack = fluid;
			this.minSize = minSize;
			this.maxSize = maxSize;
			this.traceAmount = traceAmount;
			this.weight = weight;
		}
		
		@Method
		public ReservoirBuilder addDimensions(boolean blacklist, String[] names){
			List<ResourceLocation> list = new ArrayList<>();
			for(String name:names){
				try{
					list.add(new ResourceLocation(name));
				}catch(ResourceLocationException e){
					//CraftTweakerAPI.logError("§caddDimension: %s§r", e.getMessage());
				}
			}
			
			if(blacklist){
				this.dimBlacklist.addAll(list);
			}else{
				this.dimWhitelist.addAll(list);
			}
			
			return this;
		}
		
		@Method
		public ReservoirBuilder addBiomes(boolean blacklist, String[] names){
			List<ResourceLocation> list = new ArrayList<>();
			for(String name:names){
				try{
					list.add(new ResourceLocation(name));
				}catch(ResourceLocationException e){
					//CraftTweakerAPI.logError("§caddBiome: %s§r", e.getMessage());
				}
			}
			
			if(blacklist){
				this.bioBlacklist.addAll(list);
			}else{
				this.bioWhitelist.addAll(list);
			}
			
			return this;
		}
		
		@Method
		public void build(String name){
			if(name.isEmpty()){
				//CraftTweakerAPI.logError("§cReservoir name can not be empty string!§r");
				this.isValid = false;
			}
			
			if(this.isValid){
				ResourceLocation id = ResourceUtils.ct(name);
				
				if(!ReservoirType.map.containsKey(id)){
					ReservoirType reservoir = new ReservoirType(name, id, this.iFluidStack.getFluid(), this.minSize, this.maxSize, this.traceAmount, this.weight);
					
					if(!this.dimWhitelist.isEmpty()){
						reservoir.addDimension(false, this.dimWhitelist);
					}
					if(!this.dimBlacklist.isEmpty()){
						reservoir.addDimension(true, this.dimBlacklist);
					}
					
					if(!this.bioWhitelist.isEmpty()){
						reservoir.addBiome(false, this.bioWhitelist);
					}
					if(!this.bioBlacklist.isEmpty()){
						reservoir.addBiome(true, this.bioBlacklist);
					}
					
					ReservoirHandler.addReservoir(id, reservoir);
				}else{
					//CraftTweakerAPI.logError("§cReservoir %s already exists!§r", name);
				}
			}
		}
	}
}
