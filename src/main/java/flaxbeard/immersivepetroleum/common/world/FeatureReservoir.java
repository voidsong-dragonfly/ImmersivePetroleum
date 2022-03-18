package flaxbeard.immersivepetroleum.common.world;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.HashMultimap;

import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class FeatureReservoir extends Feature<NoneFeatureConfiguration>{
	public static HashMultimap<ResourceKey<Level>, ChunkPos> generatedReservoirChunks = HashMultimap.create();
	
	public FeatureReservoir(){
		super(NoneFeatureConfiguration.CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext){
		WorldGenLevel reader = pContext.level();
		BlockPos pos = pContext.origin();
		if(ReservoirHandler.generator == null){
			ReservoirHandler.generator = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(reader.getSeed())), List.of(0));
		}
		
		ResourceKey<Level> dimension = reader.getLevel().dimension();
		ChunkAccess chunk = reader.getChunk(pos);
		if(!generatedReservoirChunks.containsEntry(dimension, chunk.getPos())){
			generatedReservoirChunks.put(dimension, chunk.getPos());
			
			ReservoirHandler.scanChunkForNewReservoirs(reader.getLevel(), chunk.getPos(), pContext.random());
			return true;
		}
		return false;
	}
}
