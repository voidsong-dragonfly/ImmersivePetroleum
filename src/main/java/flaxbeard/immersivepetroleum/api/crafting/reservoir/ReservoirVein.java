package flaxbeard.immersivepetroleum.api.crafting.reservoir;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ColumnPos;
import net.minecraftforge.common.util.Lazy;

public class ReservoirVein{
	final ColumnPos pos;
	final ResourceLocation name;
	final Lazy<Reservoir> reservoir;
	public ReservoirVein(ColumnPos pos, ResourceLocation name){
		this.pos = pos;
		this.name = name;
		this.reservoir = Lazy.of(() -> Reservoir.map.get(name));
	}
	
	public ColumnPos getPos(){
		return this.pos;
	}
	
	public Reservoir getReservoir(){
		return this.reservoir.get();
	}
	
	public CompoundNBT writeToNBT(){
		CompoundNBT nbt = new CompoundNBT();
		
		nbt.putInt("x", this.pos.x);
		nbt.putInt("z", this.pos.z);
		nbt.putString("name", this.name.toString());
		
		return nbt;
	}
}
