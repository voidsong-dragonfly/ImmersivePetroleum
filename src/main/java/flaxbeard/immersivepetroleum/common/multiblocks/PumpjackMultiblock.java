package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

public class PumpjackMultiblock extends IETemplateMultiblock{
	private static final Random RAND = new Random();
	public static final PumpjackMultiblock INSTANCE = new PumpjackMultiblock();
	
	private PumpjackMultiblock(){
		super(new ResourceLocation(ImmersivePetroleum.MODID, "multiblocks/pumpjack"),
				new BlockPos(1, 0, 0), new BlockPos(1, 1, 4), new BlockPos(3, 4, 6), () -> IPContent.Multiblock.pumpjack.defaultBlockState());
	}
	
	@Override
	public float getManualScale(){
		return 12;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean canRenderFormedStructure(){
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	private PumpjackTileEntity te;
	@OnlyIn(Dist.CLIENT)
	List<BakedQuad> list;
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderFormedStructure(PoseStack transform, MultiBufferSource buffer){
		if(this.te == null){
			this.te = new PumpjackTileEntity();
			this.te.setOverrideState(IPContent.Multiblock.pumpjack.defaultBlockState().setValue(IEProperties.FACING_HORIZONTAL, Direction.NORTH));
		}
		
		if(this.list == null){
			BlockState state = IPContent.Multiblock.pumpjack.defaultBlockState().setValue(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
			BakedModel model = ClientUtils.mc().getBlockRenderer().getBlockModel(state);
			this.list = model.getQuads(state, null, RAND, EmptyModelData.INSTANCE);
		}
		
		if(this.list != null && this.list.size() > 0){
			Level world = ClientUtils.mc().level;
			if(world != null){
				transform.pushPose();
				transform.translate(1, 0, 0);
				RenderUtils.renderModelTESRFast(this.list, buffer.getBuffer(RenderType.solid()), transform, 0xF000F0, OverlayTexture.NO_OVERLAY);
				
				transform.pushPose();
				transform.mulPose(rot);
				transform.translate(-2, -1, -1);
				ImmersivePetroleum.proxy.renderTile(this.te, buffer.getBuffer(RenderType.solid()), transform, buffer);
				transform.popPose();
				
				transform.popPose();
			}
		}
	}
	
	final Quaternion rot = new Quaternion(new Vector3f(0F, 1F, 0F), 90, true);
}
