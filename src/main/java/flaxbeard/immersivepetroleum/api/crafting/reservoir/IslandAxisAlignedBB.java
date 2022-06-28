package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * TODO Move to flaxbeard.immersivepetroleum.common.util ?
 * 
 * @author TwistedGate
 */
public class IslandAxisAlignedBB{
	final int minX, minZ;
	final int maxX, maxZ;
	final BlockPos center;
	public IslandAxisAlignedBB(int minX, int minZ, int maxX, int maxZ){
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
		
		this.center = new BlockPos((this.minX + this.maxX) / 2, 0, (this.minZ + this.maxZ) / 2);
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
	public String toString(){
		return String.format("IslandAxisAlignedBB [minX = %d, minZ = %d, maxX = %d, maxZ = %d]", this.minX, this.minZ, this.maxX, this.maxZ);
	}

	public static IslandAxisAlignedBB readFromNBT(CompoundTag nbt){
		int minX = nbt.getInt("minX");
		int minZ = nbt.getInt("minZ");
		int maxX = nbt.getInt("maxX");
		int maxZ = nbt.getInt("maxZ");
		
		return new IslandAxisAlignedBB(minX, minZ, maxX, maxZ);
	}
}
