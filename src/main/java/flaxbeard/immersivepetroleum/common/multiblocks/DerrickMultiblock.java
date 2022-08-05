package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.BlockPos;

public class DerrickMultiblock extends IPTemplateMultiblock{
	public static final DerrickMultiblock INSTANCE = new DerrickMultiblock();
	
	public DerrickMultiblock(){
		super(ResourceUtils.ip("multiblocks/derrick"),
				new BlockPos(2, 0, 2), new BlockPos(2, 1, 4), new BlockPos(5, 17, 5), IPContent.Multiblock.DERRICK);
	}
	
	@Override
	public float getManualScale(){
		return 6.0F;
	}
	
	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer){
		consumer.accept(new IPClientMultiblockProperties(this, 2.5, 0.5, 2.5));
	}
}
