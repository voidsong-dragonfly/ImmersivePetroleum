package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.UUID;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.common.items.BulletItem;
import blusunrize.immersiveengineering.common.util.IESounds;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.fluids.FluidStack;

/**
 * TODO Highly Experimental. Name not final. Function not final.
 * 
 * @author TwistedGate
 */
public class SeismicSurveyTileEntity extends IPTileEntityBase implements IPServerTickableTile, IPClientTickableTile{
	
	public static final int SCAN_RADIUS = 32;
	public static final int SCAN_SIZE = SCAN_RADIUS * 2 + 1;
	private static final int SCAN_RADIUS_SQR = SCAN_RADIUS * SCAN_RADIUS;
	
	public static final int DELAY = 10;
	
	/** Used for recoil animation. Also prevents ejecting the shell for the duration. */
	public int timer = 0;
	
	@Nonnull
	public ItemStack stack = ItemStack.EMPTY;
	public boolean isSlave;
	public SeismicSurveyTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(IPTileTypes.SEISMIC_SURVEY.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	protected void writeCustom(CompoundTag tag){
		tag.putBoolean("slave", this.isSlave);
		tag.putInt("timer", this.timer);
		tag.put("stack", this.stack.serializeNBT());
	}
	
	@Override
	protected void readCustom(CompoundTag tag){
		this.isSlave = tag.getBoolean("slave");
		this.timer = tag.getInt("timer");
		this.stack = ItemStack.of(tag.getCompound("stack"));
	}
	
	@Override
	public void tickClient(){
	}
	
	@Override
	public void tickServer(){
		if(this.timer > 0){
			this.timer--;
			setChanged();
		}
	}
	
	public SeismicSurveyTileEntity master(){
		if(this.isSlave){
			for(int i = 1;i < 3;i++){
				BlockEntity te = this.level.getBlockEntity(getBlockPos().offset(0, -i, 0));
				if(te instanceof SeismicSurveyTileEntity seis && !seis.isSlave){
					return seis;
				}
			}
		}
		return this;
	}
	
	public boolean interact(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand){
		if(this.timer > 0){
			return false;
		}
		
		ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(player.isShiftKeyDown() && !this.stack.isEmpty()){
			if(!world.isClientSide){
				Block.popResource(world, player.blockPosition(), this.stack);
				this.stack = ItemStack.EMPTY;
				this.setChanged();
			}
			
			return true;
		}else if(held.isEmpty()){
			boolean fire = false;
			
			if(!this.stack.isEmpty()){
				if(this.stack.getItem().equals(ExternalModContent.IE_ITEM_BUCKSHOT.get())){
					fire = true;
					
					if(!world.isClientSide){
						this.timer = DELAY;
						this.stack = new ItemStack(ExternalModContent.IE_ITEM_EMPTY_SHELL.get());
						this.setChanged();
					}
					
				}else{
					if(!world.isClientSide){
						Block.popResource(world, player.blockPosition(), this.stack);
						this.stack = ItemStack.EMPTY;
						this.setChanged();
						
						final double bX = (pos.getX() + 0.5);
						final double bY = (pos.getY() + 0.5);
						final double bZ = (pos.getZ() + 0.5);
						
						world.playSound(null, bX, bY, bZ, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 0.25F);
						world.playSound(null, bX, bY, bZ, SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE, SoundSource.BLOCKS, 0.25F, 0.1F);
					}
					
					return true;
				}
			}
			
			// Boom Effect
			if(fire){
				if(world.isClientSide){
					if(!player.isCreative()){
						double dst = Math.sqrt(player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
						if(dst < 4.0){
							double scale = 1.0 - Mth.clamp(dst / 3D, 0.0, 1.0);
							
							player.hurtDuration = 40;
							player.hurtTime = 40 + (int) (30 * scale);
							player.hurtDir = Math.random() < 0.5 ? 180F : 0F;
						}
					}
					
					final double bX = (pos.getX() + 0.5);
					final double bY = (pos.getY() + 0.25);
					final double bZ = (pos.getZ() + 0.5);
					
					double hSpeed = 0.05;
					for(float i = 0;i < 360;i += 11.25F){
						double xa = Math.sin(Math.toRadians(i));
						double za = Math.cos(Math.toRadians(i));
						
						xa *= 0.75;
						za *= 0.75;
						
						world.addParticle(Math.random() < 0.5 ? ParticleTypes.SMOKE : ParticleTypes.LARGE_SMOKE, bX + xa, bY, bZ + za, hSpeed * xa, 0, hSpeed * za);
					}
				}else{
					SoundEvent sound = ((BulletItem) ExternalModContent.IE_ITEM_BUCKSHOT.get()).getType().getSound();
					if(sound == null){
						sound = IESounds.revolverFire;
					}
					
					final double bX = (pos.getX() + 0.5);
					final double bY = (pos.getY() + 0.5);
					final double bZ = (pos.getZ() + 0.5);
					
					double dst = Math.sqrt(player.distanceToSqr(bX, bY, bZ));
					float volume = (float)(1.0 - Mth.clamp(dst / 3D, 0.0, 0.85));
					
					world.playSound(null, bX, bY, bZ, sound, SoundSource.BLOCKS, volume, 0.5F);
				}
				
				if(!world.isClientSide){
					ReservoirIsland island = ReservoirHandler.getIsland(world, pos);
					
					ItemStack stack = new ItemStack(IPContent.Items.SURVEYRESULT.get());
					
					if(island != null){
						// Give info about the current one.
						
						FluidStack fs = new FluidStack(island.getFluid(), 1);
						
						CompoundTag result = stack.getOrCreateTagElement("islandscan");
						result.putInt("x", pos.getX());
						result.putInt("z", pos.getZ());
						result.putByte("status", (byte) (island.getAmount() / (float) island.getCapacity() * 100));
						result.putLong("amount", island.getAmount());
						result.putString("fluid", fs.getTranslationKey());
						
						if(fs.getFluid().equals(IPContent.Fluids.CRUDEOIL.get())){
							Utils.unlockIPAdvancement(player, "main/root");
						}
						
					}else{
						// Find one nearby instead.
						
						double sqrt2048 = Math.sqrt(SCAN_RADIUS_SQR * 2);
						byte[] mapData = new byte[SCAN_SIZE * SCAN_SIZE];
						for(int j = -SCAN_RADIUS, a = 0;j <= SCAN_RADIUS;j++,a++){
							for(int i = -SCAN_RADIUS, b = 0;i <= SCAN_RADIUS;i++,b++){
								int x = pos.getX() - i;
								int z = pos.getZ() - j;
								
								int data;
								double current = ReservoirHandler.noiseFor(world, x, z);
								if(current == -1){
									data = 0;
								}else{
									data = (int) Mth.clamp(255 * current, 0, 255);
								}
								
								int noise = 31 + (int) (127 * Math.random());
								
								double blend = Math.sqrt(i * i + j * j) / sqrt2048;
								int lerped = (int)(Mth.clampedLerp(data, noise, blend));
								mapData[(a * SCAN_SIZE) + b] = (byte)(lerped & 0xFF);
							}
						}
						
						CompoundTag result = stack.getOrCreateTagElement("surveyscan");
						result.putUUID("uuid", UUID.randomUUID());
						result.putInt("x", pos.getX());
						result.putInt("z", pos.getZ());
						result.putByteArray("map", mapData);
					}
					
					Block.popResource(world, player.blockPosition(), stack);
				}
				
				return true;
			}
			
			return false;
		}else if(held.getItem().equals(ExternalModContent.IE_ITEM_BUCKSHOT.get())){
			if(this.stack.isEmpty()){
				if(!world.isClientSide){
					ItemStack copy = held.copy();
					copy.setCount(1);
					this.stack = copy;
					
					if(!player.isCreative()){
						held.shrink(1);
						if(held.isEmpty()){
							player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
						}
					}
					
					this.setChanged();
				}
				
				return true;
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
