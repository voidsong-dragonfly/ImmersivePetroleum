package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.common.IPSaveData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * Every instance of this class is it's own little ecosystem.
 * <p>
 * How much amount of fluid it has, what kind of fluid, how much residual fluid after it has been drained, etc.
 * 
 * @author TwistedGate
 */
public class ReservoirIsland{
	/** Primary mB/t */
	public static final int MIN_MBPT = 15;
	
	/** Pressure related maximum mB/t */
	public static final int MAX_MBPT = 2500;
	
	/** "Unsigned 32-Bit" */
	public static final long MAX_AMOUNT = 0xFFFFFFFFL;
	
	private Reservoir reservoir;
	private List<ColumnPos> poly;
	private IslandAxisAlignedBB islandAABB;
	private long amount;
	private long capacity;
	
	private ReservoirIsland(){}
	
	public ReservoirIsland(List<ColumnPos> poly, Reservoir reservoir, long amount){
		this.poly = poly;
		this.reservoir = reservoir;
		setAmount(amount);
		
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
	 * Sets the reservoirs current fluid amount in millibuckets.
	 * 
	 * @param amount of fluid in this reservoir. (Range: 0 - 4294967295})
	 */
	public ReservoirIsland setAmount(long amount){
		amount = MathHelper.clamp(amount, 0L, MAX_AMOUNT);
		
		this.amount = amount;
		this.capacity = amount;
		
		return this;
	}
	
	/**
	 * Sets the Reservoir Type
	 */
	public ReservoirIsland setReservoirType(@Nonnull Reservoir reservoir){
		this.reservoir = reservoir;
		return this;
	}
	
	/** While this returns long it only goes up to 4294967295 */
	public long getAmount(){
		return this.amount;
	}
	
	/** See {@link #getAmount()} */
	public long getCapacity(){
		return this.capacity;
	}
	
	public boolean isEmpty(){
		return this.amount <= 0L;
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
	
	/**
	 * @param x
	 * @param z
	 * @param amount to extract
	 * @return how much has been extracted or residual if drained
	 */
	public long extract(int x, int z, int amount){
		if(isEmpty()){
			return this.reservoir.residual;
		}
		
		int extracted = (int) Math.min(this.amount, amount);
		
		this.amount -= extracted;
		IPSaveData.markInstanceAsDirty();
		
		return extracted;
	}
	
	/**
	 * @param x
	 * @param z
	 * @return How much was spilled
	 */
	public int spill(World world, int x, int z){
		float pressure = getPressure(world, x, z);
		
		if(pressure > 0.0 && this.amount > 0){
			int flow = (int)Math.min(getFlow(pressure), this.amount);
			
			this.amount -= flow;
			IPSaveData.markInstanceAsDirty();
			return flow;
		}
		
		return 0;
	}
	
	/**
	 * @param pressure (Clamped: 0.0 - 1.0)
	 * @return mb
	 */
	public int getFlow(float pressure){
		return MIN_MBPT + (int) Math.floor((MAX_MBPT - MIN_MBPT) * MathHelper.clamp(pressure, 0.0F, 1.0F));
	}
	
	public float getPressure(World world, int x, int z){
		// prevents outside use
		double noise = ReservoirHandler.noiseFor(world, x, z);
		
		if(noise > 0.0D){
			// Pressure should drop from 100% to 0%
			// While the reservoir is between 100% and 50% at max
			
			boolean debug = false;
			if(debug){
				float modifier = (this.amount - (this.capacity * 0.75F)) / ((float) this.capacity);
				
				modifier = MathHelper.clamp(modifier, 0.0F, 1.0F);
				
				// Slightly modified version of what TeamSpen210 gave me, thank you!
				float min = 1F;
				float max = 1000F;
				float decay = (float) (-Math.log(min / max) / 1.0D);
				float output = (float) (Math.exp(decay * noise) / max) * modifier;
				
				return output <= 0.001F ? 0.0F : output;
			}else{
				double half = this.capacity * 0.50;
				double alt = this.amount - half;
				if(alt > 0){
					double pre = alt / half;
					return (float) (pre * noise);
				}
			}
		}
		
		return 0.0F;
	}
	
	public CompoundNBT writeToNBT(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("reservoir", this.reservoir.getId().toString());
		nbt.putInt("amount", (int)(this.getAmount() & MAX_AMOUNT));
		nbt.putInt("capacity", (int)(this.getCapacity() & MAX_AMOUNT));
		nbt.put("bounds", this.getBoundingBox().writeToNBT());
		
		final IslandAxisAlignedBB bounds = this.getBoundingBox();
		final ListNBT points = new ListNBT();
		this.poly.forEach(pos -> {
			byte x = (byte) ((pos.x - bounds.minX) & 0xFF);
			byte z = (byte) ((pos.z - bounds.minZ) & 0xFF);
			
			CompoundNBT point = new CompoundNBT();
			point.putByte("x", x);
			point.putByte("z", z);
			points.add(point);
			
		});
		nbt.put("points", points);
		
		return nbt;
	}
	
	public static ReservoirIsland readFromNBT(CompoundNBT nbt){
		try{
			Reservoir reservoir = Reservoir.map.get(new ResourceLocation(nbt.getString("reservoir")));
			if(reservoir != null){
				long amount = ((long)nbt.getInt("amount")) & MAX_AMOUNT;
				long capacity = ((long)nbt.getInt("capacity")) & MAX_AMOUNT;
				IslandAxisAlignedBB bounds = IslandAxisAlignedBB.readFromNBT(nbt.getCompound("bounds"));
				
				final List<ColumnPos> points = new ArrayList<>();
				final ListNBT list = nbt.getList("points", NBT.TAG_COMPOUND);
				list.forEach(tag -> {
					CompoundNBT point = (CompoundNBT) tag;
					int x = bounds.minX + ((int) point.getByte("x") & 0xFF);
					int z = bounds.minZ + ((int) point.getByte("z") & 0xFF);
					points.add(new ColumnPos(x, z));
				});
				
				ReservoirIsland island = new ReservoirIsland();
				island.reservoir = reservoir;
				island.amount = amount;
				island.capacity = capacity;
				island.poly = points;
				island.islandAABB = bounds;
				return island;
			}
		}catch(ResourceLocationException e){
			// Dont care, if it doesnt exist just move on
		}
		
		return null;
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
}
