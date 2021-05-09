package flaxbeard.immersivepetroleum.common.multiblocks;

import com.mojang.blaze3d.matrix.MatrixStack;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class OilDerickMultiblock extends IETemplateMultiblock{
	public static final OilDerickMultiblock INSTANCE = new OilDerickMultiblock();
	
	public OilDerickMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/oilderick"),
				new BlockPos(1, 0, 1), new BlockPos(1, 1, 0), new BlockPos(3, 4, 3), () -> IPContent.Multiblock.oilderick.getDefaultState());
	}
	
	@Override
	public float getManualScale(){
		return 12.0F;
	}
	
	@Override
	public boolean canRenderFormedStructure(){
		return false;
	}
	
	@Override
	public void renderFormedStructure(MatrixStack matrix, IRenderTypeBuffer buffer){
	}
}
