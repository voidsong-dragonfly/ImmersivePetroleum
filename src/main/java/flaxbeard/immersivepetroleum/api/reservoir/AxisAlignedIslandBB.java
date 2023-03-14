package flaxbeard.immersivepetroleum.api.reservoir;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * Simple BoundingBox for ReservoirIslands
 * 
 * @author TwistedGate
 */
public class AxisAlignedIslandBB{
	final int minX, minZ;
	final int maxX, maxZ;
	final BlockPos center;
	
	public AxisAlignedIslandBB(int minX, int minZ, int maxX, int maxZ){
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
		
		this.center = new BlockPos((this.minX + this.maxX) / 2, 0, (this.minZ + this.maxZ) / 2);
	}
	
	public AxisAlignedIslandBB(CompoundTag nbt){
		this(
			nbt.getInt("minX"),
			nbt.getInt("minZ"),
			nbt.getInt("maxX"),
			nbt.getInt("maxZ")
		);
	}
	
	public int minX(){
		return this.minX;
	}
	
	public int maxX(){
		return this.maxX;
	}
	
	public int minZ(){
		return this.minZ;
	}
	
	public int maxZ(){
		return this.maxZ;
	}
	
	public BlockPos getCenter(){
		return this.center;
	}
	
	public boolean contains(BlockPos pos){
		return contains(pos.getX(), pos.getZ());
	}
	
	public boolean contains(int x, int z){
		return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
	}
	
	public CompoundTag writeToNBT(){
		CompoundTag bounds = new CompoundTag();
		bounds.putInt("minX", this.minX);
		bounds.putInt("minZ", this.minZ);
		bounds.putInt("maxX", this.maxX);
		bounds.putInt("maxZ", this.maxZ);
		return bounds;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(this.maxX, this.maxZ, this.minX, this.minZ);
	}
	
	@Override
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		}
		if(!(obj instanceof AxisAlignedIslandBB)){
			return false;
		}
		AxisAlignedIslandBB other = (AxisAlignedIslandBB) obj;
		return this.maxX == other.maxX && this.maxZ == other.maxZ && this.minX == other.minX && this.minZ == other.minZ;
	}
	
	@Override
	public String toString(){
		return String.format("IslandAxisAlignedBB [minX = %d, minZ = %d, maxX = %d, maxZ = %d]", this.minX, this.minZ, this.maxX, this.maxZ);
	}
	
	/** @deprecated Use constructor instead. {@link #IslandAxisAlignedBB(CompoundTag)} */
	@Deprecated(forRemoval = true)
	public static AxisAlignedIslandBB readFromNBT(CompoundTag nbt){
		return new AxisAlignedIslandBB(nbt);
	}
}
