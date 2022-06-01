package flaxbeard.immersivepetroleum.common.lubehandlers;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.ILubricationHandler;
import flaxbeard.immersivepetroleum.client.model.IPModel;
import flaxbeard.immersivepetroleum.client.model.IPModels;
import flaxbeard.immersivepetroleum.client.model.ModelLubricantPipes;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PumpjackLubricationHandler implements ILubricationHandler<PumpjackTileEntity>{
	private static Vec3i size = new Vec3i(4, 6, 3);
	
	@Override
	public Vec3i getStructureDimensions(){
		return size;
	}
	
	@Override
	public boolean isMachineEnabled(Level world, PumpjackTileEntity mbte){
		return mbte.wasActive;
	}
	
	@Override
	public BlockEntity isPlacedCorrectly(Level world, AutoLubricatorTileEntity lubricator, Direction facing){
		BlockPos target = lubricator.getBlockPos().relative(facing);
		BlockEntity te = world.getBlockEntity(target);
		
		if(te instanceof PumpjackTileEntity){
			PumpjackTileEntity master = ((PumpjackTileEntity) te).master();
			if(master != null){
				Direction f = master.getIsMirrored() ? facing : facing.getOpposite();
				if(master.getFacing().getClockWise() == f){
					return master;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public void lubricateClient(ClientLevel world, int ticks, PumpjackTileEntity mbte){
		mbte.activeTicks += 1F / 4F;
	}
	
	@Override
	public void lubricateServer(ServerLevel world, int ticks, PumpjackTileEntity mbte){
		if(ticks % 4 == 0){
			mbte.tickServer();
		}
	}
	
	@Override
	public void spawnLubricantParticles(ClientLevel world, AutoLubricatorTileEntity lubricator, Direction facing, PumpjackTileEntity mbte){
		Direction f = mbte.getIsMirrored() ? facing : facing.getOpposite();
		float location = world.random.nextFloat();
		
		boolean flip = f.getAxis() == Axis.Z ^ facing.getAxisDirection() == AxisDirection.POSITIVE ^ !mbte.getIsMirrored();
		float xO = 2.5F;
		float zO = -.15F;
		float yO = 2.25F;
		
		if(location > .5F){
			xO = 1.7F;
			yO = 2.9F;
			zO = -1.5F;
			
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
			//BlockState n = Fluids.lubricant.block.getDefaultState();
			//world.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, n), x, y, z, r1 * 0.04F, r3 * 0.0125F, r2 * 0.025F);
			
			// Because making your own particles is so convoluted and confusing
			world.addParticle(ParticleTypes.FALLING_HONEY, x, y, z, r1 * 0.04F, r3 * 0.0125F, r2 * 0.025F);
		}
	}
	
	@Override
	public Tuple<BlockPos, Direction> getGhostBlockPosition(Level world, PumpjackTileEntity mbte){
		if(!mbte.isDummy()){
			Direction mbFacing = mbte.getFacing().getOpposite();
			BlockPos pos = mbte.getBlockPos()
					.relative(Direction.UP)
					.relative(mbFacing, 4)
					.relative(mbte.getIsMirrored() ? mbFacing.getClockWise() : mbFacing.getCounterClockWise(), 2);
			
			Direction f = (mbte.getIsMirrored() ? mbte.getFacing().getOpposite() : mbte.getFacing()).getCounterClockWise();
			return new Tuple<BlockPos, Direction>(pos, f);
		}
		return null;
	}
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(ImmersivePetroleum.MODID, "textures/models/lube_pipe.png");
	
	@OnlyIn(Dist.CLIENT)
	private static Supplier<IPModel> pipes_normal;
	
	@OnlyIn(Dist.CLIENT)
	private static Supplier<IPModel> pipes_mirrored;
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderPipes(AutoLubricatorTileEntity lubricator, PumpjackTileEntity mbte, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		matrix.translate(0, -1, 0);
		Vec3i offset = mbte.getBlockPos().subtract(lubricator.getBlockPos());
		matrix.translate(offset.getX(), offset.getY(), offset.getZ());
		
		Direction rotation = mbte.getFacing();
		switch(rotation){
			case NORTH:{
				matrix.mulPose(new Quaternion(0, 90F, 0, true));
				matrix.translate(-6, 1, -1);
				break;
			}
			case SOUTH:{
				matrix.mulPose(new Quaternion(0, 270F, 0, true));
				matrix.translate(-5, 1, -2);
				break;
			}
			case EAST:{
				matrix.translate(-5, 1, -1);
				break;
			}
			case WEST:{
				matrix.mulPose(new Quaternion(0, 180F, 0, true));
				matrix.translate(-6, 1, -2);
				break;
			}
			default:
				break;
		}
		
		IPModel model;
		if(mbte.getIsMirrored()){
			if(pipes_mirrored == null)
				pipes_mirrored = IPModels.getSupplier(ModelLubricantPipes.Pumpjack.ID_MIRRORED);
			
			model = pipes_mirrored.get();
		}else{
			if(pipes_normal == null)
				pipes_normal = IPModels.getSupplier(ModelLubricantPipes.Pumpjack.ID_NORMAL);
			
			model = pipes_normal.get();
		}
		
		if(model != null){
			model.renderToBuffer(matrix, buffer.getBuffer(model.renderType(TEXTURE)), combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
