package flaxbeard.immersivepetroleum.api.crafting.pumpjack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirWorldInfo;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;

/**
 * @deprecated Will be nuked as part of a complete rewrite.
 */
// TODO Chunk-Based Reservoir: Nuke this once the new system is in place.
public class PumpjackHandler{
	private static Map<ResourceLocation, Map<ResourceLocation, Integer>> totalWeightMap = new HashMap<>();
	
	public static Map<DimensionChunkCoords, Long> timeCache = new HashMap<>();
	public static Map<DimensionChunkCoords, ReservoirWorldInfo> reservoirsCache = new HashMap<>();
	
	private static int depositSize = 1;
	
	/**
	 * Gets amount of fluid in a specific chunk's reservoir in mB
	 *
	 * @param world The world to test
	 * @param chunkX X coordinate of desired chunk
	 * @param chunkZ Z coordinate of desired chunk
	 * @return mB of fluid in the given reservoir
	 */
	public static int getFluidAmount(World world, int chunkX, int chunkZ){
		if(world.isRemote)
			return 0;
		
		ReservoirWorldInfo info = getOrCreateOilWorldInfo(world, chunkX, chunkZ);
		if(info == null || (info.capacity == 0) || info.getType() == null || info.getType().fluidLocation == null || (info.current == 0 && info.getType().residual == 0))
			return 0;
		
		return info.current;
	}
	
	/**
	 * Gets Fluid type in a specific chunk's reservoir
	 *
	 * @param world The world to test
	 * @param chunkX X coordinate of desired chunk
	 * @param chunkZ Z coordinate of desired chunk
	 * @return Fluid in given reservoir (or null if none)
	 */
	public static Fluid getFluid(World world, int chunkX, int chunkZ){
		if(world.isRemote)
			return null;
		
		ReservoirWorldInfo info = getOrCreateOilWorldInfo(world, chunkX, chunkZ);
		
		if(info == null || info.getType() == null){
			return null;
		}else{
			return info.getType().getFluid();
		}
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
	public static int getResidualFluid(World world, int chunkX, int chunkZ){
		ReservoirWorldInfo info = getOrCreateOilWorldInfo(world, chunkX, chunkZ);
		
		if(info == null || info.getType() == null || info.getType().fluidLocation == null || (info.capacity == 0) || (info.current == 0 && info.getType().residual == 0))
			return 0;
		
		DimensionChunkCoords coords = new DimensionChunkCoords(world.getDimensionKey(), chunkX / depositSize, chunkZ / depositSize);
		
		Long l = timeCache.get(coords);
		if(l == null){
			timeCache.put(coords, world.getGameTime());
			return info.getType().residual;
		}
		
		long lastTime = world.getGameTime();
		timeCache.put(coords, world.getGameTime());
		return lastTime != l ? info.getType().residual : 0;
	}
	
	/**
	 * Gets the OilWorldInfo object associated with the given chunk
	 * 
	 * @param world The world to retrieve
	 * @param chunkX X coordinate of desired chunk
	 * @param chunkZ Z coordinate of desired chunk
	 * @return The OilWorldInfo corresponding w/ given chunk
	 */
	public static ReservoirWorldInfo getOrCreateOilWorldInfo(World world, int chunkX, int chunkZ){
		return getOrCreateOilWorldInfo(world, new DimensionChunkCoords(world.getDimensionKey(), chunkX, chunkZ), false);
	}
	
	/**
	 * Gets the OilWorldInfo object associated with the given chunk
	 *
	 * @param world The world to retrieve
	 * @param coords Coordinates of desired chunk
	 * @param force Force creation on an empty chunk
	 * @return The OilWorldInfo corresponding w/ given chunk
	 */
	public static ReservoirWorldInfo getOrCreateOilWorldInfo(World world, DimensionChunkCoords coords, boolean force){
		if(world.isRemote)
			return null;
		
		ReservoirWorldInfo worldInfo = reservoirsCache.get(coords);
		if(worldInfo == null){
			Reservoir reservoir = null;
			
			Random r = SharedSeedRandom.createSlimeChunkSpawningSeed(coords.x, coords.z, ((ISeedReader) world).getSeed(), 90210L);
			boolean empty = (r.nextDouble() > IPServerConfig.EXTRACTION.reservoir_chance.get());
			double size = r.nextDouble();
			int query = r.nextInt();
			
			ImmersivePetroleum.log.debug("Empty? {}. Forced? {}. Size: {}, Query: {}", empty ? "Yes" : "No", force ? "Yes" : "No", size, query);
			
			if(!empty || force){
				ResourceLocation biome = world.getBiome(new BlockPos(coords.x << 4, 64, coords.z << 4)).getRegistryName();
				ResourceLocation dimension = coords.dimension.getLocation();
				ImmersivePetroleum.log.debug(coords.dimension.getLocation());
				
				int totalWeight = getTotalWeight(dimension, biome);
				ImmersivePetroleum.log.debug("Total Weight: " + totalWeight);
				if(totalWeight > 0){
					int weight = Math.abs(query % totalWeight);
					for(Reservoir res:Reservoir.map.values()){
						if(res.isValidDimension(dimension) && res.isValidBiome(biome)){
							weight -= res.weight;
							if(weight < 0){
								reservoir = res;
								break;
							}
						}
					}
				}
			}
			
			int capacity = 0;
			
			if(reservoir != null){
				ImmersivePetroleum.log.debug("Using: {}", reservoir.name);
				
				capacity = (int) ((reservoir.maxSize - reservoir.minSize) * size + reservoir.minSize);
			}
			
			ImmersivePetroleum.log.debug("Capacity: {}", capacity);
			
			worldInfo = new ReservoirWorldInfo();
			worldInfo.capacity = capacity;
			worldInfo.current = capacity;
			worldInfo.type = reservoir;
			
			ImmersivePetroleum.log.debug("Storing {} for {}", worldInfo, coords);
			reservoirsCache.put(coords, worldInfo);
		}
		
		return worldInfo;
	}
	
	/**
	 * Depletes fluid from a given chunk
	 *
	 * @param world World whose chunk to drain
	 * @param chunkX Chunk x
	 * @param chunkZ Chunk z
	 * @param amount Amount of fluid in mB to drain
	 */
	public static void depleteFluid(World world, int chunkX, int chunkZ, int amount){
		ReservoirWorldInfo info = getOrCreateOilWorldInfo(world, chunkX, chunkZ);
		info.current = Math.max(info.current - amount, 0);
		IPSaveData.setDirty();
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
		if(!totalWeightMap.containsKey(dimension)){
			totalWeightMap.put(dimension, new HashMap<>());
		}
		
		Map<ResourceLocation, Integer> dimMap = totalWeightMap.get(dimension);
		
		if(dimMap.containsKey(biome))
			return dimMap.get(biome);
		
		int totalWeight = 0;
		for(Reservoir reservoir:Reservoir.map.values()){
			if(reservoir.isValidDimension(dimension) && reservoir.isValidBiome(biome))
				totalWeight += reservoir.weight;
		}
		return totalWeight;
	}
	
	/**
	 * Adds a reservoir type to the pool of valid reservoirs
	 * 
	 * @param id The "recipeId" of the reservoir type
	 * @param reservoir The reservoir type to add
	 * @return
	 */
	public static Reservoir addReservoir(ResourceLocation id, Reservoir reservoir){
		Reservoir.map.put(id, reservoir);
		return reservoir;
	}
	
	public static void recalculateChances(){
		totalWeightMap.clear();
	}
}
