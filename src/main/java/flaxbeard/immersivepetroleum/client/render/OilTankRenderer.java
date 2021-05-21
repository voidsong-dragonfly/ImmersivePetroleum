package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class OilTankRenderer extends TileEntityRenderer<OilTankTileEntity>{
	public OilTankRenderer(TileEntityRendererDispatcher rendererDispatcherIn){
		super(rendererDispatcherIn);
	}
	
	@Override
	public boolean isGlobalRenderer(OilTankTileEntity te){
		return true;
	}
	
	@Override
	public void render(OilTankTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
	}
}
