package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockOverlayText;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHammerInteraction;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IPlayerInteraction;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.OilTankMultiblock;
import flaxbeard.immersivepetroleum.common.util.FluidHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OilTankTileEntity extends MultiblockPartTileEntity<OilTankTileEntity> implements IPlayerInteraction, IBlockOverlayText, IBlockBounds, IHammerInteraction{
	
	public static enum PortState implements IStringSerializable{
		INPUT, OUTPUT;
		
		@Override
		public String getString(){
			return this.toString().toLowerCase(Locale.ENGLISH);
		}
		
		public ITextComponent getText(){
			return new TranslationTextComponent("desc.immersivepetroleum.info.oiltank." + getString());
		}
		
		public PortState next(){
			return this == INPUT ? OUTPUT : INPUT;
		}
	}
	
	public static enum Port implements IStringSerializable{
		TOP(new BlockPos(2, 2, 3)),
		BOTTOM(new BlockPos(2, 0, 3)),
		DYNAMIC_A(new BlockPos(0, 1, 2)),
		DYNAMIC_B(new BlockPos(4, 1, 2)),
		DYNAMIC_C(new BlockPos(0, 1, 4)),
		DYNAMIC_D(new BlockPos(4, 1, 4));
		
		public static final Port[] DYNAMIC_PORTS = {DYNAMIC_A, DYNAMIC_B, DYNAMIC_C, DYNAMIC_D};
		
		public static final Set<BlockPos> ALL = toSet(values());
		public static final Set<BlockPos> DYNAMIC_PORTS_SET = toSet(DYNAMIC_PORTS);
		
		public final BlockPos posInMultiblock;
		private Port(BlockPos posInMultiblock){
			this.posInMultiblock = posInMultiblock;
		}
		
		public boolean matches(BlockPos posInMultiblock){
			return posInMultiblock.equals(this.posInMultiblock);
		}
		
		@Override
		public String getString(){
			return this.toString().toLowerCase(Locale.ENGLISH);
		}
		
		static Set<BlockPos> toSet(Port... ports){
			ImmutableSet.Builder<BlockPos> builder = ImmutableSet.builder();
			for(Port port:ports){
				builder.add(port.posInMultiblock);
			}
			return builder.build();
		}
	}
	
	/** Template-Location of the Redstone Input Port. (0 0 0)<br>*/
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(2, 2, 5));
	
	public FluidTank tank = new FluidTank(1024 * FluidAttributes.BUCKET_VOLUME);
	public EnumMap<Port, PortState> portConfig = new EnumMap<>(Port.class);
	public OilTankTileEntity(){
		super(OilTankMultiblock.INSTANCE, IPTileTypes.OILTANK.get(), true);
		this.redstoneControlInverted = true;
		for(Port port:Port.values()){
			if(port == Port.DYNAMIC_B || port == Port.DYNAMIC_C || port == Port.BOTTOM){
				portConfig.put(port, PortState.OUTPUT);
			}else{
				portConfig.put(port, PortState.INPUT);
			}
		}
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.readCustomNBT(nbt, descPacket);
		this.tank.readFromNBT(nbt.getCompound("tank"));
		
		for(Port port:Port.DYNAMIC_PORTS){
			portConfig.put(port, PortState.values()[nbt.getInt(port.getString())]);
		}
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.writeCustomNBT(nbt, descPacket);
		nbt.put("tank", this.tank.writeToNBT(new CompoundNBT()));
		
		for(Port port:Port.DYNAMIC_PORTS){
			nbt.putInt(port.getString(), getPortStateFor(port).ordinal());
		}
	}
	
	@Override
	public void tick(){
		checkForNeedlessTicking();
		if(isDummy() || world.isRemote){
			return;
		}
		int threshold = 5;
		int maxTransfer = FluidAttributes.BUCKET_VOLUME;
		
		PortState portStateA = getPortStateFor(Port.DYNAMIC_A),
				portStateB = getPortStateFor(Port.DYNAMIC_B),
				portStateC = getPortStateFor(Port.DYNAMIC_C),
				portStateD = getPortStateFor(Port.DYNAMIC_D);
		
		boolean wasBalancing = false;
		if((portStateA == PortState.OUTPUT && portStateC == PortState.INPUT) || (portStateA == PortState.INPUT && portStateC == PortState.OUTPUT)){
			wasBalancing |= equalize(Port.DYNAMIC_A, threshold, maxTransfer);
		}
		
		if((portStateB == PortState.OUTPUT && portStateD == PortState.INPUT) || (portStateB == PortState.INPUT && portStateD == PortState.OUTPUT)){
			wasBalancing |= equalize(Port.DYNAMIC_B, threshold, maxTransfer);
		}
		
		if(!isRSDisabled()){
			for(Port port:Port.values()){
				if((!wasBalancing && getPortStateFor(port) == PortState.OUTPUT) || (wasBalancing && port == Port.BOTTOM)){
					Direction facing = getPortDirection(port);
					BlockPos pos = getBlockPosForPos(port.posInMultiblock).offset(facing);
					final boolean isSameTEType = getWorld().getTileEntity(pos) instanceof OilTankTileEntity;
					
					FluidUtil.getFluidHandler(this.world, pos, facing.getOpposite()).map(out -> {
						if(this.tank.getFluidAmount() > 0){
							FluidStack fs = FluidHelper.copyFluid(this.tank.getFluid(), Math.min(tank.getFluidAmount(), 432), !isSameTEType);
							int accepted = out.fill(fs, FluidAction.SIMULATE);
							if(accepted > 0){
								int drained = out.fill(FluidHelper.copyFluid(fs, Math.min(fs.getAmount(), accepted), !isSameTEType), FluidAction.EXECUTE);
								this.tank.drain(Utils.copyFluidStackWithAmount(this.tank.getFluid(), drained, true), FluidAction.EXECUTE);
								this.markContainingBlockForUpdate(null);
								return true;
							}
						}
						return false;
					}).orElse(false);
				}
			}
		}
	}
	
	private boolean equalize(Port port, int threshold, int maxTransfer){
		Direction facing = getPortDirection(port);
		BlockPos pos = getBlockPosForPos(port.posInMultiblock).offset(facing);
		TileEntity te = getWorld().getTileEntity(pos);
		
		if(te instanceof OilTankTileEntity){
			OilTankTileEntity otherMaster = ((OilTankTileEntity) te).master();
			int diff = otherMaster.tank.getFluidAmount() - this.tank.getFluidAmount();
			int amount = Math.min(Math.abs(diff) / 2, maxTransfer);
			
			if((diff < -threshold && transfer(this, otherMaster, amount)) || (diff > threshold && transfer(otherMaster, this, amount))){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean transfer(OilTankTileEntity src, OilTankTileEntity dst, int amount){
		FluidStack fs = new FluidStack(src.tank.getFluid(), amount);
		int accepted = dst.tank.fill(fs, FluidAction.SIMULATE);
		if(accepted > 0){
			fs = new FluidStack(src.tank.getFluid(), accepted);
			dst.tank.fill(fs, FluidAction.EXECUTE);
			src.tank.drain(fs, FluidAction.EXECUTE);
			
			src.markContainingBlockForUpdate(null);
			dst.markContainingBlockForUpdate(null);
			return true;
		}
		
		return false;
	}
	
	private Direction getPortDirection(Port port){
		switch(port){
			case DYNAMIC_B:
			case DYNAMIC_D:{
				return getIsMirrored() ? getFacing().rotateYCCW() : getFacing().rotateY();
			}
			case DYNAMIC_A:
			case DYNAMIC_C:{
				return getIsMirrored() ? getFacing().rotateY() : getFacing().rotateYCCW();
			}
			case TOP:{
				return Direction.UP;
			}
			default:
				return Direction.DOWN;
		}
	}
	
	@Override
	public boolean interact(Direction side, PlayerEntity player, Hand hand, ItemStack heldItem, float hitX, float hitY, float hitZ){
		OilTankTileEntity master = this.master();
		if(master != null){
			if(FluidUtils.interactWithFluidHandler(player, hand, master.tank)){
				this.updateMasterBlock(null, true);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hammerUseSide(Direction side, PlayerEntity player, Hand hand, Vector3d hitVec){
		if(!this.getWorldNonnull().isRemote){
			for(Port port:Port.DYNAMIC_PORTS){
				if(port.posInMultiblock.equals(this.posInMultiblock)){
					OilTankTileEntity master = master();
					if(master != null){
						PortState portState = master.getPortStateFor(port);
						master.portConfig.put(port, portState.next());
						this.updateMasterBlock(null, true);
						return true;
					}
					break;
				}
			}
		}
		return false;
	}
	
	public PortState getPortStateFor(Port port){
		return this.portConfig.get(port);
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return Redstone_IN;
	}
	
	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side){
		OilTankTileEntity master = master();
		if(master != null && Port.ALL.contains(posInMultiblock)){
			return new FluidTank[]{master.tank};
		}
		return new FluidTank[0];
	}
	
	@Override
	protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource){
		for(Port port:Port.values()){
			if(port.matches(this.posInMultiblock)){
				OilTankTileEntity master = isDummy() ? master() : this;
				return master.getPortStateFor(port) == PortState.INPUT;
			}
		}
		
		return false;
	}
	
	@Override
	protected boolean canDrainTankFrom(int iTank, Direction side){
		for(Port port:Port.values()){
			if(port.matches(this.posInMultiblock)){
				OilTankTileEntity master = isDummy() ? master() : this;
				return master.getPortStateFor(port) == PortState.OUTPUT;
			}
		}
		
		return false;
	}
	
	@Override
	public ITextComponent[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer){
		if(Utils.isFluidRelatedItemStack(player.getHeldItem(Hand.MAIN_HAND))){
			OilTankTileEntity master = master();
			FluidStack fs = master != null ? master.tank.getFluid() : this.tank.getFluid();
			return new ITextComponent[]{TextUtils.formatFluidStack(fs)};
		}
		return null;
	}
	
	@Override
	public boolean useNixieFont(PlayerEntity player, RayTraceResult mop){
		return false;
	}
	
	private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(OilTankTileEntity::getShape);
	public static boolean updateShapes = false;
	@Override
	public VoxelShape getBlockBounds(ISelectionContext ctx){
		if(updateShapes){
			updateShapes = false;
			SHAPES = CachedShapesWithTransform.createForMultiblock(OilTankTileEntity::getShape);
		}
		return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
		int x = posInMultiblock.getX();
		int y = posInMultiblock.getY();
		int z = posInMultiblock.getZ();
		
		List<AxisAlignedBB> main = new ArrayList<>();
		
		// Corner Supports
		if(y == 0){
			if(x == 0 && z == 0){
				main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 0.25, 1.0, 0.25));
				
			}else if(x == 4 && z == 0){
				main.add(new AxisAlignedBB(0.75, 0.0, 0.0, 1.0, 1.0, 0.25));
				
			}else if(x == 0 && z == 4){
				main.add(new AxisAlignedBB(0.0, 0.0, 0.75, 0.25, 1.0, 1.0));
				
			}else if(x == 4 && z == 4){
				main.add(new AxisAlignedBB(0.75, 0.0, 0.75, 1.0, 1.0, 1.0));
			}
		}
		
		// Easy Access Ladders™
		if(x == 3 && z == 0){
			if(y == 1 || y == 2){
				main.add(new AxisAlignedBB(0.125, 0.0, 0.9375, 0.875, 1.0, 1.0));
			}
		}
		
		// Easy Access Slabs™
		if(y == 2){
			if(z == 0 && (x == 2 || x == 4)){
				main.add(new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
			}
		}
		
		// Railings
		if(y == 3){
			if(z >= 1 && z <= 5){
				if(x == 0){
					main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0));
				}else if(x == 4){
					main.add(new AxisAlignedBB(0.9375, 0.0, 0.0, 1.0, 1.0, 1.0));
				}
			}
			if(x >= 0 && x <= 4){
				if(z == 5){
					main.add(new AxisAlignedBB(0.0, 0.0, 0.9375, 1.0, 1.0, 1.0));
				}else if(z == 1 && x != 4){
					main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.0625));
				}
			}
		}
		
		// Use default cube shape if nessesary
		if(main.isEmpty()){
			main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
		}
		return main;
	}
}
