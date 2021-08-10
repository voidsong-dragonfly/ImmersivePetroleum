package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.ColumnPos;
import net.minecraftforge.common.util.Constants.NBT;

public class ReservoirIsland{
	private Reservoir reservoir;
	private List<ColumnPos> poly;
	/** TODO Stored in Special block? Stored in ReservoirIsland? Stored Somewhere else? */
	private List<ColumnPos> wells;
	private IslandAxisAlignedBB islandAABB;
	private int amount;
	
	public ReservoirIsland(List<ColumnPos> poly, Reservoir reservoir, int amount){
		this.poly = poly;
		this.reservoir = reservoir;
		this.amount = amount;
		
		createBoundingBox();
	}
	
	void createBoundingBox(){
		int minX = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		
		int maxX = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;
		
		for(ColumnPos p:this.poly){
			if(p.x < minX) minX = p.x;
			if(p.z < minZ) minZ = p.z;
			
			if(p.x > maxX) maxX = p.x;
			if(p.z > maxZ) maxZ = p.z;
		}
		
		this.islandAABB = new IslandAxisAlignedBB(minX, minZ, maxX, maxZ);
	}
	
	/**
	 * Sets the reservoirs current fluid amount
	 */
	public ReservoirIsland setAmount(int amount){
		this.amount = amount;
		return this;
	}
	
	/**
	 * Sets the Reservoir Type
	 */
	public ReservoirIsland setReservoirType(@Nonnull Reservoir reservoir){
		this.reservoir = reservoir;
		return this;
	}
	
	public int getAmount(){
		return this.amount;
	}
	
	@Nonnull
	public Reservoir getType(){
		return this.reservoir;
	}
	
	public IslandAxisAlignedBB getBoundingBox(){
		return this.islandAABB;
	}
	
	public List<ColumnPos> getPolygon(){
		return Collections.unmodifiableList(this.poly);
	}
	
	public CompoundNBT writeToNBT(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("reservoir", this.reservoir.getId().toString());
		nbt.putInt("amount", this.getAmount());
		
		final ListNBT points = new ListNBT();
		this.poly.forEach(pos -> {
			CompoundNBT point = new CompoundNBT();
			point.putInt("x", pos.x);
			point.putInt("z", pos.z);
			points.add(point);
		});
		nbt.put("poly_points", points);
		
		return nbt;
	}
	
	public static ReservoirIsland readFromNBT(CompoundNBT nbt){
		try{
			Reservoir reservoir = Reservoir.map.get(new ResourceLocation(nbt.getString("reservoir")));
			if(reservoir != null){
				final List<ColumnPos> points = new ArrayList<>();
				final ListNBT list = nbt.getList("poly_points", NBT.TAG_COMPOUND);
				list.forEach(tag -> {
					CompoundNBT point = (CompoundNBT) tag;
					int x = point.getInt("x");
					int z = point.getInt("z");
					points.add(new ColumnPos(x, z));
				});
				
				int amount = nbt.getInt("amount");
				return new ReservoirIsland(points, reservoir, amount);
			}
		}catch(ResourceLocationException e){
			// Dont care, if it doesnt exist just move on
		}
		
		return null;
	}
	
	public void readFromNBTOld(CompoundNBT nbt){
		final List<ColumnPos> points = new ArrayList<>();
		final ListNBT list = nbt.getList("poly_points", NBT.TAG_COMPOUND);
		list.forEach(tag -> {
			CompoundNBT point = (CompoundNBT) tag;
			int x = point.getInt("x");
			int z = point.getInt("z");
			points.add(new ColumnPos(x, z));
		});
		this.poly = points;
		createBoundingBox();
	}
	
	public boolean contains(ColumnPos pos){
		return contains(pos.x, pos.z);
	}
	
	/**
	 * Same as {@link #polygonContains(int, int)} but with the Bounds as the first check.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public boolean contains(int x, int z){
		if(!this.islandAABB.contains(x, z)){
			return false;
		}
		
		return polygonContains(x, z);
	}
	
	public boolean polygonContains(ColumnPos pos){
		return polygonContains(pos.x, pos.z);
	}
	
	/**
	 * Test wether or not the given XZ coordinates are within the islands polygon.
	 * 
	 * @param x
	 * @param z
	 * @return true if the coordinates are inside, false otherwise
	 */
	public boolean polygonContains(int x, int z){
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
}
