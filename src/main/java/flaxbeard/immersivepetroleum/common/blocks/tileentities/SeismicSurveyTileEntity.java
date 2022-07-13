package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.common.items.BulletItem;
import blusunrize.immersiveengineering.common.util.IESounds;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * TODO Highly Experimental. Name not final. Function not final.
 * 
 * @author TwistedGate
 */
public class SeismicSurveyTileEntity extends IPTileEntityBase{
	
	@Nonnull
	public ItemStack stack = ItemStack.EMPTY;
	public boolean isSlave;
	public SeismicSurveyTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(IPTileTypes.SEISMIC_SURVEY.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	protected void writeCustom(CompoundTag compound){
		compound.putBoolean("slave", this.isSlave);
		compound.put("stack", this.stack.serializeNBT());
	}
	
	@Override
	protected void readCustom(CompoundTag compound){
		this.isSlave = compound.getBoolean("slave");
		this.stack = ItemStack.of(compound.getCompound("stack"));
	}
	
	public boolean interact(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand){
		BlockEntity master = this.isSlave ? this.level.getBlockEntity(getBlockPos().offset(0, -1, 0)) : this;
		if(master != null && master instanceof SeismicSurveyTileEntity survey){
			pos = survey.getBlockPos();
			
			ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
			
			final double x = (pos.getX() + 0.5);
			final double y = (pos.getY() + 0.0625);
			final double z = (pos.getZ() + 0.5);
			
			if(player.isShiftKeyDown() && !survey.stack.isEmpty()){
				if(!world.isClientSide){
					Block.popResourceFromFace(world, pos, Direction.UP, survey.stack);
					survey.stack = ItemStack.EMPTY;
					survey.setChanged();
				}
				
				return true;
			}else if(held.isEmpty()){
				boolean fire = false;
				
				if(!survey.stack.isEmpty()){
					if(survey.stack.getItem().equals(ExternalModContent.IE_ITEM_BUCKSHOT.get())){
						fire = true;
						if(!world.isClientSide){
							survey.stack = new ItemStack(ExternalModContent.IE_ITEM_EMPTY_SHELL.get());
							survey.setChanged();
						}
						
					}else{
						if(!world.isClientSide){
							Block.popResourceFromFace(world, pos, Direction.UP, survey.stack);
							survey.stack = ItemStack.EMPTY;
							survey.setChanged();
							
							world.playSound(null, x, y, z, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 0.25F);
							world.playSound(null, x, y, z, SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE, SoundSource.BLOCKS, 0.25F, 0.1F);
						}
						
						return true;
					}
				}
				
				// Boom Effect
				if(fire){
					if(world.isClientSide){
						if(!player.isCreative()){
							double dst = Math.sqrt(player.distanceToSqr(x, y, z));
							if(dst < 4.0){
								double scale = 1.0 - Mth.clamp(dst / 3D, 0.0, 1.0);
								
								player.hurtDuration = 40;
								player.hurtTime = 40 + (int) (30 * scale);
								player.hurtDir = Math.random() < 0.5 ? 180F : 0F;
							}
						}
						
						double hSpeed = 0.05;
						for(float i = 0;i < 360;i += 11.25F){
							double xa = Math.sin(Math.toRadians(i));
							double za = Math.cos(Math.toRadians(i));
							
							xa += (0.5 - Math.random()) * 1.5;
							za += (0.5 - Math.random()) * 1.5;
							
							xa *= 0.75;
							za *= 0.75;
							
							world.addParticle(Math.random() < 0.5 ? ParticleTypes.SMOKE : ParticleTypes.LARGE_SMOKE, x + xa, y, z + za, hSpeed * xa, 0, hSpeed * za);
						}
					}else{
						SoundEvent sound = ((BulletItem) ExternalModContent.IE_ITEM_BUCKSHOT.get()).getType().getSound();
						if(sound == null){
							sound = IESounds.revolverFire;
						}
						world.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 1.0F, 0.5F);
					}
					
					// TODO This is the point where it would check for reservoirs and do *something*. Perhaps a map, book or anything else that is able to Display some information.
					
					return true;
				}
				
				return false;
			}else if(held.getItem().equals(ExternalModContent.IE_ITEM_BUCKSHOT.get())){
				if(survey.stack.isEmpty()){
					if(!world.isClientSide){
						ItemStack copy = held.copy();
						copy.setCount(1);
						survey.stack = copy;
						
						if(!player.isCreative()){
							held.shrink(1);
							if(held.isEmpty()){
								player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
							}
						}
						
						survey.setChanged();
					}
					
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void setChanged(){
		super.setChanged();
		
		BlockState state = this.level.getBlockState(this.worldPosition);
		this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
		this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
	}
}
