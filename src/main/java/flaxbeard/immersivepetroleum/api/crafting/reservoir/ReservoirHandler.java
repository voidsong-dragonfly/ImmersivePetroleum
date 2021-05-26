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
			double scale = 0.015625;
			double maxNoise = 0;
			for(int z = 0;z < 16;z++){
				for(int x = 0;x < 16;x++){
					double noise = generator.noiseAt((chunkX + x) * scale, (chunkZ + z) * scale, scale, x * scale);
					double chance = Math.abs(noise) / .55;
					if(chance > noiseThreshold && chance > maxNoise){
						pos = new ColumnPos(chunkX + x, chunkZ + z);
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
}
