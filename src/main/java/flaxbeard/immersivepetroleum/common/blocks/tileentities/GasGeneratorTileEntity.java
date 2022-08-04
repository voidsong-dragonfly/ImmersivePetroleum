package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.utils.CapabilityUtils;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.impl.ImmersiveConnectableBlockEntity;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import blusunrize.immersiveengineering.api.wires.utils.WireUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.Utils;
import flaxbeard.immersivepetroleum.api.energy.FuelHandler;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import javax.annotation.Nonnull;

public class GasGeneratorTileEntity extends ImmersiveConnectableBlockEntity implements IPServerTickableTile, IPClientTickableTile, IEBlockInterfaces.IDirectionalBE, IEBlockInterfaces.IPlayerInteraction, IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IBlockEntityDrop, IEBlockInterfaces.ISoundBE, EnergyTransferHandler.EnergyConnector{
	public static final int FUEL_CAPACITY = 8000;
	
	protected WireType wireType;
	protected boolean isActive = false;
	protected Direction facing = Direction.NORTH;
	protected final MutableEnergyStorage energyStorage = new MutableEnergyStorage(getMaxStorage(), Integer.MAX_VALUE, getMaxOutput());
	protected final FluidTank tank = new FluidTank(FUEL_CAPACITY, fluid -> (fluid != FluidStack.EMPTY && FuelHandler.isValidFuel(fluid.getFluid())));
	
	public GasGeneratorTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(IPTileTypes.GENERATOR.get(), pWorldPosition, pBlockState);
	}
	
	public int getMaxOutput(){
		return IEServerConfig.MACHINES.lvCapConfig.output.getAsInt();
	}
	
	private int getMaxStorage(){
		return IEServerConfig.MACHINES.lvCapConfig.storage.getAsInt();
	}

	@Override
	public void load(@Nonnull CompoundTag nbt){
		super.load(nbt);
		
		this.isActive = nbt.getBoolean("isActive");
		this.tank.readFromNBT(nbt.getCompound("tank"));
		this.wireType = nbt.contains("wiretype") ? WireUtils.getWireTypeFromNBT(nbt, "wiretype") : null;
		
		if(nbt.contains("buffer"))
			this.energyStorage.deserializeNBT(nbt.get("buffer"));
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt){
		nbt.putBoolean("isActive", this.isActive);
		nbt.put("tank", this.tank.writeToNBT(new CompoundTag()));
		nbt.put("buffer", this.energyStorage.serializeNBT());
		
		if(this.wireType != null){
			nbt.putString("wiretype", this.wireType.getUniqueName());
		}
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
		return ClientboundBlockEntityDataPacket.create(this, b -> getUpdateTag());
	}

	@Override
	public void handleUpdateTag(CompoundTag tag){
		load(tag);
	}
	
	@Override
	@Nonnull
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = new CompoundTag();
		saveAdditional(nbt);
		return nbt;
	}

	@Override
	public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt){
		if(pkt.getTag() != null){
			load(pkt.getTag());
		}
	}
	
	@Override
	public void readOnPlacement(LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			CompoundTag nbt = stack.getOrCreateTag();
			
			this.tank.readFromNBT(nbt.getCompound("tank"));
			this.energyStorage.deserializeNBT(nbt.get("energy"));
		}
	}
	
	@Override
	public void setChanged(){
		super.setChanged();
		
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 3);
		level.updateNeighborsAt(worldPosition, state.getBlock());
	}

	@Override
	@Nonnull
	public List<ItemStack> getBlockEntityDrop(@Nullable LootContext context){
		ItemStack stack;
		if(context != null){
			stack = new ItemStack(context.getParamOrNull(LootContextParams.BLOCK_STATE).getBlock());
		}else{
			stack = new ItemStack(getBlockState().getBlock());
		}
		
		CompoundTag nbt = new CompoundTag();
		
		if(this.tank.getFluidAmount() > 0){
			CompoundTag tankNbt = this.tank.writeToNBT(new CompoundTag());
			nbt.put("tank", tankNbt);
		}
		
		if(this.energyStorage.getEnergyStored() > 0){
			Tag energyNbt = this.energyStorage.serializeNBT();
			nbt.put("energy", energyNbt);
		}
		
		if(!nbt.isEmpty())
			stack.setTag(nbt);
		return ImmutableList.of(stack);
	}
	
	@Override
	public int getAvailableEnergy(){
		return Math.min(getMaxOutput(), this.energyStorage.getEnergyStored());
	}
	
	@Override
	public void extractEnergy(int amount){
		this.energyStorage.extractEnergy(amount, false);
	}
	
	@Override
	public boolean isSource(ConnectionPoint cp){
		return true;
	}
	
	@Override
	public boolean isSink(ConnectionPoint cp){
		return false;
	}
	
	@Override
	public boolean shouldPlaySound(@Nonnull String sound){
		return this.isActive;
	}
	
	private final LazyOptional<IFluidHandler> fluidHandler = CapabilityUtils.constantOptional(this.tank);
	private final LazyOptional<IEnergyStorage> energyHandler = CapabilityUtils.constantOptional(this.energyStorage);
	@Override
	public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == null || side == Direction.UP)){
			return this.fluidHandler.cast();
		}else if(cap == CapabilityEnergy.ENERGY && (side == null || side == this.facing)){
			return this.energyHandler.cast();
		}
		return super.getCapability(cap, side);
	}
	
	@Override
	public void invalidateCaps(){
		super.invalidateCaps();
		this.fluidHandler.invalidate();
		this.energyHandler.invalidate();
	}
	
	@Override
	public Component[] getOverlayText(Player player, @Nonnull HitResult mop, boolean hammer){
		if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND))){
			Component s = null;
			if(tank.getFluid().getAmount() > 0)
				s = ((MutableComponent) tank.getFluid().getDisplayName()).append(": " + tank.getFluidAmount() + "mB");
			else
				s = new TranslatableComponent(Lib.GUI + "empty");
			return new Component[]{s};
		}
		return null;
	}
	
	@Override
	public boolean useNixieFont(@Nonnull Player player, @Nonnull HitResult mop){
		return false;
	}
	
	@Override
	public boolean interact(@Nonnull Direction side, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull ItemStack heldItem, float hitX, float hitY, float hitZ){
		if(FluidUtil.interactWithFluidHandler(player, hand, tank)){
			setChanged();
			flaxbeard.immersivepetroleum.common.util.Utils.unlockIPAdvancement(player, "main/gas_generator");
			return true;
		}else if(player.isShiftKeyDown()){
			boolean added = false;
			if(player.getInventory().getSelected().isEmpty()){
				added = true;
				player.getInventory().setItem(player.getInventory().selected, getBlockEntityDrop(null).get(0));
			}else{
				added = player.getInventory().add(getBlockEntityDrop(null).get(0));
			}
			
			if(added){
				level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
			}
			return true;
		}
		return false;
	}
	
	@Override
	@Nonnull
	public Direction getFacing(){
		return this.facing;
	}
	
	@Override
	public void setFacing(@Nonnull Direction facing){
		this.facing = facing;
	}
	
	@Override
	@Nonnull
	public PlacementLimitation getFacingLimitation(){
		return PlacementLimitation.HORIZONTAL;
	}
	
	@Override
	public boolean mirrorFacingOnPlacement(@Nonnull LivingEntity placer){
		return false;
	}
	
	@Override
	public boolean canHammerRotate(@Nonnull Direction side, @Nonnull Vec3 hit, @Nonnull LivingEntity entity){
		return true;
	}
	
	@Override
	public void tickClient(){
		ImmersiveEngineering.proxy.handleTileSound(IESounds.dieselGenerator, this, this.isActive, .3f, .75f);
		if(this.isActive && this.level.getGameTime() % 4 == 0){
			Direction fl = this.facing;
			Direction fw = this.facing.getClockWise();
			
			Vec3i vec = fw.getOpposite().getNormal();
			
			double x = this.worldPosition.getX() + .5 + (fl.getStepX() * 2 / 16F) + (-fw.getStepX() * .6125f);
			double y = this.worldPosition.getY() + .4;
			double z = this.worldPosition.getZ() + .5 + (fl.getStepZ() * 2 / 16F) + (-fw.getStepZ() * .6125f);
			
			this.level.addParticle(this.level.random.nextInt(10) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, x, y, z, vec.getX() * 0.025, 0, vec.getZ() * 0.025);
		}
	}
	
	@Override
	public void tickServer(){
		boolean lastActive = this.isActive;
		this.isActive = false;
		if(!this.level.hasNeighborSignal(this.worldPosition) && !this.tank.getFluid().isEmpty()){
			Fluid fluid = this.tank.getFluid().getFluid();
			int amount = FuelHandler.getFuelUsedPerTick(fluid);
			if(amount > 0 && this.tank.getFluidAmount() >= amount){
				int generated = FuelHandler.getFluxGeneratedPerTick(fluid);
				
				if(this.energyStorage.receiveEnergy(generated, true) >= generated){
					this.energyStorage.receiveEnergy(FuelHandler.getFluxGeneratedPerTick(fluid), false);
					this.tank.drain(new FluidStack(fluid, amount), FluidAction.EXECUTE);
					this.isActive = true;
				}
			}
		}
		
		if(lastActive != this.isActive || (!this.level.isClientSide && this.isActive)){
			setChanged();
		}
	}
	
	@Override
	public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other, ConnectionPoint otherTarget){
		this.wireType = cableType;
		setChanged();
	}
	
	@Override
	public void removeCable(@Nullable Connection connection, ConnectionPoint attachedPoint){
		this.wireType = null;
		setChanged();
	}
	
	@Override
	public boolean canConnect(){
		return true;
	}
	
	@Override
	public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset){
		if(level.getBlockState(target.position()).getBlock() != level.getBlockState(getBlockPos()).getBlock()){
			return false;
		}
		
		return this.wireType == null && (cableType.getCategory().equals(WireType.LV_CATEGORY) || cableType.getCategory().equals(WireType.MV_CATEGORY));
	}
	
	@Override
	public BlockPos getConnectionMaster(@Nullable WireType cableType, TargetingInfo target){
		return worldPosition;
	}
	
	@Override
	public ConnectionPoint getTargetedPoint(TargetingInfo info, Vec3i offset){
		return new ConnectionPoint(worldPosition, 0);
	}
	
	@Override
	public Collection<ConnectionPoint> getConnectionPoints(){
		return List.of(new ConnectionPoint(worldPosition, 0));
	}

	@Override
	public BlockPos getPosition(){
		return worldPosition;
	}

	@Override
	public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type){
		float xo = facing.getNormal().getX() * .5f + .5f;
		float zo = facing.getNormal().getZ() * .5f + .5f;
		return new Vec3(xo, .5f, zo);
	}
	
	@Override
	public Collection<ResourceLocation> getRequestedHandlers(){
		return ImmutableList.of(EnergyTransferHandler.ID);
	}
}
