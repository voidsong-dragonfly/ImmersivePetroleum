package flaxbeard.immersivepetroleum.api.crafting.pumpjack;

import java.util.HashMap;
import java.util.Map;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirWorldInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

/**
 * @deprecated Will be nuked as part of a complete rewrite.
 */
// TODO Chunk-Based Reservoir: Nuke this once the new system is in place.
public class PumpjackHandler{
	@SuppressWarnings("unused")
	@Deprecated
	private static Map<ResourceLocation, Map<ResourceLocation, Integer>> totalWeightMap = new HashMap<>();
	
	@Deprecated
	public static Map<DimensionChunkCoords, Long> timeCache = new HashMap<>();
	@Deprecated
	public static Map<DimensionChunkCoords, ReservoirWorldInfo> reservoirsCache = new HashMap<>();
	
	/**
	 * Gets amount of fluid in a specific chunk's reservoir in mB
	 *
	 * @param world The world to test
	 * @param chunkX X coordinate of desired chunk
	 * @param chunkZ Z coordinate of desired chunk
	 * @return mB of fluid in the given reservoir
	 */
	public static int getFluidAmount(Level world, int chunkX, int chunkZ){
		return 0;
	}
	
	/**
	 * Gets Fluid type in a specific chunk's reservoir
	 *
	 * @param world The world to test
	 * @param chunkX X coordinate of desired chunk
	 * @param chunkZ Z coordinate of desired chunk
	 * @return Fluid in given reservoir (or null if none)
	 */
	public static Fluid getFluid(Level world, int chunkX, int chunkZ){
		return null;
	}
	
	/**
	 * Gets the mB/tick of fluid that is produced "residually" in the chunk (can
	 * be extracted while empty)
	 *
	 * @param world The world to test
	 * @param chunkX X coordinate of desired chunk
	 * @param chunkZ Z coordinate of desired chunk
	 * @return mB of fluid that can be extracted "residually"
	 */
	public static int getResidualFluid(Level world, int chunkX, int chunkZ){
		return 0;
	}
	
	/**
	 * Gets the OilWorldInfo object associated with the given chunk
	 * 
	 * @param world The world to retrieve
	 * @param chunkX X coordinate of desired chunk
	 * @param chunkZ Z coordinate of desired chunk
	 * @return The OilWorldInfo corresponding w/ given chunk
	 */
	public static ReservoirWorldInfo getOrCreateOilWorldInfo(Level world, int chunkX, int chunkZ){
		return null;
	}
	
	/**
	 * Gets the OilWorldInfo object associated with the given chunk
	 *
	 * @param world The world to retrieve
	 * @param coords Coordinates of desired chunk
	 * @param force Force creation on an empty chunk
	 * @return The OilWorldInfo corresponding w/ given chunk
	 */
	public static ReservoirWorldInfo getOrCreateOilWorldInfo(Level world, DimensionChunkCoords coords, boolean force){
		return null;
	}
	
	/**
	 * Depletes fluid from a given chunk
	 *
	 * @param world World whose chunk to drain
	 * @param chunkX Chunk x
	 * @param chunkZ Chunk z
	 * @param amount Amount of fluid in mB to drain
	 */
	public static void depleteFluid(Level world, int chunkX, int chunkZ, int amount){
	}
	
	/**
	 * Gets the total weight of reservoir types for the given dimension ID and
	 * biome type
	 *
	 * @param dimension The dimension to check
	 * @param biome The biome to check
	 * @return The total weight associated with the dimension/biome pair
	 */
	public static int getTotalWeight(ResourceLocation dimension, ResourceLocation biome){
		return 0;
	}
	
	/**
	 * Adds a reservoir type to the pool of valid reservoirs
	 * 
	 * @param id The "recipeId" of the reservoir type
	 * @param reservoir The reservoir type to add
	 * @return
	 */
	public static Reservoir addReservoir(ResourceLocation id, Reservoir reservoir){
		return null;
	}
	
	public static void recalculateChances(){
	}
}
