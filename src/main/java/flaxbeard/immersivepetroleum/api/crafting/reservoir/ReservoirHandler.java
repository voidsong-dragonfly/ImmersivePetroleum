package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.server.ServerWorld;

public class ReservoirHandler{
	private static final Multimap<RegistryKey<World>, ReservoirIsland> RESERVOIR_ISLAND_LIST = ArrayListMultimap.create();
	private static final Map<Pair<RegistryKey<World>, ColumnPos>, ReservoirIsland> CACHE = new HashMap<>();
	
	public static INoiseGenerator generator;
	public static double noiseThreshold = 0;
	
	public static void scanChunkForNewReservoirs(ServerWorld world, ChunkPos chunkPos, Random random){
		int chunkX = chunkPos.getXStart();
		int chunkZ = chunkPos.getZStart();
		for(int j = 0;j < 16;j++){
			for(int i = 0;i < 16;i++){
				int x = chunkX + i;
				int z = chunkZ + j;
				
				if(ReservoirHandler.noiseFor(x, z) > -1){
					ColumnPos pos = new ColumnPos(x, z);
					// TODO
				}
			}
		}
	}
	
	public static ReservoirIsland getIsland(World world, ColumnPos pos){
		if(world.isRemote){
			return null;
		}
		
		RegistryKey<World> dimension = world.getDimensionKey();
		Pair<RegistryKey<World>, ColumnPos> cacheKey = Pair.of(dimension, pos);
		synchronized(RESERVOIR_ISLAND_LIST){
			ReservoirIsland ret = CACHE.get(cacheKey);
			if(ret == null){
				// TODO Maybe do this better somehow? It'll do for testing, but not for real-world stuff
				for(ReservoirIsland island:RESERVOIR_ISLAND_LIST.get(dimension)){
					if(island.contains(pos)){
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
	static final double d0 = 0.6666666666666667;
	static final double d1 = 0.3333333333333333D;
	
	/**
	 * @param x Block Position
	 * @param z Block Position
	 * @return -1 (Nothing/Empty), >=0.0 means there's <i>something</i>
	 */
	public static double noiseFor(int x, int z){
		if(generator == null){
			return -1D;
		}
		
		double noise = Math.abs(generator.noiseAt(x * scale, z * scale, scale, x * scale)) / .55;
		
		double ret = -1D;
		if(noise > d0){
			ret = (noise - d0) / d1;
		}
		
		return ret;
	}
	
	public static void clearCache(){
		synchronized(RESERVOIR_ISLAND_LIST){
			CACHE.clear();
		}
	}
	
	public static Multimap<RegistryKey<World>, ReservoirIsland> getReservoirIslandList(){
		return RESERVOIR_ISLAND_LIST;
	}
}
