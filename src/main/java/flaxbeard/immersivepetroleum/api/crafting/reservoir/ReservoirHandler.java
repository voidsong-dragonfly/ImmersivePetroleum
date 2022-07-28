package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

/**
 * This takes care of dealing with generating, storing and caching (Faster access for regulary queried positions) reservoir islands.
 * 
 * @author TwistedGate
 */
// FIXME There is a leak *SOMEWHERE* that causes islands from an existing world to carry over to freshly created worlds
public class ReservoirHandler{
	private static final Multimap<ResourceKey<Level>, ReservoirIsland> RESERVOIR_ISLAND_LIST = ArrayListMultimap.create();
	private static final Map<Pair<ResourceKey<Level>, ColumnPos>, ReservoirIsland> CACHE = new HashMap<>();
	
	private static Map<ResourceLocation, Map<ResourceLocation, Integer>> totalWeightMap = new HashMap<>();
	
	static long lastSeed;
	public static PerlinSimplexNoise generator;
	public static double noiseThreshold = 0;
	
	public static void scanChunkForNewReservoirs(ServerLevel world, ChunkPos chunkPos, Random random){
		int chunkX = chunkPos.getMinBlockX();
		int chunkZ = chunkPos.getMinBlockZ();
		
		ResourceKey<Level> dimensionKey = world.dimension();
		ResourceLocation dimensionRL = dimensionKey.location();
		
		for(int j = 0;j < 16;j++){
			for(int i = 0;i < 16;i++){
				int x = chunkX + i;
				int z = chunkZ + j;
				
				if(ReservoirHandler.getValueOf(world, x, z) > -1){
					// Getting the biome now to prevent lockups
					ResourceLocation biome = world.getBiome(new BlockPos(x, 64, z)).value().getRegistryName();
					
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
						next(world, poly, x, z);
						poly = optimizeIsland(world, poly);
						
						int amount = (int) Mth.lerp(random.nextFloat(), reservoir.minSize, reservoir.maxSize);
						ReservoirIsland island = new ReservoirIsland(poly, reservoir, amount);
						RESERVOIR_ISLAND_LIST.put(dimensionKey, island);
						IPSaveData.markInstanceAsDirty();
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
			totalWeight = Integer.valueOf(0);
			
			for(Reservoir reservoir:Reservoir.map.values()){
				if(reservoir.isValidDimension(dimension) && reservoir.isValidBiome(biome)){
					totalWeight += reservoir.weight;
				}
			}
			
			map.put(biome, totalWeight);
		}
		
		return totalWeight;
	}
	
	/** May only be called on the server-side. Returns null on client-side. */
	public static ReservoirIsland getIsland(Level world, BlockPos pos){
		return getIsland(world, new ColumnPos(pos));
	}
	
	/** May only be called on the server-side. Returns null on client-side. */
	public static ReservoirIsland getIsland(Level world, ColumnPos pos){
		if(world.isClientSide){
			return null;
		}
		
		// TODO Maybe do this better somehow? It'll do for testing, but not for real-world stuff probably
		
		ResourceKey<Level> dimension = world.dimension();
		Pair<ResourceKey<Level>, ColumnPos> cacheKey = Pair.of(dimension, pos);
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
	 * <i>Only call on server side!</i>
	 * 
	 * @param world
	 * @param x Block Position
	 * @param z Block Position
	 * @return -1 (Nothing/Empty), >=0.0 means there's <i>something</i>
	 */
	public static double getValueOf(@Nonnull Level world, int x, int z){
		if(!world.isClientSide){
			if(generator == null || ((WorldGenLevel) world).getSeed() != lastSeed){
				lastSeed = ((WorldGenLevel) world).getSeed();
				generator = new PerlinSimplexNoise(new WorldgenRandom(new SingleThreadedRandomSource(lastSeed)), ImmutableList.of(0));
//				generator = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(lastSeed)), ImmutableList.of(0));
			}
		}
		
		double noise = Math.abs(generator.getValue(x * scale, z * scale, false));
		if(noise > d0){
			return (noise - d0) / d1;
		}
		
		return -1D;
	}
	
	/** Recursively discover the whole island */
	static void next(Level world, List<ColumnPos> list, int x, int z){
		if(ReservoirHandler.getValueOf(world, x, z) > -1 && !list.contains(new ColumnPos(x, z))){
			list.add(new ColumnPos(x, z));
			
			next(world, list, x + 1, z);
			next(world, list, x - 1, z);
			next(world, list, x, z + 1);
			next(world, list, x, z - 1);
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
	public static Multimap<ResourceKey<Level>, ReservoirIsland> getReservoirIslandList(){
		return RESERVOIR_ISLAND_LIST;
	}
	
	// ####################################################
	// Optimization methods below. Warning, overengineered!
	// ####################################################
	
	private static List<ColumnPos> optimizeIsland(Level world, List<ColumnPos> poly){
		poly = keepOutline(world, poly);
		poly = makeDirectional(poly);
		poly = cullLines(poly);
		
		return poly;
	}
	
	/** Keep edges/corners and dump the rest */
	private static List<ColumnPos> keepOutline(Level world, List<ColumnPos> poly){
		final List<ColumnPos> list = new ArrayList<>();
		poly.forEach(pos -> {
			for(int z = -1;z <= 1;z++){
				for(int x = -1;x <= 1;x++){
					if(ReservoirHandler.getValueOf(world, pos.x + 1, pos.z) == -1){
						ColumnPos p = new ColumnPos(pos.x + 1, pos.z);
						if(!list.contains(p)){
							list.add(p);
						}
					}
					if(ReservoirHandler.getValueOf(world, pos.x - 1, pos.z) == -1){
						ColumnPos p = new ColumnPos(pos.x - 1, pos.z);
						if(!list.contains(p)){
							list.add(p);
						}
					}
					if(ReservoirHandler.getValueOf(world, pos.x, pos.z + 1) == -1){
						ColumnPos p = new ColumnPos(pos.x, pos.z + 1);
						if(!list.contains(p)){
							list.add(p);
						}
					}
					if(ReservoirHandler.getValueOf(world, pos.x, pos.z - 1) == -1){
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
	private static List<ColumnPos> makeDirectional(List<ColumnPos> poly){
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
	private static ArrayList<ColumnPos> cullLines(List<ColumnPos> poly){
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
	
	private static boolean moveNext(ColumnPos pos, List<ColumnPos> src, List<ColumnPos> dst){
		// X Z axis biased
		ColumnPos p0 = new ColumnPos(pos.x + 1, pos.z);
		ColumnPos p1 = new ColumnPos(pos.x - 1, pos.z);
		ColumnPos p2 = new ColumnPos(pos.x, pos.z + 1);
		ColumnPos p3 = new ColumnPos(pos.x, pos.z - 1);
		
		if((src.remove(p0) && dst.add(p0)) || (src.remove(p1) && dst.add(p1)) || (src.remove(p2) && dst.add(p2)) || (src.remove(p3) && dst.add(p3))){
			return true;
		}
		
		// Diagonals
		ColumnPos p4 = new ColumnPos(pos.x - 1, pos.z - 1);
		ColumnPos p5 = new ColumnPos(pos.x - 1, pos.z + 1);
		ColumnPos p6 = new ColumnPos(pos.x + 1, pos.z - 1);
		ColumnPos p7 = new ColumnPos(pos.x + 1, pos.z + 1);
		
		if((src.remove(p4) && dst.add(p4)) || (src.remove(p5) && dst.add(p5)) || (src.remove(p6) && dst.add(p6)) || (src.remove(p7) && dst.add(p7))){
			return true;
		}
		
		return false;
	}
}
