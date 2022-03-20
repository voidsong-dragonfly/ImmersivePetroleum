package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class PumpjackMultiblock extends IPTemplateMultiblock{
	public static final PumpjackMultiblock INSTANCE = new PumpjackMultiblock();
	
	private PumpjackMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/pumpjack"),
				new BlockPos(1, 0, 0), new BlockPos(1, 1, 4), new BlockPos(3, 4, 6), IPContent.Multiblock.PUMPJACK);
	}
	
	@Override
	public float getManualScale(){
		return 12;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer){
		consumer.accept(new PumpjackClientData());
	}
}
