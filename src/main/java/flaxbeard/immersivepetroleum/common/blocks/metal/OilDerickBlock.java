package flaxbeard.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilDerickTileEntity;

public class OilDerickBlock extends IPMetalMultiblock<OilDerickTileEntity>{
	public OilDerickBlock(){
		super("oilderick", () -> IPTileTypes.DERICK.get());
	}
}
