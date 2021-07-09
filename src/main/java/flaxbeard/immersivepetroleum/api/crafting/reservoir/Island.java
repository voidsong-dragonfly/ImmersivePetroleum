package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.World;

public class Island{
	public static final Multimap<RegistryKey<World>, Island> ALL = ArrayListMultimap.create();
	
	List<ColumnPos> list;
	public Island(List<ColumnPos> list){
		this.list = list;
	}
	
	public boolean contains(ColumnPos pos){
		return contains(pos.x, pos.z);
	}
	
	public boolean contains(int x, int z){
		return false;
	}
	
	public static ColumnPos getFirst(int chunkStartX, int chunkStartZ){
		for(int j = 0;j < 16;j++){
			for(int i = 0;i < 16;i++){
				int x = chunkStartX + i;
				int z = chunkStartZ + j;
				if(ReservoirHandler.noiseFor(x, z) > -1){
					return new ColumnPos(x, z);
				}
			}
		}
		
		return null;
	}
	
	public static void next(List<ColumnPos> list, int x, int z){
		if(ReservoirHandler.noiseFor(x, z) > -1 && !list.contains(new ColumnPos(x, z))){
			list.add(new ColumnPos(x, z));
			
			next(list, x + 1, z);
			next(list, x - 1, z);
			next(list, x, z + 1);
			next(list, x, z - 1);
		}
	}
}
