package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import flaxbeard.immersivepetroleum.common.items.DebugItem;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.server.ServerWorld;

public class ReservoirHandler{
	private static final Multimap<RegistryKey<World>, ReservoirIsland> RESERVOIR_ISLAND_LIST = ArrayListMultimap.create();
	private static final Map<Pair<RegistryKey<World>, ColumnPos>, ReservoirIsland> CACHE = new HashMap<>();
	
	private static Map<ResourceLocation, Map<ResourceLocation, Integer>> totalWeightMap = new HashMap<>();
	
	public static INoiseGenerator generator;
	public static double noiseThreshold = 0;
	
	public static void scanChunkForNewReservoirs(ServerWorld world, ChunkPos chunkPos, Random random){
		int chunkX = chunkPos.getXStart();
		int chunkZ = chunkPos.getZStart();
		
		RegistryKey<World> dimensionKey = world.getDimensionKey();
		ResourceLocation dimensionRL = dimensionKey.getRegistryName();
		
		for(int j = 0;j < 16;j++){
			for(int i = 0;i < 16;i++){
				int x = chunkX + i;
				int z = chunkZ + j;
				
				if(ReservoirHandler.noiseFor(x, z) > -1){
					// Getting the biome now to prevent lockups
					ResourceLocation biome = world.getBiome(new BlockPos(x, 64, z)).getRegistryName();
					
					synchronized(RESERVOIR_ISLAND_LIST){
						final ColumnPos current = new ColumnPos(x, z);
						if(RESERVOIR_ISLAND_LIST.values().stream().anyMatch(island -> island.contains(current))){
							continue;
						}
						
						Reservoir reservoir = null;
						int totalWeight = getTotalWeight(dimensionRL, biome);
						if(totalWeight > 0){
							int weight = Math.abs(random.nextInt() % totalWeight);
							for(Reservoir res:Reservoir.map.values()){
								if(res.isValidDimension(dimensionRL) && res.isValidBiome(biome)){
									weight -= res.weight;
									if(weight < 0){
										reservoir = res;
										break;
									}
								}
							}
						}
						
						List<ColumnPos> poly = new ArrayList<>();
						next(poly, x, z);
						poly = DebugItem.optimizeLines(DebugItem.direction(DebugItem.edgy(poly)));
						
						int amount = (int) MathHelper.lerp(random.nextFloat(), reservoir.minSize, reservoir.maxSize);
						ReservoirIsland island = new ReservoirIsland(poly, reservoir, amount);
						RESERVOIR_ISLAND_LIST.put(world.getDimensionKey(), island);
					}
				}
			}
		}
	}
	
	public static int getTotalWeight(ResourceLocation dimension, ResourceLocation biome){
		Map<ResourceLocation, Integer> map = totalWeightMap.get(dimension);
		if(map == null){
			map = new HashMap<>();
			totalWeightMap.put(dimension, map);
		}
		
		Integer totalWeight = map.get(biome);
		if(totalWeight == null){
			totalWeight = new Integer(0);
			
			for(Reservoir reservoir:Reservoir.map.values()){
				if(reservoir.isValidDimension(dimension) && reservoir.isValidBiome(biome)){
					totalWeight += reservoir.weight;
				}
			}
			
			map.put(biome, totalWeight);
		}
		
		return totalWeight;
	}
	
	public static ReservoirIsland getIsland(World world, BlockPos pos){
		return getIsland(world, new ColumnPos(pos.getX(), pos.getZ()));
	}
	
	public static ReservoirIsland getIsland(World world, ColumnPos pos){
		if(world.isRemote){
			return null;
		}
		
		// TODO Maybe do this better somehow? It'll do for testing, but not for real-world stuff
		
		RegistryKey<World> dimension = world.getDimensionKey();
		Pair<RegistryKey<World>, ColumnPos> cacheKey = Pair.of(dimension, pos);
		synchronized(RESERVOIR_ISLAND_LIST){
			ReservoirIsland ret = CACHE.get(cacheKey);
			if(ret == null){
				for(ReservoirIsland island:RESERVOIR_ISLAND_LIST.get(dimension)){
					if(island.polygonContains(pos)){
						/*
						 * There's no such thing as overlapping islands, so just
						 * return what was found directly (After putting it into
						 * the cache)
						 */
						CACHE.put(cacheKey, island);
						return island;
					}
				}
			}
			return ret;
		}
	}
	
	static final double scale = 0.015625D;
	static final double d0 = 2 / 3D;
	static final double d1 = 1 / 3D;
	
	/**
	 * @param x Block Position
	 * @param z Block Position
	 * @return -1 (Nothing/Empty), >=0.0 means there's <i>something</i>
	 */
	public static double noiseFor(int x, int z){
		if(generator != null){
			double noise = Math.abs(generator.noiseAt(x * scale, z * scale, scale, x * scale)) / .55;
			if(noise > d0){
				return (noise - d0) / d1;
			}
		}
		return -1D;
		
	}
	
	/** Recursively discover the whole island */
	static void next(List<ColumnPos> list, int x, int z){
		if(ReservoirHandler.noiseFor(x, z) > -1 && !list.contains(new ColumnPos(x, z))){
			list.add(new ColumnPos(x, z));
			
			next(list, x + 1, z);
			next(list, x - 1, z);
			next(list, x, z + 1);
			next(list, x, z - 1);
		}
	}
	
	public static void clearCache(){
		synchronized(RESERVOIR_ISLAND_LIST){
			CACHE.clear();
		}
	}
	
	/**
	 * {@link #clearCache()} Must be called after modifying the returned list!
	 * 
	 * @return
	 */
	public static Multimap<RegistryKey<World>, ReservoirIsland> getReservoirIslandList(){
		return RESERVOIR_ISLAND_LIST;
	}
}
