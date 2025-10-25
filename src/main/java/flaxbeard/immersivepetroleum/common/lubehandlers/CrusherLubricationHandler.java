package flaxbeard.immersivepetroleum.common.lubehandlers;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CrusherLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.ILubricationHandler;
import flaxbeard.immersivepetroleum.client.model.IPModel;
import flaxbeard.immersivepetroleum.client.model.IPModels;
import flaxbeard.immersivepetroleum.client.model.ModelLubricantPipes;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class CrusherLubricationHandler implements ILubricationHandler<IMultiblockBEHelper<CrusherLogic.State>, CrusherLogic.State>{
	private static final Vec3i size = new Vec3i(3, 3, 5);
	
	@Override
	public Vec3i getStructureDimensions(){
		return size;
	}
	
	@Override
	public boolean isMachineEnabled(Level world, IMultiblockBEHelper<CrusherLogic.State> mbte){
		return mbte.getState().shouldRenderActive();
	}
	
	@Override
	public BlockEntity isPlacedCorrectly(Level world, AutoLubricatorTileEntity lubricator, Direction facing){
		final BlockPos target = lubricator.getBlockPos().relative(facing);
		
		MultiblockBlockEntityMaster<?> mbMaster = getMultiblockMaster(world, target);
		if(mbMaster != null && mbMaster.getHelper().getContext().getLevel().getOrientation().front().getOpposite() == facing){
			return mbMaster;
		}
		
		return null;
	}
	
	@Override
	public void lubricateClient(ClientLevel world, Fluid lubricant, int ticks, IMultiblockBEHelper<CrusherLogic.State> mbte){
		if(mbte.getState().shouldRenderActive()){
			@SuppressWarnings("unchecked")
			IClientTickableComponent<CrusherLogic.State> tick = (IClientTickableComponent<CrusherLogic.State>) mbte.getMultiblock().logic();
			tick.tickClient(mbte.getContext());
			//mbte.getState().getBarrelAngle() += 4.5f;
			//mbte.animation_barrelRotation %= 360f;
		}
	}
	
	@Override
	public void lubricateServer(ServerLevel world, Fluid lubricant, int ticks, IMultiblockBEHelper<CrusherLogic.State> mbte){
		if(ticks % 4 == 0){
			@SuppressWarnings("unchecked")
			IServerTickableComponent<CrusherLogic.State> tick = (IServerTickableComponent<CrusherLogic.State>) mbte.getMultiblock().logic();
			tick.tickServer(mbte.getContext());
		}
	}
	
	@Override
	public void spawnLubricantParticles(ClientLevel world, AutoLubricatorTileEntity lubricator, Direction facing, IMultiblockBEHelper<CrusherLogic.State> mbte){
		boolean mirrored = mbte.getContext().getLevel().getOrientation().mirrored();
		Direction f = mirrored ? facing : facing.getOpposite();
		
		float location = world.random.nextFloat();
		
		boolean flip = f.getAxis() == Axis.Z ^ facing.getAxisDirection() == AxisDirection.NEGATIVE ^ !mirrored;
		float xO = 2.5F;
		float zO = -0.1F;
		float yO = 1.3F;
		
		if(location > .5F){
			xO = 1.0F;
			yO = 3.0F;
			zO = 1.65F;
		}
		
		if(facing.getAxisDirection() == AxisDirection.NEGATIVE)
			xO = -xO + 1;
		if(!flip)
			zO = -zO + 1;
		
		float x = lubricator.getBlockPos().getX() + (f.getAxis() == Axis.X ? xO : zO);
		float y = lubricator.getBlockPos().getY() + yO;
		float z = lubricator.getBlockPos().getZ() + (f.getAxis() == Axis.X ? zO : xO);
		
		for(int i = 0;i < 3;i++){
			float r1 = (world.random.nextFloat() - .5F) * 2F;
			float r2 = (world.random.nextFloat() - .5F) * 2F;
			float r3 = world.random.nextFloat();
			
			world.addParticle(ParticleTypes.FALLING_HONEY, x, y, z, r1 * 0.04F, r3 * 0.0125F, r2 * 0.025F);
		}
	}
	
	@Override
	public GhostInfo getGhostBlockPosition(Level world, IMultiblockBEHelper<CrusherLogic.State> mbte){
		if(mbte.getContext() == null)
			return null;
		
		IMultiblockLevel level = mbte.getContext().getLevel();
		
		BlockPos position = level.toAbsolute(new BlockPos(2, 0, -1));
		Direction facing = level.getOrientation().front().getOpposite();
		return new GhostInfo(position, facing);
	}
	
	private static final ResourceLocation TEXTURE = ResourceUtils.ip("textures/models/lube_pipe.png");
	private static Supplier<IPModel> pipes;
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderPipes(AutoLubricatorTileEntity lubricator, IMultiblockBEHelper<CrusherLogic.State> mbte, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		if(mbte.getContext() == null)
			return;
		
		final Direction rotation = mbte.getContext().getLevel().getOrientation().front();
		if(rotation == Direction.NORTH){
			matrix.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90F));
			matrix.translate(-3, 0, 0);
			
		}else if(rotation == Direction.SOUTH){
			matrix.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270F));
			matrix.translate(-2, 0, -1);
			
		}else if(rotation == Direction.EAST){
			matrix.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0F));
			matrix.translate(-2, 0, 0);
			
		}else if(rotation == Direction.WEST){
			matrix.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180F));
			matrix.translate(-3, 0, -1);
		}
		
		if(pipes == null)
			pipes = IPModels.getSupplier(ModelLubricantPipes.Crusher.ID);
		
		IPModel model;
		if((model = pipes.get()) != null){
			model.renderToBuffer(matrix, buffer.getBuffer(model.renderType(TEXTURE)), combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
