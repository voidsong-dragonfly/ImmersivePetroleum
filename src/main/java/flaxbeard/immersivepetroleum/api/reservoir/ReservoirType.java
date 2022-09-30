package flaxbeard.immersivepetroleum.api.reservoir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import flaxbeard.immersivepetroleum.api.crafting.IPRecipeTypes;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

public class ReservoirType extends IESerializableRecipe{
	static final Lazy<ItemStack> EMPTY_LAZY = Lazy.of(() -> ItemStack.EMPTY);
	
	public static Map<ResourceLocation, ReservoirType> map = new HashMap<>();
	
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
	
	private final Fluid fluid;
	
	/**
	 * Creates a new reservoir.
	 * 
	 * @param name          The name of this reservoir type
	 * @param id            The "recipeId" of this reservoir
	 * @param fluidLocation The registry name of the fluid this reservoir is containing
	 * @param minSize       Minimum amount of fluid in this reservoir
	 * @param maxSize       Maximum amount of fluid in this reservoir
	 * @param residual      Leftover fluid amount after depletion
	 * @param weight        The weight for this reservoir
	 */
	public ReservoirType(String name, ResourceLocation id, ResourceLocation fluidLocation, int minSize, int maxSize, int residual, int weight){
		this(name, id, ForgeRegistries.FLUIDS.getValue(fluidLocation), minSize, maxSize, residual, weight);
	}
	
	/**
	 * Creates a new reservoir.
	 * 
	 * @param name     The name of this reservoir type
	 * @param id       The "recipeId" of this reservoir
	 * @param fluid    The fluid this reservoir is containing
	 * @param minSize  Minimum amount of fluid in this reservoir
	 * @param maxSize  Maximum amount of fluid in this reservoir
	 * @param residual Leftover fluid amount after depletion
	 * @param weight   The weight for this reservoir
	 */
	public ReservoirType(String name, ResourceLocation id, Fluid fluid, int minSize, int maxSize, int residual, int weight){
		super(EMPTY_LAZY, IPRecipeTypes.RESERVOIR.get(), id);
		this.name = name;
		this.fluidLocation = fluid.getRegistryName();
		this.fluid = fluid;
		this.residual = residual;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.weight = weight;
	}
	
	public ReservoirType(CompoundTag nbt){
		super(EMPTY_LAZY, IPRecipeTypes.RESERVOIR.get(), new ResourceLocation(nbt.getString("id")));
		
		this.name = nbt.getString("name");
		
		this.fluidLocation = new ResourceLocation(nbt.getString("fluid"));
		this.fluid = ForgeRegistries.FLUIDS.getValue(this.fluidLocation);
		
		this.minSize = nbt.getInt("minSize");
		this.maxSize = nbt.getInt("maxSize");
		this.residual = nbt.getInt("residual");
		
		this.dimWhitelist = toList(nbt.getList("dimensionWhitelist", Tag.TAG_STRING));
		this.dimBlacklist = toList(nbt.getList("dimensionBlacklist", Tag.TAG_STRING));
		
		this.bioWhitelist = toList(nbt.getList("biomeWhitelist", Tag.TAG_STRING));
		this.bioBlacklist = toList(nbt.getList("biomeBlacklist", Tag.TAG_STRING));
	}
	
	@Override
	protected IERecipeSerializer<ReservoirType> getIESerializer(){
		return Serializers.RESERVOIR_SERIALIZER.get();
	}
	
	public CompoundTag writeToNBT(){
		return writeToNBT(new CompoundTag());
	}
	
	public CompoundTag writeToNBT(CompoundTag nbt){
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
	
	public boolean isValidDimension(@Nonnull Level level){
		return isValidDimension(level.dimension().location());
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
	@Nonnull
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}
	
	public Fluid getFluid(){
		return this.fluid;
	}
	
	@Override
	public String toString(){
		return this.writeToNBT().toString();
	}
	
	private List<ResourceLocation> toList(ListTag nbtList){
		List<ResourceLocation> list = new ArrayList<>(0);
		if(nbtList.size() > 0){
			for(Tag tag:nbtList){
				if(tag instanceof StringTag){
					list.add(new ResourceLocation(tag.getAsString()));
				}
			}
		}
		return list;
	}
	
	private ListTag toNbt(List<ResourceLocation> list){
		ListTag nbtList = new ListTag();
		for(ResourceLocation rl:list){
			nbtList.add(StringTag.valueOf(rl.toString()));
		}
		return nbtList;
	}
}
