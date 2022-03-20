package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class DerrickMultiblock extends IPTemplateMultiblock{
	public static final DerrickMultiblock INSTANCE = new DerrickMultiblock();
	
	public DerrickMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/derrick"),
				new BlockPos(2, 0, 2), new BlockPos(2, 1, 4), new BlockPos(5, 17, 5),
				IPContent.Multiblock.DERRICK);
	}
	
	@Override
	public float getManualScale(){
		return 6.0F;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer){
		consumer.accept(new IPClientMultiblockProperties(this));
	}
}
