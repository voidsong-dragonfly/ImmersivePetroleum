package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class DerrickRenderer extends TileEntityRenderer<DerrickTileEntity>{
	public DerrickRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}
	
	@Override
	public boolean isGlobalRenderer(DerrickTileEntity te){
		return true;
	}

	@Override
	public void render(DerrickTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn){
		if(te == null || te.isDummy()){
			return;
		}
		
		/*
		BlockState state = te.getBlockState();
		ResourceLocation modelLocation = new ResourceLocation(ImmersivePetroleum.MODID, "models/multiblock/obj/derrick_pipe.obj");
		
		IBakedModel model = ModelLoader.instance().getBakedModel(modelLocation, ModelRotation.X0_Y0, ModelLoader.instance().getSpriteMap()::getSprite);
		
		List<BakedQuad> quads = model.getQuads(state, null, new Random(), EmptyModelData.INSTANCE);
		
		matrix.push();
		{
			if(te.drilling){
				// Speeen
			}
			
			IVertexBuilder builder = bufferIn.getBuffer(RenderType.getSolid());
			MatrixStack.Entry last = matrix.getLast();
			
			quads.forEach(quad -> builder.addQuad(last, quad, 1.0F, 1.0F, 1.0F, combinedLightIn, combinedOverlayIn));
			
		}
		matrix.pop();
		*/
	}
}
