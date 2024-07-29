package flaxbeard.immersivepetroleum.api.reservoir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import flaxbeard.immersivepetroleum.api.crafting.IPRecipeTypes;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import flaxbeard.immersivepetroleum.common.util.RegistryUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.jarjar.nio.util.Lazy;

public class ReservoirType extends IESerializableRecipe{
	static final Lazy<ItemStack> EMPTY_LAZY = Lazy.of(() -> ItemStack.EMPTY);
	
	public static Map<ResourceLocation, RecipeHolder<ReservoirType>> map = new HashMap<>();
	
	public final String name;
	public final ResourceLocation fluidLocation;
	public final int weight;
	
	public final int minSize;
	public final int maxSize;
	public final int residual;
	public final int equilibrium;
	
	private final Fluid fluid;
	
	private BWList biomes = new BWList(false);
	private BWList dimensions = new BWList(false);
	
	/**
	 * Creates a new reservoir.
	 *
	 * @param name          The name of this reservoir type
	 * @param fluidLocation The registry name of the fluid this reservoir is containing
	 * @param minSize       Minimum amount of fluid in this reservoir
	 * @param maxSize       Maximum amount of fluid in this reservoir
	 * @param residual      Leftover fluid amount after depletion
	 * @param equilibrium   Maximum amount of fluid that residuals regenerate at
	 * @param weight        The weight for this reservoir
	 */
	public ReservoirType(String name, ResourceLocation fluidLocation, int minSize, int maxSize, int residual, int equilibrium, int weight){
		this(name, BuiltInRegistries.FLUID.get(fluidLocation), minSize, maxSize, residual, equilibrium, weight);
	}
	
	/**
	 * Creates a new reservoir.
	 * 
	 * @param name     The name of this reservoir type
	 * @param fluid    The fluid this reservoir is containing
	 * @param minSize  Minimum amount of fluid in this reservoir
	 * @param maxSize  Maximum amount of fluid in this reservoir
	 * @param residual      Leftover fluid amount after depletion
	 * @param equilibrium   Maximum amount of fluid that residuals regenerate at
	 * @param weight   The weight for this reservoir
	 */
	public ReservoirType(String name, Fluid fluid, int minSize, int maxSize, int residual, int equilibrium, int weight){
		super(new TagOutput(ItemStack.EMPTY), IPRecipeTypes.RESERVOIR);
		this.name = name;
		this.fluidLocation = RegistryUtils.getRegistryNameOf(fluid);
		this.fluid = fluid;
		this.residual = residual;
		this.equilibrium = equilibrium;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.weight = weight;
	}
	
	public ReservoirType(CompoundTag nbt){
		super(new TagOutput(ItemStack.EMPTY), IPRecipeTypes.RESERVOIR);
		
		this.name = nbt.getString("name");
		
		this.fluidLocation = new ResourceLocation(nbt.getString("fluid"));
		this.fluid = BuiltInRegistries.FLUID.get(this.fluidLocation);
		
		this.minSize = nbt.getInt("minSize");
		this.maxSize = nbt.getInt("maxSize");
		this.residual = nbt.getInt("residual");
		this.equilibrium = nbt.getInt("equilibrium");
		
		this.biomes = new BWList(nbt.getCompound("biomes"));
		this.dimensions = new BWList(nbt.getCompound("dimensions"));
		
		this.weight = nbt.getInt("weight");
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
		nbt.putString("fluid", this.fluidLocation.toString());
		
		nbt.putInt("minSize", this.minSize);
		nbt.putInt("maxSize", this.maxSize);
		nbt.putInt("residual", this.residual);
		nbt.putInt("equilibrium", this.equilibrium);
		
		nbt.put("biomes", this.biomes.toNbt());
		nbt.put("dimensions", this.dimensions.toNbt());
		
		nbt.putInt("weight", this.weight);
		
		return nbt;
	}
	
	public ReservoirType setBiomes(boolean blacklist, ResourceLocation... names){
		return setBiomes(blacklist, Arrays.asList(names));
	}
	
	public ReservoirType setBiomes(boolean blacklist, List<ResourceLocation> names){
		return setBiomes(new BWList(new HashSet<>(names), blacklist));
	}
	
	public ReservoirType setBiomes(@Nonnull BWList biomeList){
		this.biomes = biomeList;
		return this;
	}
	
	public ReservoirType setDimensions(boolean blacklist, ResourceLocation... names){
		return setDimensions(blacklist, Arrays.asList(names));
	}
	
	public ReservoirType setDimensions(boolean blacklist, List<ResourceLocation> names){
		return setDimensions(new BWList(new HashSet<>(names), blacklist));
	}
	
	public ReservoirType setDimensions(@Nonnull BWList dimensionList){
		this.dimensions = dimensionList;
		return this;
	}
	
	public Set<ResourceLocation> getBiomeList(){
		return this.biomes.getSet();
	}
	
	public Set<ResourceLocation> getDimensionList(){
		return this.dimensions.getSet();
	}
	
	public BWList getDimensions(){
		return this.dimensions;
	}
	
	public BWList getBiomes(){
		return this.biomes;
	}
	
	@Override
	@Nonnull
	public ItemStack getResultItem(RegistryAccess pRegistryAccess){
		return ItemStack.EMPTY;
	}
	
	public Fluid getFluid(){
		return this.fluid;
	}
	
	@Override
	public String toString(){
		return this.writeToNBT().toString();
	}
	
	static Set<ResourceLocation> toSet(ListTag nbtList){
		Set<ResourceLocation> set = new HashSet<>();
		if(nbtList.size() > 0){
			nbtList.forEach(tag -> {
				if(tag instanceof StringTag){
					set.add(new ResourceLocation(tag.getAsString()));
				}
			});
		}
		return set;
	}
	
	static ListTag toNbt(Set<ResourceLocation> set){
		ListTag nbtList = new ListTag();
		if(set.size() > 0){
			set.forEach(rl -> nbtList.add(StringTag.valueOf(rl.toString())));
		}
		return nbtList;
	}
	
	/**
	 * Simple Black/White-List.
	 * 
	 * @author TwistedGate
	 */
	public static class BWList{
		
		public static final Codec<BWList> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			Codec.BOOL.fieldOf("isBlacklist").forGetter(l -> l.isBlacklist()),
			ResourceLocation.CODEC.listOf().fieldOf("list").xmap(HashSet::new, ArrayList::new).forGetter(l -> (HashSet<ResourceLocation>) l.getSet())
		).apply(inst, (isBlacklist, list) -> {
			return new BWList(list, isBlacklist);
		}));
		
		private Set<ResourceLocation> set;
		private boolean isBlacklist;
		public BWList(boolean isBlacklist){
			this(new HashSet<>(), isBlacklist);
		}
		
		public BWList(Set<ResourceLocation> set, boolean isBlacklist){
			this.set = set;
			this.isBlacklist = isBlacklist;
		}
		
		public BWList(CompoundTag tag){
			this.isBlacklist = tag.getBoolean("isBlacklist");
			
			if(tag.contains("list", Tag.TAG_LIST)){
				ListTag list = tag.getList("list", Tag.TAG_STRING);
				
				Set<ResourceLocation> set = new HashSet<>();
				if(list.size() > 0){
					list.forEach(t -> {
						if(t instanceof StringTag){
							set.add(new ResourceLocation(t.getAsString()));
						}
					});
				}
				this.set = set;
			}else{
				this.set = new HashSet<>();
			}
		}
		
		public boolean isBlacklist(){
			return this.isBlacklist;
		}
		
		public boolean add(ResourceLocation rl){
			return this.set.add(rl);
		}
		
		public boolean addAll(Collection<? extends ResourceLocation> c){
			return this.set.addAll(c);
		}
		
		public boolean hasEntries(){
			return this.set.size() > 0;
		}
		
		public boolean valid(ResourceLocation rl){
			if(this.set.isEmpty()){
				// An empty set is considered to be "allow anywhere". Regardless of "isBlacklist" value.
				return true;
			}
			
			boolean contains = this.set.contains(rl);
			return this.isBlacklist ^ contains;
		}
		
		public Set<ResourceLocation> getSet(){
			return Collections.unmodifiableSet(this.set);
		}
		
		public void forEach(Consumer<ResourceLocation> action){
			this.set.forEach(action);
		}
		
		public CompoundTag toNbt(){
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("isBlacklist", this.isBlacklist);
			tag.put("list", toNbtList());
			return tag;
		}
		
		private ListTag toNbtList(){
			ListTag nbtList = new ListTag();
			if(this.set.size() > 0){
				this.set.forEach(rl -> nbtList.add(StringTag.valueOf(rl.toString())));
			}
			return nbtList;
		}
	}
}
