package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class Island{
	public static final Multimap<RegistryKey<World>, Island> ALL = ArrayListMultimap.create();
	
	private List<ColumnPos> poly;
	public Island(List<ColumnPos> poly){
		this.poly = poly;
	}
	
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		if(this.poly != null && !this.poly.isEmpty()){
			final ListNBT points = new ListNBT();
			this.poly.forEach(pos -> {
				CompoundNBT point = new CompoundNBT();
				point.putInt("x", pos.x);
				point.putInt("z", pos.z);
				points.add(point);
			});
			nbt.put("poly_points", points);
		}
		
		return nbt;
	}
	
	public void readFromNBT(CompoundNBT nbt){
		if(nbt.contains("poly_points", NBT.TAG_LIST)){
			final List<ColumnPos> points = new ArrayList<>();
			final ListNBT list = nbt.getList("poly_points", NBT.TAG_COMPOUND);
			list.forEach(tag -> {
				CompoundNBT point = (CompoundNBT) tag;
				int x = point.getInt("x");
				int z = point.getInt("z");
				points.add(new ColumnPos(x, z));
			});
			this.poly = points;
		}
	}
	
	public boolean isInside(ColumnPos pos){
		return isInside(pos.x, pos.z);
	}
	
	public boolean isInside(int x, int z){
		boolean ret = false;
		int j = this.poly.size() - 1;
		for(int i = 0;i < this.poly.size();i++){
			ColumnPos a = this.poly.get(i);
			ColumnPos b = this.poly.get(j);
			
			// They need to be floats or it wont work for some reason
			float ax = a.x, az = a.z;
			float bx = b.x, bz = b.z;
			
			// Any point directly on the edge is considered "outside"
			if((ax == x && az == z)){
				return false;
			}else if((ax == x && bx == x) && ((z > bz && z < az) || (z > az && z < bz))){
				return false;
			}else if((az == z && bz == z) && ((x > ax && x < bx) || (x > bx && x < ax))){
				return false;
			}
			
			// Voodoo Magic for Point-In-Polygon
			if(((az < z && bz >= z) || (bz < z && az >= z)) && (ax <= x || bx <= x)){
				float f0 = ax + (z - az) / (bz - az) * (bx - ax);
				ret ^= (f0 < x);
			}
			
			j = i;
		}
		
		return ret;
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
	
	/** Recursively discover the whole island */
	public static void next(List<ColumnPos> list, int x, int z){
		if(ReservoirHandler.noiseFor(x, z) > -1 && !list.contains(new ColumnPos(x, z))){
			list.add(new ColumnPos(x, z));
			
			next(list, x + 1, z);
			next(list, x - 1, z);
			next(list, x, z + 1);
			next(list, x, z - 1);
		}
	}
	
	boolean inPoly(int x, int z, List<ColumnPos> poly){
		boolean ret = false;
		int j = poly.size() - 1;
		for(int i = 0;i < poly.size();i++){
			ColumnPos a = poly.get(i);
			ColumnPos b = poly.get(j);
			
			// They need to be floats or it wont work for some reason
			float ax = a.x, az = a.z;
			float bx = b.x, bz = b.z;
			
			// Any point directly on the edge is considered "outside"
			if((ax == x && az == z)){
				return false;
			}else if((ax == x && bx == x) && ((z > bz && z < az) || (z > az && z < bz))){
				return false;
			}else if((az == z && bz == z) && ((x > ax && x < bx) || (x > bx && x < ax))){
				return false;
			}
			
			// Voodoo Magic for Point-In-Polygon
			if(((az < z && bz >= z) || (bz < z && az >= z)) && (ax <= x || bx <= x)){
				float f0 = ax + (z - az) / (bz - az) * (bx - ax);
				ret ^= (f0 < x);
			}
			
			j = i;
		}
		
		return ret;
	}
}
