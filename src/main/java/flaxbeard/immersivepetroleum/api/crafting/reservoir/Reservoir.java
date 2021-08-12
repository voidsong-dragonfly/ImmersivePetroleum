package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;

public class Reservoir extends IESerializableRecipe{
	public static final IRecipeType<Reservoir> TYPE = IRecipeType.register(ImmersivePetroleum.MODID + ":reservoir");
	
	public static Map<ResourceLocation, Reservoir> map = new HashMap<>();
	
	public String name;
	public ResourceLocation fluidLocation;
	
	public int minSize;
	public int maxSize;
	public int residual;
	
	public int weight;
	
	public List<ResourceLocation> dimWhitelist = new ArrayList<>(0);
	public List<ResourceLocation> dimBlacklist = new ArrayList<>(0);
	
	public List<ResourceLocation> bioWhitelist = new ArrayList<>(0);
	public List<ResourceLocation> bioBlacklist = new ArrayList<>(0);
	
	private Fluid fluid;
	
	/**
	 * Creates a new reservoir.
	 * 
	 * @param name The name of this reservoir type
	 * @param id The "recipeId" of this reservoir
	 * @param fluidLocation The registry name of the fluid this reservoir is
	 *        containing
	 * @param minSize Minimum amount of fluid in this reservoir
	 * @param maxSize Maximum amount of fluid in this reservoir
	 * @param residual Leftover fluid amount after depletion
	 * @param weight The weight for this reservoir
	 */
	public Reservoir(String name, ResourceLocation id, ResourceLocation fluidLocation, int minSize, int maxSize, int residual, int weight){
		this(name, id, ForgeRegistries.FLUIDS.getValue(fluidLocation), minSize, maxSize, residual, weight);
	}
	
	/**
	 * Creates a new reservoir.
	 * 
	 * @param name The name of this reservoir type
	 * @param id The "recipeId" of this reservoir
	 * @param fluid The fluid this reservoir is containing
	 * @param minSize Minimum amount of fluid in this reservoir
	 * @param maxSize Maximum amount of fluid in this reservoir
	 * @param residual Leftover fluid amount after depletion
	 * @param weight The weight for this reservoir
	 */
	public Reservoir(String name, ResourceLocation id, Fluid fluid, int minSize, int maxSize, int residual, int weight){
		super(ItemStack.EMPTY, TYPE, id);
		this.name = name;
		this.fluidLocation = fluid.getRegistryName();
		this.fluid = fluid;
		this.residual = residual;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.weight = weight;
	}
	
	public Reservoir(CompoundNBT nbt){
		super(ItemStack.EMPTY, TYPE, new ResourceLocation(nbt.getString("id")));
		
		this.name = nbt.getString("name");
		
		this.fluidLocation = new ResourceLocation(nbt.getString("fluid"));
		this.fluid = ForgeRegistries.FLUIDS.getValue(this.fluidLocation);
		
		this.minSize = nbt.getInt("minSize");
		this.maxSize = nbt.getInt("maxSize");
		this.residual = nbt.getInt("residual");
		
		this.dimWhitelist = toList(nbt.getList("dimensionWhitelist", NBT.TAG_STRING));
		this.dimBlacklist = toList(nbt.getList("dimensionBlacklist", NBT.TAG_STRING));
		
		this.bioWhitelist = toList(nbt.getList("biomeWhitelist", NBT.TAG_STRING));
		this.bioBlacklist = toList(nbt.getList("biomeBlacklist", NBT.TAG_STRING));
	}
	
	@Override
	protected IERecipeSerializer<Reservoir> getIESerializer(){
		return Serializers.RESERVOIR_SERIALIZER.get();
	}
	
	public CompoundNBT writeToNBT(){
		return writeToNBT(new CompoundNBT());
	}
	
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		nbt.putString("name", this.name);
		nbt.putString("id", this.id.toString());
		nbt.putString("fluid", this.fluidLocation.toString());
		
		nbt.putInt("minSize", this.minSize);
		nbt.putInt("maxSize", this.maxSize);
		nbt.putInt("residual", this.residual);
		
		nbt.put("dimensionWhitelist", toNbt(this.dimWhitelist));
		nbt.put("dimensionBlacklist", toNbt(this.dimBlacklist));
		
		nbt.put("biomeWhitelist", toNbt(this.bioWhitelist));
		nbt.put("biomeBlacklist", toNbt(this.bioBlacklist));
		
		return nbt;
	}
	
	public boolean addDimension(boolean blacklist, ResourceLocation... names){
		return addDimension(blacklist, Arrays.asList(names));
	}
	
	public boolean addDimension(boolean blacklist, List<ResourceLocation> names){
		if(blacklist){
			return this.dimBlacklist.addAll(names);
		}else{
			return this.dimWhitelist.addAll(names);
		}
	}
	
	public boolean addBiome(boolean blacklist, ResourceLocation... names){
		return addBiome(blacklist, Arrays.asList(names));
	}
	
	public boolean addBiome(boolean blacklist, List<ResourceLocation> names){
		if(blacklist){
			return this.bioBlacklist.addAll(names);
		}else{
			return this.bioWhitelist.addAll(names);
		}
	}
	
	public boolean isValidDimension(@Nonnull World world){
		if(world == null)
			return false;
		
		return isValidDimension(world.getDimensionKey().getRegistryName());
	}
	
	public boolean isValidDimension(@Nonnull ResourceLocation rl){
		if(this.dimWhitelist.size() > 0){
			return this.dimWhitelist.contains(rl);
			
		}else if(this.dimBlacklist.size() > 0){
			return !this.dimBlacklist.contains(rl);
		}
		
		return true;
	}
	
	public boolean isValidBiome(@Nonnull Biome biome){
		return isValidBiome(biome.getRegistryName());
	}
	
	public boolean isValidBiome(@Nonnull ResourceLocation rl){
		if(this.bioWhitelist.size() > 0){
			return this.bioWhitelist.contains(rl);
			
		}else if(this.bioBlacklist.size() > 0){
			return !this.bioBlacklist.contains(rl);
		}
		
		return true;
	}
	
	@Override
	public ItemStack getRecipeOutput(){
		return ItemStack.EMPTY;
	}
	
	public Fluid getFluid(){
		return this.fluid;
	}
	
	@Override
	public String toString(){
		return this.writeToNBT().toString();
	}
	
	private List<ResourceLocation> toList(ListNBT nbtList){
		List<ResourceLocation> list = new ArrayList<>(0);
		if(nbtList.size() > 0){
			for(INBT tag:nbtList){
				if(tag instanceof StringNBT){
					list.add(new ResourceLocation(((StringNBT) tag).getString()));
				}
			}
		}
		return list;
	}
	
	private ListNBT toNbt(List<ResourceLocation> list){
		ListNBT nbtList = new ListNBT();
		for(ResourceLocation rl:list){
			nbtList.add(StringNBT.valueOf(rl.toString()));
		}
		return nbtList;
	}
}
