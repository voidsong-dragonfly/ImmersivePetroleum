package flaxbeard.immersivepetroleum.common.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class OilTankMultiblock extends IPTemplateMultiblock{
	public static final OilTankMultiblock INSTANCE = new OilTankMultiblock();
	
	public OilTankMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/oiltank"),
				new BlockPos(2, 0, 3), new BlockPos(2, 1, 5), new BlockPos(5, 4, 6), IPContent.Multiblock.OILTANK);
	}

	@Override
	public float getManualScale(){
		return 12;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer){
		consumer.accept(new IPClientMultiblockProperties(this));
	}
}
