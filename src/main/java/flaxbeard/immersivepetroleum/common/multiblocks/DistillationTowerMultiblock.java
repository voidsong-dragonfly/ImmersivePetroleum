package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class DistillationTowerMultiblock extends IPTemplateMultiblock{
	public static final DistillationTowerMultiblock INSTANCE = new DistillationTowerMultiblock();
	
	private DistillationTowerMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/distillationtower"),
				new BlockPos(2, 0, 2), new BlockPos(0, 1, 3), new BlockPos(4, 16, 4), Multiblock.DISTILLATIONTOWER);
	}
	
	@Override
	public float getManualScale(){
		return 6;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer){
		consumer.accept(new IPClientMultiblockProperties(this, 2.5, 0.5, 2.5));
	}
}
