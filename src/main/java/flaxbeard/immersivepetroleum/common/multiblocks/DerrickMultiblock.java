package flaxbeard.immersivepetroleum.common.multiblocks;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class DerrickMultiblock extends IETemplateMultiblock{
	public static final DerrickMultiblock INSTANCE = new DerrickMultiblock();
	
	public DerrickMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/derrick"),
				new BlockPos(2, 0, 2), new BlockPos(2, 1, 4), new BlockPos(5, 17, 5), () -> IPContent.Multiblock.derrick.defaultBlockState());
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
	public void renderFormedStructure(PoseStack matrix, MultiBufferSource buffer){
	}
}
