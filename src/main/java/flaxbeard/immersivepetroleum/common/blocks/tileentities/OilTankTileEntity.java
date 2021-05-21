package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockOverlayText;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.OilTankMultiblock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OilTankTileEntity extends MultiblockPartTileEntity<OilTankTileEntity> implements IBlockOverlayText, IBlockBounds{
	/** Template-Location of the Redstone Input Port. (0 0 0)<br> */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(0, 1, 0));
	
	public FluidTank tank = new FluidTank(1024 * FluidAttributes.BUCKET_VOLUME);
	public OilTankTileEntity(){
		super(OilTankMultiblock.INSTANCE, IPTileTypes.OILTANK.get(), true);
	}
	
	@Override
	public void tick(){
		checkForNeedlessTicking();
		if(isDummy() || world.isRemote)
			return;
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return Redstone_IN;
	}
	
	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side){
		return new IFluidTank[0];
	}
	
	@Override
	protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource){
		return false;
	}
	
	@Override
	public ITextComponent[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer){
		return null;
	}
	
	@Override
	public boolean useNixieFont(PlayerEntity player, RayTraceResult mop){
		return false;
	}
	
	@Override
	protected boolean canDrainTankFrom(int iTank, Direction side){
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
		if(x == 5 && z == 3){
			if(y == 1 || y == 2){
				main.add(new AxisAlignedBB(0.0, 0.0, 0.125, 0.0625, 1.0, 0.875));
			}
		}
		
		// Easy Access Slabs™
		if(y == 2){
			if(x == 5 && (z == 2 || z == 4)){
				main.add(new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0));
			}
		}
		
		// Railings
		if(y == 3){
			if(x >= 0 && x <= 4){
				if(z == 0){
					main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.0625));
				}else if(z == 4){
					main.add(new AxisAlignedBB(0.0, 0.0, 0.9375, 1.0, 1.0, 1.0));
				}
			}
			if(z >= 0 && z <= 4){
				if(x == 0){
					main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0));
				}else if(x == 4 && z != 4){
					main.add(new AxisAlignedBB(0.9375, 0.0, 0.0, 1.0, 1.0, 1.0));
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
