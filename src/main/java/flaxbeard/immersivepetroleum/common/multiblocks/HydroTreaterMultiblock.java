package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class HydroTreaterMultiblock extends IPTemplateMultiblock{
	public static final HydroTreaterMultiblock INSTANCE = new HydroTreaterMultiblock();
	
	public HydroTreaterMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/hydrotreater"),
				new BlockPos(1, 0, 2), new BlockPos(1, 1, 3), new BlockPos(3, 3, 4),
				IPContent.Multiblock.HYDROTREATER);
	}
	
	@Override
	public float getManualScale(){
		return 12.0F;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer){
		consumer.accept(new IPClientMultiblockProperties(this, 1.5, 0.5, 2.5));
	}
}
