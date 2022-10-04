package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks;

import java.util.Map;

import dan200.computercraft.api.lua.LuaFunction;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.CCTUtils;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.generic.MultiblockPartPeripheral;

public class OilTankPeripheral extends MultiblockPartPeripheral{
	private static final String TYPE = ResourceUtils.ip("oiltank").toString();
	
	OilTankTileEntity master;
	public OilTankPeripheral(OilTankTileEntity oiltank){
		super(oiltank);
		this.master = oiltank.master();
	}
	
	@LuaFunction
	public final Map<String, Object> getFluid(){
		return CCTUtils.fluidToMap(this.master.tank.getFluid());
	}
	
	@LuaFunction
	public final int getTankSize(){
		return this.master.tank.getCapacity();
	}
	
	@Override
	public String getType(){
		return TYPE;
	}
}
