package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import net.minecraft.util.math.BlockPos;

/**
 * TODO Move to flaxbeard.immersivepetroleum.common.util ?
 * 
 * @author TwistedGate
 */
public class IslandAxisAlignedBB{
	final int minX, minZ;
	final int maxX, maxZ;
	public IslandAxisAlignedBB(int minX, int minZ, int maxX, int maxZ){
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
	}
	
	// May end up never using it, but its here already just incase i do
	public BlockPos getCenter(){
		return new BlockPos((this.minX + this.maxX) / 2, 0, (this.minZ + this.maxZ) / 2);
	}
	
	public boolean contains(BlockPos pos){
		return contains(pos.getX(), pos.getZ());
	}
	
	public boolean contains(int x, int z){
		return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
	}
}
