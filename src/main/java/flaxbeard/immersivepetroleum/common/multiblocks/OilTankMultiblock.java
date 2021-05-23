package flaxbeard.immersivepetroleum.common.multiblocks;

import com.mojang.blaze3d.matrix.MatrixStack;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class OilTankMultiblock extends IETemplateMultiblock{
	public static final OilTankMultiblock INSTANCE = new OilTankMultiblock();
	
	public OilTankMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/oiltank"),
				new BlockPos(2, 0, 3), new BlockPos(2, 1, 5), new BlockPos(5, 4, 6), () -> IPContent.Multiblock.oiltank.getDefaultState());
	}

	@Override
	public float getManualScale(){
		return 12;
	}

	@Override
	public boolean canRenderFormedStructure(){
		return false;
	}

	@Override
	public void renderFormedStructure(MatrixStack transform, IRenderTypeBuffer buffer){
	}
}
