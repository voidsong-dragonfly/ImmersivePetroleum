package flaxbeard.immersivepetroleum.common.data.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public class ReservoirBuilder extends IPRecipeBuilder<ReservoirBuilder>{
	private String name;
	private Fluid fluid;
	private int fluidMinimum;
	private int fluidMaximum;
	private int fluidResidual;
	private int equilibrium;
	private int weight;
	
	private boolean isDimBlacklist = false;
	private final List<ResourceLocation> dimensions = new ArrayList<>();
	
	private boolean isBioBlacklist = false;
	private final List<ResourceLocation> biomes = new ArrayList<>();

	private ReservoirBuilder() { }

	public static ReservoirBuilder builder() {
		return new ReservoirBuilder();
	}

	public ReservoirBuilder setName(String name){
		this.name = name;
		return this;
	}

	/**
	 * Sets the fluid for this Reservoir.
	 * 
	 * @param fluid The fluid to set.
	 * @return {@link ReservoirBuilder}
	 */
	public ReservoirBuilder setFluid(Fluid fluid){
		this.fluid = fluid;
		return this;
	}
	
	/**
	 * Sets minimum <code>amount</code> of fluid for this Reservoir. <code><pre>
	 * 1.000 = 1 Bucket
	 * 0.001 = 1 Millibucket
	 * </pre></code>
	 * 
	 * @param amount The amount to set.
	 * @return {@link ReservoirBuilder}
	 */
	public ReservoirBuilder min(double amount){
		this.fluidMinimum = (int) Math.floor(amount * 1000D);
		return this;
	}
	
	/**
	 * Sets maximum/capacity <code>amount</code> of fluid for this Reservoir. <code><pre>
	 * 1.000 = 1 Bucket
	 * 0.001 = 1 Millibucket
	 * </pre></code>
	 * 
	 * @param amount The amount to set.
	 * @return {@link ReservoirBuilder}
	 */
	public ReservoirBuilder max(double amount){
		this.fluidMaximum = (int) Math.floor(amount * 1000D);
		return this;
	}
	
	/**
	 * Replenish <code>amount</code> per tick. <code><pre>
	 * 1.000 = 1 Bucket
	 * 0.001 = 1 Millibucket
	 * </pre></code>
	 * 
	 * @param amount The amount to set.
	 * @return {@link ReservoirBuilder}
	 */
	public ReservoirBuilder residual(double amount){
		this.fluidResidual = (int) Math.floor(amount * 1000D);
		return this;
	}
	
	/**
	 * Reservoir Weight
	 * 
	 * @param weight the weight to provide the reservoir
	 * @return {@link ReservoirBuilder}
	 */
	public ReservoirBuilder weight(int weight){
		this.weight = weight;
		return this;
	}
	
	/**
	 * Sets maximum fluid <code>amount</code> for trace fluid to regenerate. <code><pre>
	 * 1.000 = 1 Bucket
	 * 0.001 = 1 Millibucket
	 * </pre></code>
	 *
	 * @param amount The amount to set.
	 * @return {@link ReservoirBuilder}
	 */
	public ReservoirBuilder equilibrium(double amount){
		this.equilibrium = (int) Math.floor(amount * 1000D);
		return this;
	}
	
	/**
	 * <i>This may only be called once.</i><br>
	 * <br>
	 * Dimension check for this Reservior.
	 * 
	 * @param isBlacklist Marks this as a blacklist when true. Whilelist otherwise.
	 * @param dimensions  Dimensions to blacklist/whitelist
	 * @return {@link ReservoirBuilder}
	 * @throws IllegalArgumentException when it has already been set
	 */
	public ReservoirBuilder setDimensions(boolean isBlacklist, @Nonnull ResourceLocation[] dimensions){
		if(!this.dimensions.isEmpty()){
			throw new IllegalArgumentException("Dimensions list already set.");
		}
		Objects.requireNonNull(dimensions);
		
		this.isDimBlacklist = isBlacklist;
		for(ResourceLocation rl:dimensions){
			if(rl != null && !this.dimensions.contains(rl)){
				this.dimensions.add(rl);
			}
		}
		
		return this;
	}
	
	/**
	 * <i>This may only be called once.</i><br>
	 * <br>
	 * Biome check for this Reservior.
	 * 
	 * @param isBlacklist Marks this as a blacklist when true. Whilelist otherwise.
	 * @param biomes      Biomes to blacklist/whitelist
	 * @return {@link ReservoirBuilder}
	 * @throws IllegalArgumentException when it has already been set
	 */
	public ReservoirBuilder setBiomes(boolean isBlacklist, @Nonnull ResourceLocation[] biomes){
		if(!this.biomes.isEmpty()){
			throw new IllegalArgumentException("Biomes list already set.");
		}
		Objects.requireNonNull(biomes);
		
		this.isBioBlacklist = isBlacklist;
		for(ResourceLocation rl:biomes){
			if(rl != null && !this.biomes.contains(rl)){
				this.biomes.add(rl);
			}
		}
		
		return this;
	}

	public void build(RecipeOutput out, ResourceLocation loc)
	{
		ReservoirType recipe = new ReservoirType(name, fluid, fluidMinimum, fluidMaximum, fluidResidual, equilibrium, weight);
		recipe.setBiomes(isBioBlacklist, biomes);
		recipe.setDimensions(isDimBlacklist, dimensions);
		out.accept(loc, recipe, null, getConditions());
	}
}
