package flaxbeard.immersivepetroleum.common.world;

import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.HashMultimap;

import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class FeatureReservoir extends Feature<NoFeatureConfig>{
	public static HashMultimap<RegistryKey<World>, ChunkPos> generatedReservoirChunks = HashMultimap.create();
	
	public FeatureReservoir(){
		super(NoFeatureConfig.CODEC);
	}
	
	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config){
		if(ReservoirHandler.generator == null){
			ReservoirHandler.generator = new PerlinNoiseGenerator(new SharedSeedRandom(reader.getSeed()), IntStream.of(0));
		}
		
		RegistryKey<World> dimension = reader.getWorld().getDimensionKey();
		IChunk chunk = reader.getChunk(pos);
		if(!generatedReservoirChunks.containsEntry(dimension, chunk.getPos())){
			generatedReservoirChunks.put(dimension, chunk.getPos());
			
			ReservoirHandler.generatePotentialReservoir(reader.getWorld(), chunk.getPos(), rand);
			return true;
		}
		return false;
	}
}
