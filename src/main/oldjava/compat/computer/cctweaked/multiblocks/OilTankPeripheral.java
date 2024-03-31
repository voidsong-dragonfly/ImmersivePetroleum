package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks;

import java.util.Map;

import dan200.computercraft.api.lua.LuaFunction;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.CCTUtils;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.generic.MultiblockPartPeripheral;

public class OilTankPeripheral extends MultiblockPartPeripheral{
	OilTankTileEntity master;
	public OilTankPeripheral(OilTankTileEntity oiltank){
		super(oiltank);
		this.master = oiltank.master();
	}
	
	@Override
	public String getType(){
		return "ip_oiltank";
	}
	
	@LuaFunction
	public final Map<String, Object> getFluid(){
		return CCTUtils.fluidToMap(this.master.tank.getFluid());
	}
	
	@LuaFunction
	public final int getTankSize(){
		return this.master.tank.getCapacity();
	}
}
