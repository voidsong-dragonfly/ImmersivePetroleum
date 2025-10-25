package flaxbeard.immersivepetroleum.api.crafting;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LubricatedHandler{
	public interface ILubricationHandler<E extends IMultiblockBEHelper<B>, B extends IMultiblockState>{
		GhostInfo getGhostBlockPosition(Level world, E mbte);
		
		Vec3i getStructureDimensions();
		
		boolean isMachineEnabled(Level world, E mbte);
		
		BlockEntity isPlacedCorrectly(Level world, AutoLubricatorTileEntity lubricator, Direction direction);
		
		void lubricateClient(ClientLevel world, Fluid lubricant, int ticks, E mbte);
		
		void lubricateServer(ServerLevel world, Fluid lubricant, int ticks, E mbte);
		
		void spawnLubricantParticles(ClientLevel world, AutoLubricatorTileEntity lubricator, Direction direction, E mbte);
		
		@OnlyIn(Dist.CLIENT)
		void renderPipes(AutoLubricatorTileEntity lubricator, E mbte, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay);
		
		@Nullable
		default MultiblockBlockEntityMaster<?> getMultiblockMaster(Level world, BlockPos pos){
			BlockEntity te = world.getBlockEntity(pos);
			
			if(te instanceof IMultiblockBE<?> mbBE){
				IMultiblockBEHelper<?> helper = mbBE.getHelper();
				
				if(!(helper instanceof IMultiblockBEHelperMaster<?>) && helper.getContext() != null){
					BlockPos masterPos = helper.getContext().getLevel().toAbsolute(helper.getMultiblock().masterPosInMB());
					BlockEntity be = world.getBlockEntity(masterPos);
					
					if(be instanceof MultiblockBlockEntityMaster<?> master){
						return master;
					}
				}
			}
			
			return null;
		}
		
		record GhostInfo(BlockPos position, Direction facing){}
	}
	
	static final Map<Class<? extends IMultiblockLogic<? extends IMultiblockState>>, ILubricationHandler<? extends IMultiblockBEHelper<?>, ? extends IMultiblockState>> lubricationHandlers = new HashMap<>();
	
	public static <T extends IMultiblockLogic<B>, B extends IMultiblockState> void registerLubricatedTile(Class<T> tileClass, Supplier<ILubricationHandler<?, B>> handler){
		ILubricationHandler<?, B> instance = handler.get();
		lubricationHandlers.put(tileClass, instance);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IMultiblockBEHelper<B>, B extends IMultiblockState> ILubricationHandler<T, B> getHandlerForTile(T te){
		if(te != null){
			final IMultiblockLogic<B> logic = te.getMultiblock().logic();
			
			ILubricationHandler<?, ?> handler = lubricationHandlers.get(logic.getClass());
			if(handler != null)
				return (ILubricationHandler<T, B>) handler;
		}
		
		return null;
	}
	
	public static class LubricatedTileInfo{
		public BlockPos pos;
		public ResourceKey<Level> world;
		public Fluid lubricant = Fluids.EMPTY;
		public int ticks;
		
		public LubricatedTileInfo(ResourceKey<Level> registryKey, BlockPos pos, Fluid lubricant, int ticks){
			this.world = registryKey;
			this.pos = pos;
			this.ticks = ticks;
			
			if(lubricant != null && lubricant != Fluids.EMPTY){
				this.lubricant = lubricant;
			}
		}
		
		public LubricatedTileInfo(CompoundTag tag){
			int ticks = tag.getInt("ticks");
			int x = tag.getInt("x");
			int y = tag.getInt("y");
			int z = tag.getInt("z");
			String name = tag.getString("world");
			String lubricantName = tag.getString("lubricant");
			
			this.world = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(name));
			this.pos = new BlockPos(x, y, z);
			this.ticks = ticks;
			
			this.lubricant = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(lubricantName));
			if(this.lubricant == null){
				this.lubricant = Fluids.EMPTY;
			}
		}
		
		public CompoundTag writeToNBT(){
			CompoundTag tag = new CompoundTag();
			
			tag.putInt("ticks", this.ticks);
			tag.putInt("x", this.pos.getX());
			tag.putInt("y", this.pos.getY());
			tag.putInt("z", this.pos.getZ());
			tag.putString("world", this.world.location().toString());
			tag.putString("lubricant", ForgeRegistries.FLUIDS.getKey(this.lubricant).toString());
			
			return tag;
		}
	}
	
	public static List<LubricatedTileInfo> lubricatedTiles = new ArrayList<>();
	
	public static boolean lubricateTile(BlockEntity tile, Fluid lubricant, int ticks){
		return lubricateTile(tile, lubricant, ticks, false, -1);
	}
	
	public static boolean lubricateTile(BlockEntity tile, Fluid lubricant, int ticks, boolean additive, int cap){
		if(!(tile instanceof IMultiblockBEHelperMaster<?> master))
			return false;
		
		// ------------------------------------------------------------------------------------------------
		
		// TODO 19.10.2025 - No idea why this is here anymore.
		/* 
		boolean debugDisabled = true;
		if(debugDisabled)
			return false;
		if(tile instanceof MultiblockPartBlockEntity<?> mpte && mpte.offsetToMaster != BlockPos.ZERO){
			tile = mpte.master();
		}
		*/
		
		if(getHandlerForTile(master) != null){
			BlockPos pos = tile.getBlockPos();
			
			ResourceKey<Level> key = tile.getLevel().dimension();
			for(LubricatedTileInfo info: lubricatedTiles){
				if(info.pos.equals(pos) && info.world == key){
					if(info.ticks >= ticks){
						if(additive){
							if(cap == -1){
								info.ticks += ticks;
							}else{
								info.ticks = Math.min(cap, info.ticks + ticks);
							}
							return true;
						}else{
							return false;
						}
					}
					
					info.ticks = ticks;
					return true;
				}
			}
			
			LubricatedTileInfo lti = new LubricatedTileInfo(tile.getLevel().dimension(), tile.getBlockPos(), lubricant, ticks);
			lubricatedTiles.add(lti);
			
			return true;
		}
		
		return false;
	}
	
	public static class LubricantEffect extends ChemthrowerHandler.ChemthrowerEffect{
		@Override
		public void applyToEntity(LivingEntity target, Player shooter, ItemStack thrower, Fluid fluid){
			if(target instanceof IronGolem){
				if(LubricantHandler.isValidLube(fluid)){
					int ticks = (Math.max(1, IEServerConfig.TOOLS.chemthrower_consumption.get() / LubricantHandler.getLubeAmount(fluid)) * 4) / 3;
					
					MobEffectInstance activeSpeed = target.getEffect(MobEffects.MOVEMENT_SPEED);
					int ticksSpeed = ticks;
					if(activeSpeed != null && activeSpeed.getAmplifier() <= 1){
						ticksSpeed = Math.min(activeSpeed.getDuration() + ticks, 1200); // 1 Minute
					}
					
					MobEffectInstance activeStrength = target.getEffect(MobEffects.DAMAGE_BOOST);
					int ticksStrength = ticks;
					if(activeStrength != null && activeStrength.getAmplifier() <= 1){
						ticksStrength = Math.min(activeStrength.getDuration() + ticks, 1200); // 1 Minute
					}
					
					target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ticksSpeed, 1));
					target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticksStrength, 1));
				}
			}
			
		}
		
		@Override
		public void applyToBlock(Level world, HitResult mop, Player shooter, ItemStack thrower, Fluid fluid){
			if(LubricantHandler.isValidLube(fluid)){
				int amount = (Math.max(1, IEServerConfig.TOOLS.chemthrower_consumption.get() / LubricantHandler.getLubeAmount(fluid)) * 2) / 3;
				LubricatedHandler.lubricateTile(world.getBlockEntity(BlockPos.containing(mop.getLocation())), fluid, amount, true, 1200); // 1 Minute
			}
		}
	}
}
