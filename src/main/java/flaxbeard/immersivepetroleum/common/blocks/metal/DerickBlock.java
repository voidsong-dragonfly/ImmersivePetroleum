package flaxbeard.immersivepetroleum.common.blocks.metal;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPMetalMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;

public class DerickBlock extends IPMetalMultiblock<DerrickTileEntity>{
	public DerickBlock(){
		super("derrick", () -> IPTileTypes.DERRICK.get());
	}
}
