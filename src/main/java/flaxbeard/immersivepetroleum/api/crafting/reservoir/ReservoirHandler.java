package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.server.ServerWorld;

/**
 * This takes care of dealing with generating, storing and caching (Faster access for regulary queried positions) reservoir islands.
 * 
 * @author TwistedGate
 */
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
						poly = optimizeLines(direction(edgy(poly)));
						
						int amount = (int) MathHelper.lerp(random.nextFloat(), reservoir.minSize, reservoir.maxSize);
						ReservoirIsland island = new ReservoirIsland(poly, reservoir, amount);
						RESERVOIR_ISLAND_LIST.put(world.getDimensionKey(), island);
					}
				}
			}
		}
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
		
		// TODO Maybe do this better somehow? It'll do for testing, but not for real-world stuff probably
		
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
	
	public static void recalculateChances(){
		totalWeightMap.clear();
	}
	
	/**
	 * {@link #clearCache()} Must be called after modifying the returned list!
	 * 
	 * @return
	 */
	public static Multimap<RegistryKey<World>, ReservoirIsland> getReservoirIslandList(){
		return RESERVOIR_ISLAND_LIST;
	}
	
	// ####################################################
	// Optimization methods below. Warning, overengineered!
	// ####################################################
	
	/** Keep edges/corners and dump the rest */
	private static List<ColumnPos> edgy(List<ColumnPos> poly){
		final List<ColumnPos> list = new ArrayList<>();
		poly.forEach(pos -> {
			for(int z = -1;z <= 1;z++){
				for(int x = -1;x <= 1;x++){
					if(ReservoirHandler.noiseFor(pos.x + 1, pos.z) == -1){
						ColumnPos p = new ColumnPos(pos.x + 1, pos.z);
						if(!list.contains(p)){
							list.add(p);
						}
					}
					if(ReservoirHandler.noiseFor(pos.x - 1, pos.z) == -1){
						ColumnPos p = new ColumnPos(pos.x - 1, pos.z);
						if(!list.contains(p)){
							list.add(p);
						}
					}
					if(ReservoirHandler.noiseFor(pos.x, pos.z + 1) == -1){
						ColumnPos p = new ColumnPos(pos.x, pos.z + 1);
						if(!list.contains(p)){
							list.add(p);
						}
					}
					if(ReservoirHandler.noiseFor(pos.x, pos.z - 1) == -1){
						ColumnPos p = new ColumnPos(pos.x, pos.z - 1);
						if(!list.contains(p)){
							list.add(p);
						}
					}
				}
			}
		});
		
		return list;
	}
	
	/**
	 * Give this some direction. Result can end up being either clockwise or
	 * counter-clockwise!
	 */
	private static List<ColumnPos> direction(List<ColumnPos> poly){
		List<ColumnPos> list = new ArrayList<>();
		list.add(poly.remove(0));
		int a = 0;
		while(poly.size() > 0){
			final ColumnPos col = list.get(a);
			
			if(moveNext(col, poly, list)){
				a++;
			}else{
				ImmersivePetroleum.log.warn("This should not happen, but it did..");
				break;
			}
		}
		
		return list;
	}
	
	/**
	 * <pre>
	 * Straight Line Optimizations (Cut down on number of Points)
	 * to avoid things like #### and turn them into #--#
	 * Where # is a Point, and - is just an imaginary line between.
	 * 
	 * For X and Z
	 * </pre>
	 */
	private static ArrayList<ColumnPos> optimizeLines(List<ColumnPos> poly){
		ArrayList<ColumnPos> list = new ArrayList<>(poly);
		
		int endIndex = 0;
		ColumnPos startPos = null, endPos = null;
		for(int startIndex = 0;startIndex < list.size();startIndex++){
			startPos = list.get(startIndex);
			
			// Find the end of the current line on X
			{
				for(int j = 1;j < 64;j++){
					int index = (startIndex + j) % list.size();
					ColumnPos pos = list.get(index);
					
					if(startPos.z != pos.z){
						break;
					}
					
					endIndex = index;
					endPos = pos;
				}
			}
			
			// Find the end the current line on Z
			{
				for(int j = 1;j < 64;j++){
					int index = (startIndex + j) % list.size();
					ColumnPos pos = list.get(index);
					
					if(startPos.x != pos.x){
						break;
					}
					
					endIndex = index;
					endPos = pos;
				}
			}
			
			// Diagonal lines?
			boolean debug = false;
			if(debug){
				for(int j = 1;j < 64;j++){
					int index = (startIndex + j) % list.size();
					ColumnPos pos = list.get(index);
					
					int dx = Math.abs(pos.x - startPos.x);
					int dz = Math.abs(pos.z - startPos.z);
					
					if(dx != dz){
						break;
					}
					
					endIndex = index;
					endPos = pos;
				}
			}
			
			// Commence culling
			if(startPos != null && endPos != null){
				int len = (endIndex - startIndex);
				if(len > 1){
					int index = startIndex + 1;
					for(int j = index;j < endIndex;j++){
						list.remove(index % list.size());
					}
				}else if(len < 0){
					// Start and End overlap themselfs
					len = len + list.size() - 1;
					
					if(len > 1){
						int index = startIndex + 1;
						for(int j = 0;j < len;j++){
							list.remove(index % list.size());
						}
					}
				}
				
				startPos = endPos = null;
			}
		}
		
		return list;
	}
	
	private static boolean moveNext(ColumnPos pos, List<ColumnPos> list0, List<ColumnPos> list1){
		// X Z axis biased
		ColumnPos p0 = new ColumnPos(pos.x + 1, pos.z);
		ColumnPos p1 = new ColumnPos(pos.x - 1, pos.z);
		ColumnPos p2 = new ColumnPos(pos.x, pos.z + 1);
		ColumnPos p3 = new ColumnPos(pos.x, pos.z - 1);
		
		if((list0.remove(p0) && list1.add(p0)) || (list0.remove(p1) && list1.add(p1)) || (list0.remove(p2) && list1.add(p2)) || (list0.remove(p3) && list1.add(p3))){
			return true;
		}
		
		// Diagonals
		ColumnPos p4 = new ColumnPos(pos.x - 1, pos.z - 1);
		ColumnPos p5 = new ColumnPos(pos.x - 1, pos.z + 1);
		ColumnPos p6 = new ColumnPos(pos.x + 1, pos.z - 1);
		ColumnPos p7 = new ColumnPos(pos.x + 1, pos.z + 1);
		
		if((list0.remove(p4) && list1.add(p4)) || (list0.remove(p5) && list1.add(p5)) || (list0.remove(p6) && list1.add(p6)) || (list0.remove(p7) && list1.add(p7))){
			return true;
		}
		
		return false;
	}
}
