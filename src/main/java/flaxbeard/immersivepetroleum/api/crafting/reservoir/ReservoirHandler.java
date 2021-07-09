package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.server.ServerWorld;

public class ReservoirHandler{
	private static final Multimap<RegistryKey<World>, ReservoirVein> RESERVOIR_VEIN_LIST = ArrayListMultimap.create();
	
	public static INoiseGenerator generator;
	public static double noiseThreshold = 0;
	
	public static void generatePotentialReservoir(ServerWorld world, ChunkPos chunkPos, Random random){
		ColumnPos pos = null;
		{
			int chunkX = chunkPos.getXStart();
			int chunkZ = chunkPos.getZStart();
			double maxNoise = 0;
			for(int j = 0;j < 16;j++){
				for(int i = 0;i < 16;i++){
					int x = chunkX + i;
					int z = chunkZ + j;
					
					double chance = noiseFor(x, z);
					if(chance > noiseThreshold && chance > maxNoise){
						pos = new ColumnPos(x, z);
						maxNoise = chance;
					}
				}
			}
		}
		
		if(pos != null){
			synchronized(RESERVOIR_VEIN_LIST){
				
			}
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
}
