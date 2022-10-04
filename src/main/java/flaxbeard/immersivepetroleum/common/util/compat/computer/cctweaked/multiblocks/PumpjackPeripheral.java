package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.generic.PoweredMultiblockPeripheral;

public class PumpjackPeripheral extends PoweredMultiblockPeripheral{
	private static final String TYPE = ResourceUtils.ip("pumpjack").toString();
	
	public PumpjackPeripheral(PumpjackTileEntity pumpjack){
		super(pumpjack);
	}
	
	@Override
	public String getType(){
		return TYPE;
	}
}
