package flaxbeard.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;

public class OilTankBlock extends IPMetalMultiblock<OilTankTileEntity>{
	public OilTankBlock(){
		super("oiltank", () -> IPTileTypes.OILTANK.get());
	}
}
