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
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.OilTankMultiblock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
	
	public static enum DynPortState implements IStringSerializable{
		INPUT, OUTPUT;
		
		@Override
		public String getString(){
			return this.toString().toLowerCase(Locale.ENGLISH);
		}
		
		public ITextComponent getText(){
			return new TranslationTextComponent("desc.immersivepetroleum.info.oiltank." + getString());
		}
		
		public DynPortState next(){
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
	
	/** Template-Location of the Redstone Input Port. (0 0 0)<br> */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(2, 1, 5));
	
	public FluidTank tank = new FluidTank(1024 * FluidAttributes.BUCKET_VOLUME);
	public EnumMap<Port, DynPortState> portConfig = new EnumMap<>(Port.class);
	public OilTankTileEntity(){
		super(OilTankMultiblock.INSTANCE, IPTileTypes.OILTANK.get(), true);
		this.redstoneControlInverted = true;
		for(Port port:Port.values()){
			if(port == Port.DYNAMIC_B || port == Port.DYNAMIC_C || port == Port.BOTTOM){
				portConfig.put(port, DynPortState.OUTPUT);
			}else{
				portConfig.put(port, DynPortState.INPUT);
			}
		}
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.readCustomNBT(nbt, descPacket);
		this.tank.readFromNBT(nbt.getCompound("tank"));
		
		for(Port port:Port.DYNAMIC_PORTS){
			portConfig.put(port, DynPortState.values()[nbt.getInt(port.getString())]);
		}
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.writeCustomNBT(nbt, descPacket);
		nbt.put("tank", this.tank.writeToNBT(new CompoundNBT()));
		
		for(Port port:Port.DYNAMIC_PORTS){
			nbt.putInt(port.getString(), portConfig.get(port).ordinal());
		}
	}
	
	@Override
	public void tick(){
		checkForNeedlessTicking();
		if(isDummy() || world.isRemote){
			return;
		}
		
		if(!isRSDisabled()){
			boolean update = false;
			for(Port port:Port.values()){
				if(this.portConfig.get(port) == DynPortState.OUTPUT){
					Direction facing;
					if(port == Port.DYNAMIC_D || port == Port.DYNAMIC_B){
						facing = getFacing().rotateY();
					}else if(port == Port.DYNAMIC_A || port == Port.DYNAMIC_C){
						facing = getFacing().rotateYCCW();
					}else if(port == Port.TOP){
						facing = Direction.UP;
					}else{
						facing = Direction.DOWN;
					}
					
					FluidUtil.getFluidHandler(this.world, getBlockPosForPos(port.posInMultiblock).offset(facing), facing.getOpposite()).map(out -> {
						if(this.tank.getFluidAmount() > 0){
							FluidStack fs = copyFluid(this.tank.getFluid(), Math.min(tank.getFluidAmount(), 432));
							int accepted = out.fill(fs, FluidAction.SIMULATE);
							if(accepted > 0){
								int drained = out.fill(copyFluid(fs, Math.min(fs.getAmount(), accepted)), FluidAction.EXECUTE);
								this.tank.drain(Utils.copyFluidStackWithAmount(fs, drained, true), FluidAction.EXECUTE);
								this.markContainingBlockForUpdate(null);
								return true;
							}
						}
						return false;
					}).orElse(false);
				}
			}
			
			if(update){
				updateMasterBlock(null, true);
			}
		}
	}
	
	private FluidStack copyFluid(FluidStack fluid, int amount){
		FluidStack fs = new FluidStack(fluid, amount);
		if(amount > 50){
			fs.getOrCreateTag().putBoolean("pressurized", true);
		}
		return fs;
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
						DynPortState portState = master.portConfig.get(port);
						ImmersivePetroleum.log.info("{} -> {}", portState, portState.next());
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
	
	public DynPortState getPortStateFor(Port port){
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
		return Port.ALL.contains(this.posInMultiblock);
	}
	
	@Override
	protected boolean canDrainTankFrom(int iTank, Direction side){
		return Port.BOTTOM.matches(this.posInMultiblock);
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
