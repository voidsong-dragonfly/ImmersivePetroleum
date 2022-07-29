package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.Arrays;
import java.util.List;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import flaxbeard.immersivepetroleum.api.crafting.LubricantHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.ILubricationHandler;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.wooden.AutoLubricatorBlock;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class AutoLubricatorTileEntity extends IPTileEntityBase implements IPServerTickableTile, IPClientTickableTile, IEBlockInterfaces.IPlayerInteraction, IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IBlockEntityDrop, IEBlockInterfaces.IReadOnPlacement{
	public boolean isSlave;
//	public boolean predictablyDraining = false;
	public Direction facing = Direction.NORTH;
	public FluidTank tank = new FluidTank(8000, fluid -> (fluid != null && LubricantHandler.isValidLube(fluid.getFluid())));
	
	public AutoLubricatorTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(IPTileTypes.AUTOLUBE.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	protected void readCustom(CompoundTag compound){
		this.isSlave = compound.getBoolean("slave");
//		this.predictablyDraining = compound.getBoolean("predictablyDraining");
		
		Direction facing = Direction.byName(compound.getString("facing"));
		if(facing.get2DDataValue() == -1)
			this.facing = Direction.NORTH;
		this.facing = facing;
		
		this.tank.readFromNBT(compound.getCompound("tank"));
	}
	
	@Override
	protected void writeCustom(CompoundTag compound){
		compound.putBoolean("slave", this.isSlave);
//		compound.putBoolean("predictablyDraining", this.predictablyDraining);
		compound.putString("facing", this.facing.getName());
		compound.putInt("count", this.count);
		
		CompoundTag tank = this.tank.writeToNBT(new CompoundTag());
		compound.put("tank", tank);
	}
	
	public void readTank(CompoundTag nbt){
		this.tank.readFromNBT(nbt.getCompound("tank"));
	}
	
	public void writeTank(CompoundTag nbt, boolean toItem){
		boolean write = this.tank.getFluidAmount() > 0;
		CompoundTag tankTag = this.tank.writeToNBT(new CompoundTag());
		if(!toItem || write)
			nbt.put("tank", tankTag);
	}
	
	@Override
	public void readOnPlacement(LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			readTank(stack.getTag());
		}
		
		if(placer instanceof Player player){
			BlockPos target = this.worldPosition.relative(this.facing);
			BlockEntity te = this.level.getBlockEntity(target);
			
			ILubricationHandler<BlockEntity> handler = LubricatedHandler.getHandlerForTile(te);
			if(handler != null && handler.isPlacedCorrectly(this.level, this, this.facing) != null){
				Utils.unlockIPAdvancement(player, "main/auto_lubricator");
			}
		}
	}
	
	@Override
	public List<ItemStack> getBlockEntityDrop(LootContext context){
		BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
		if(state.getValue(AutoLubricatorBlock.SLAVE)){
			return Arrays.asList(ItemStack.EMPTY);
		}
		
		ItemStack stack = new ItemStack(state.getBlock());
		
		BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		if(te instanceof AutoLubricatorTileEntity autolube){
			CompoundTag tag = new CompoundTag();
			autolube.writeTank(tag, true);
			if(!tag.isEmpty()){
				stack.setTag(tag);
			}
		}
		
		return Arrays.asList(stack);
	}
	
	private LazyOptional<IFluidHandler> outputHandler;
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && this.isSlave && (side == null || side == Direction.UP)){
			if(this.outputHandler == null){
				this.outputHandler = LazyOptional.of(() -> {
					BlockEntity te = this.level.getBlockEntity(getBlockPos().relative(Direction.DOWN));
					if(te != null && te instanceof AutoLubricatorTileEntity autolube){
						return autolube.tank;
					}
					return null;
				});
			}
			return this.outputHandler.cast();
		}
		
		return super.getCapability(cap, side);
	}
	
	@Override
	public void setRemoved(){
		super.setRemoved();
		if(this.outputHandler != null)
			this.outputHandler.invalidate();
	}
	
	@Override
	public void setChanged(){
		super.setChanged();
		
		BlockState state = this.level.getBlockState(this.worldPosition);
		this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
		this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
	}
	
	@Override
	public void invalidateCaps(){
		super.invalidateCaps();
		if(this.outputHandler != null)
			this.outputHandler.invalidate();
	}
	
	public Direction getFacing(){
		return this.facing;
	}
	
	public boolean isMaster(){
		return !this.isSlave;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public AABB getRenderBoundingBox(){
		BlockPos pos = getBlockPos();
		return new AABB(pos.offset(-3, -3, -3), pos.offset(3, 3, 3));
	}
	
	@Override
	public Component[] getOverlayText(Player player, HitResult mop, boolean hammer){
		if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND))){
			BlockEntity master = this.level.getBlockEntity(getBlockPos().offset(0, this.isSlave ? -1 : 0, 0));
			if(master != null && master instanceof AutoLubricatorTileEntity autolube){
				Component s = null;
				if(!autolube.tank.isEmpty()){
					s = ((MutableComponent) autolube.tank.getFluid().getDisplayName()).append(": " + autolube.tank.getFluidAmount() + "mB");
				}else{
					s = new TranslatableComponent(Lib.GUI + "empty");
				}
				return new Component[]{s};
			}
		}
		return null;
	}
	
	@Override
	public boolean useNixieFont(Player player, HitResult mop){
		return false;
	}
	
	@Override
	public boolean interact(Direction side, Player player, InteractionHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ){
		BlockEntity master = this.isSlave ? this.level.getBlockEntity(getBlockPos().offset(0, -1, 0)) : this;
		if(master != null && master instanceof AutoLubricatorTileEntity autolube){
			if(!this.level.isClientSide && FluidUtil.interactWithFluidHandler(player, hand, autolube.tank)){
				setChanged();
			}
			return true;
		}
		return false;
	}
	
	int count = 0;
	int lastTank = 0;
	int lastTankUpdate = 0;
	int countClient = 0;
	
	@Override
	public void tickClient(){
		if(this.isSlave){
			return;
		}
		
		if(!this.tank.isEmpty() && LubricantHandler.isValidLube(this.tank.getFluid()) && this.tank.getFluidAmount() >= LubricantHandler.getLubeAmount(this.tank.getFluid())){
			BlockPos target = this.worldPosition.relative(this.facing);
			BlockEntity te = this.level.getBlockEntity(target);
			
			ILubricationHandler<BlockEntity> handler = LubricatedHandler.getHandlerForTile(te);
			if(handler != null){
				BlockEntity master = handler.isPlacedCorrectly(this.level, this, this.facing);
				if(master != null && handler.isMachineEnabled(this.level, master)){
					handler.lubricateClient((ClientLevel) this.level, this.tank.getFluid().getFluid(), this.count, master);
					
					if(this.countClient++ % 50 == 0){
						this.countClient = this.level.random.nextInt(40);
						handler.spawnLubricantParticles((ClientLevel) this.level, this, this.facing, master);
					}
				}
			}
		}
	}
	
	@Override
	public void tickServer(){
		if(this.isSlave){
			return;
		}
		
		if(!this.tank.isEmpty() && LubricantHandler.isValidLube(this.tank.getFluid()) && this.tank.getFluidAmount() >= LubricantHandler.getLubeAmount(this.tank.getFluid())){
			BlockPos target = this.worldPosition.relative(this.facing);
			BlockEntity te = this.level.getBlockEntity(target);
			
			ILubricationHandler<BlockEntity> handler = LubricatedHandler.getHandlerForTile(te);
			if(handler != null){
				BlockEntity master = handler.isPlacedCorrectly(this.level, this, this.facing);
				if(master != null && handler.isMachineEnabled(this.level, master)){
					handler.lubricateServer((ServerLevel) this.level, this.tank.getFluid().getFluid(), this.count, master);
					
					if(this.count++ % 4 == 0){
						this.tank.drain(LubricantHandler.getLubeAmount(this.tank.getFluid()), FluidAction.EXECUTE);
					}
					
					setChanged();
				}
			}
		}
		
		/*// Unused
		if(!this.level.isClientSide && this.lastTank != this.tank.getFluidAmount()){
			if(this.predictablyDraining && !this.tank.isEmpty() && this.lastTank - this.tank.getFluidAmount() == LubricantHandler.getLubeAmount(this.tank.getFluid().getFluid())){
				this.lastTank = this.tank.getFluidAmount();
			}
			
			if(Math.abs(this.lastTankUpdate - this.tank.getFluidAmount()) > 25){
				this.predictablyDraining = !this.tank.isEmpty() && this.lastTank - this.tank.getFluidAmount() == LubricantHandler.getLubeAmount(this.tank.getFluid().getFluid());
				this.lastTankUpdate = this.tank.getFluidAmount();
			}
			setChanged();
		}
		*/
	}
}
