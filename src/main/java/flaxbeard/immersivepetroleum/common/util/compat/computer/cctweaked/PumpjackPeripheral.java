package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;

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
