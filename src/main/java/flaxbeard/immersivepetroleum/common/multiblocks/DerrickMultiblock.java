package flaxbeard.immersivepetroleum.common.multiblocks;

import com.mojang.blaze3d.matrix.MatrixStack;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class DerrickMultiblock extends IETemplateMultiblock{
	public static final DerrickMultiblock INSTANCE = new DerrickMultiblock();
	
	public DerrickMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/derick"),
				new BlockPos(2, 0, 2), new BlockPos(2, 1, 4), new BlockPos(5, 17, 5), () -> IPContent.Multiblock.derrick.getDefaultState());
	}
	
	@Override
	public float getManualScale(){
		return 6.0F;
	}
	
	@Override
	public boolean canRenderFormedStructure(){
		return false;
	}
	
	@Override
	public void renderFormedStructure(MatrixStack matrix, IRenderTypeBuffer buffer){
	}
}
