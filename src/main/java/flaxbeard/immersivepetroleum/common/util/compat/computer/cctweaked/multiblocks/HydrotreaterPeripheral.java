package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks;

import java.util.Map;

import dan200.computercraft.api.lua.LuaFunction;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.CCTUtils;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.generic.PoweredMultiblockPeripheral;
import net.minecraftforge.fluids.FluidStack;

public class HydrotreaterPeripheral extends PoweredMultiblockPeripheral{
	private static final String TYPE = ResourceUtils.ip("hydrotreater").toString();
	
	HydrotreaterTileEntity master;
	public HydrotreaterPeripheral(HydrotreaterTileEntity treater){
		super(treater);
		this.master = treater.master();
	}
	
	@Override
	public String getType(){
		return TYPE;
	}
	
	@LuaFunction
	public final int getTankSize(int tank){
		switch(tank){
			case 1:
			case 2:
			case 3:
				return this.master.tanks[tank - 1].getCapacity();
			default:
				return -1;
		}
	}
	
	@LuaFunction
	public final Map<String, Object> getInputTank(int tank){
		switch(tank){
			case 1:
			case 2:
				return CCTUtils.fluidToMap(this.master.tanks[tank - 1].getFluid());
			default:
				return CCTUtils.fluidToMap(FluidStack.EMPTY);
		}
	}
	
	@LuaFunction
	public final Map<String, Object> getOutputTank(){
		return CCTUtils.fluidToMap(this.master.tanks[HydrotreaterTileEntity.TANK_OUTPUT].getFluid());
	}
}
