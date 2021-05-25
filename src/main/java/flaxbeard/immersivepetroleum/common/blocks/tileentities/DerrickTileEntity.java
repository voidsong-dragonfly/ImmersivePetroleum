package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class DerrickTileEntity extends PoweredMultiblockTileEntity<DerrickTileEntity, MultiblockRecipe> implements IBlockBounds{
	
	/** Template-Location of the Fluid Input Port. (2 0 4)<br> */
	public static final BlockPos Fluid_IN = new BlockPos(2, 0, 4);
	
	/** Template-Location of the Fluid Output Port. (4 0 2)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(4, 0, 2);
	
	/** Template-Location of the Energy Input Ports.<br><pre>0 0 0</pre><br> */
	public static final Set<BlockPos> Energy_IN = ImmutableSet.of(new BlockPos(0, 0, 0));
	
	/** Template-Location of the Redstone Input Port. (0 0 0)<br> */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(1, 0, 0));
	
	public FluidTank dummyTank = new FluidTank(0);
	public DerrickTileEntity(){
		super(DerrickMultiblock.INSTANCE, 16000, true, null);
	}
	
	@Override
	public TileEntityType<?> getType(){
		return IPTileTypes.DERRICK.get();
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.readCustomNBT(nbt, descPacket);
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket){
		super.writeCustomNBT(nbt, descPacket);
	}
	
	@Override
	public void tick(){
		super.tick();
	}
	
	@Override
	public NonNullList<ItemStack> getInventory(){
		return null;
	}
	
	@Override
	public boolean isStackValid(int slot, ItemStack stack){
		return false;
	}
	
	@Override
	public int getSlotLimit(int slot){
		return 0;
	}
	
	@Override
	public void doGraphicalUpdates(int slot){
	}
	
	@Override
	protected MultiblockRecipe getRecipeForId(ResourceLocation id){
		return null;
	}
	
	@Override
	public Set<BlockPos> getEnergyPos(){
		return Energy_IN;
	}
	
	@Override
	public Set<BlockPos> getRedstonePos(){
		return Redstone_IN;
	}
	
	@Override
	public IFluidTank[] getInternalTanks(){
		return new IFluidTank[0];
	}
	
	@Override
	public MultiblockRecipe findRecipeForInsertion(ItemStack inserting){
		return null;
	}
	
	@Override
	public int[] getOutputSlots(){
		return null;
	}
	
	@Override
	public int[] getOutputTanks(){
		return null;
	}
	
	@Override
	public boolean additionalCanProcessCheck(MultiblockProcess<MultiblockRecipe> process){
		return false;
	}
	
	@Override
	public void doProcessOutput(ItemStack output){
	}
	
	@Override
	public void doProcessFluidOutput(FluidStack output){
	}
	
	@Override
	public void onProcessFinish(MultiblockProcess<MultiblockRecipe> process){
	}
	
	@Override
	public int getMaxProcessPerTick(){
		return 1;
	}
	
	@Override
	public int getProcessQueueMaxLength(){
		return 1;
	}
	
	@Override
	public float getMinProcessDistance(MultiblockProcess<MultiblockRecipe> process){
		return 0;
	}
	
	@Override
	public boolean isInWorldProcessingMachine(){
		return false;
	}
	
	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side){
		DerrickTileEntity master = master();
		if(master != null){
			if(this.posInMultiblock.equals(Fluid_IN)){
				if(side == null || side == getFacing().getOpposite()){
					return new IFluidTank[]{this.dummyTank};
				}
			}
			
			if(this.posInMultiblock.equals(Fluid_OUT)){
				if(side == null || (getIsMirrored() ? side == getFacing().rotateYCCW() : side == getFacing().rotateY())){
					return new IFluidTank[]{this.dummyTank};
				}
			}
		}
		return new IFluidTank[0];
	}
	
	@Override
	protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource){
		return false;
	}
	
	@Override
	protected boolean canDrainTankFrom(int iTank, Direction side){
		return false;
	}
	
	public boolean isLadder(){
		int x = posInMultiblock.getX();
		int y = posInMultiblock.getY();
		int z = posInMultiblock.getZ();
		
		// Primary Ladder
		if((x == 0 && z == 2) && (y >= 0 && y <= 8)){
			return true;
		}
		
		// Secondary Ladder
		if((x == 1 && z == 2) && (y >= 9 && y <= 14)){
			return true;
		}
		
		return false;
	}
	
	private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(DerrickTileEntity::getShape);
	public static boolean updateShapes = false;
	@Override
	public VoxelShape getBlockBounds(ISelectionContext ctx){
		if(updateShapes){
			updateShapes = false;
			SHAPES = CachedShapesWithTransform.createForMultiblock(DerrickTileEntity::getShape);
		}
		return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
	}
	
	private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
		int x = posInMultiblock.getX();
		int y = posInMultiblock.getY();
		int z = posInMultiblock.getZ();
		
		List<AxisAlignedBB> main = new ArrayList<>();
		
		// Base
		if(y == 0){
			if(!(x == 2 && z == 4 || x == 4 && z == 2 || x == 2 && z == 0 || x == 2 && z == 2)){
				main.add(box(0, 0, 0, 16, 8, 16));
			}
		}
		
		// Platform 1 & 2
		if((y == 8 || y == 14) && !(x == 2 && z == 2 || x == 0 && z == 2 || x == 1 && z == 2)){
			main.add(box(0, 8, 0, 16, 16, 16));
		}
		
		if(y == 0 || y == 1){
			// Power Box
			if(z == 0){
				if(x == 1){
					main.add(box(4, 0, 0, 16, 16, 16));
				}else if(x == 3){
					main.add(box(0, 0, 0, 12, 16, 16));
				}
			}
			
			// Fluid Input Box
			if(x == 1 && z == 4){
				main.add(box(8, 8, 0, 16, 24, 16));
			}else if(x == 3 && z == 4){
				main.add(box(0, 8, 0, 8, 24, 16));
			}
			if(y == 1 && x == 2 && z == 4){
				main.add(box(-8, 0, 0, 24, 8, 16));
			}
		}
		
		// Center Pipe and Stuff
		if(x == 2 && z == 2 && y >= 0 && y <= 13){
			if(y == 0){
				main.add(box(-4, 8, -4, 20, 16, 20));
				main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
			}else if(y > 1){
				// Pipe
				main.add(box(4, 0, 4, 12, 16, 12));
			}
			
			if(y == 1){
				main.add(box(-4, 0, -4, 20, 8, 20));
				main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
			}
			if(y == 2){
				main.add(box(0, 0, 0, 16, 8, 16));
			}
		}
		
		// Primary Ladder
		if(x == 0 && z == 2 && (y >= 1 && y <= 8)){
			if(y == 1){
				main.add(box(15, 8, 2, 16, 16, 14));
			}else{
				main.add(box(15, 0, 2, 16, 16, 14));
			}
			
			if(y > 2){
				main.add(box(0, 0, 0, 1, 16, 16));
				main.add(box(0, 0, 0, 16, 16, 1));
				main.add(box(0, 0, 15, 16, 16, 16));
			}
		}
		// Secondary Ladder
		if((x == 1 && z == 2) && (y >= 9 && y <= 14)){
			if(y == 9){
				main.add(box(15, 8, 2, 16, 16, 14));
			}else{
				main.add(box(15, 0, 2, 16, 16, 14));
			}
			
			if(y > 10){
				main.add(box(0, 0, 0, 1, 16, 16));
				main.add(box(0, 0, 0, 16, 16, 1));
				main.add(box(0, 0, 15, 16, 16, 16));
			}
		}
		
		// Supports: from Base to Platform 1
		if(y >= 0 && y <= 8){
			// Corners
			if(x == 0 && z == 0){
				main.add(box(9 + y, 0, 9 + y, 15 + y, 16, 15 + y));
			}else if(x == 4 && z == 0){
				main.add(box(1 - y, 0, 9 + y, 7 - y, 16, 15 + y));
			}else if(x == 0 && z == 4){
				main.add(box(9 + y, 0, 1 - y, 15 + y, 16, 7 - y));
			}else if(x == 4 && z == 4){
				main.add(box(1 - y, 0, 1 - y, 7 - y, 16, 7 - y));
			}
			
			// Centers
			if(y >= 3){
				if(x == 2 && z == 0){
					main.add(box(6, 0, 9 + y, 10, 16, 14 + y));
				}else if(x == 2 && z == 4){
					main.add(box(6, 0, 2 - y, 10, 16, 7 - y));
				}else if(x == 4 && z == 2){
					main.add(box(2 - y, 0, 6, 7 - y, 16, 10));
				}
			}
			
			// First horizontal bars, above base
			if(y == 3){
				if(x >= 1 && x <= 3){
					if(z == 0){
						main.add(box(0, -4, 12, 16, 0, 16));
					}else if(z == 4){
						main.add(box(0, -4, 0, 16, 0, 4));
					}
				}
				if(z >= 1 && z <= 3){
					if(x == 0 && z != 2){
						main.add(box(12, -4, 0, 16, 0, 16));
					}else if(x == 4){
						main.add(box(0, -4, 0, 4, 0, 16));
					}
				}
			}
			
			// Second horizontal bars, below Platform 1
			if(y == 6){
				if(x >= 1 && x <= 3){
					if(z == 0){
						main.add(box(0, 4, 16, 16, 8, 20));
					}else if(z == 4){
						main.add(box(0, 4, -4, 16, 8, 0));
					}
				}
				if(z >= 1 && z <= 3){
					if(x == 0){
						main.add(box(16, 4, 0, 20, 8, 16));
					}else if(x == 4){
						main.add(box(-4, 4, 0, 0, 8, 16));
					}
				}
			}
		}
		
		// Supports: From Platform 1 to Platform 2, up to the very top
		if(y >= 9 && y <= 16){
			double off = y - 9;
			
			// Corners
			if(x == 1 && z == 1){
				main.add(box(2 + off, 0, 2 + off, 7 + off, 16, 7 + off));
			}else if(x == 3 && z == 1){
				main.add(box(9 - off, 0, 2 + off, 14 - off, 16, 7 + off));
			}else if(x == 1 && z == 3){
				main.add(box(2 + off, 0, 9 - off, 7 + off, 16, 14 - off));
			}else if(x == 3 && z == 3){
				main.add(box(9 - off, 0, 9 - off, 14 - off, 16, 14 - off));
			}
			
			// Centers
			if(x == 2 && z == 1){
				main.add(box(6, 0, 2 + off, 10, 16, 7 + off));
			}else if(x == 2 && z == 3){
				main.add(box(6, 0, 9 + off, 10, 16, 14 + off));
			}else if(x == 3 && z == 2){
				main.add(box(10 - off, 0, 6, 14, 16 - off, 10));
			}
			
			// Third horizontal bars, above platform 1
			if(y == 9){
				if(x == 3){
					if(z == 1){
						main.add(box(9, 12, 7, 13, 16, 16));
					}else if(z == 2){
						main.add(box(9, 12, 0, 13, 16, 16));
					}else if(z == 3){
						main.add(box(9, 12, 0, 13, 16, 9));
					}
				}
				
				if(z == 1){
					if(x == 1){
						main.add(box(7, 12, 3, 16, 16, 7));
					}else if(x == 2){
						main.add(box(0, 12, 3, 16, 16, 7));
					}else if(x == 3){
						main.add(box(0, 12, 3, 9, 16, 7));
					}
				}else if(z == 3){
					if(x == 1){
						main.add(box(7, 12, 9, 16, 16, 13));
					}else if(x == 2){
						main.add(box(0, 12, 9, 16, 16, 13));
					}else if(x == 3){
						main.add(box(0, 12, 9, 9, 16, 13));
					}
				}
			}
			
			// Fourth horizontal bars, below platform 2
			if(y == 13){
				if(x == 3){
					if(z == 1){
						main.add(box(5, 4, 11, 9, 8, 16));
					}else if(z == 2){
						main.add(box(5, 4, 0, 9, 8, 16));
					}else if(z == 3){
						main.add(box(5, 4, 0, 9, 8, 5));
					}
				}
				
				if(z == 1){
					if(x == 1){
						main.add(box(11, 4, 7, 16, 8, 11));
						main.add(box(7, 4, 11, 11, 8, 16));
					}else if(x == 2){
						main.add(box(0, 4, 7, 16, 8, 11));
					}else if(x == 3){
						main.add(box(0, 4, 7, 5, 8, 11));
					}
				}else if(z == 3){
					if(x == 1){
						main.add(box(11, 4, 5, 16, 8, 9));
						main.add(box(7, 4, 0, 11, 8, 5));
					}else if(x == 2){
						main.add(box(0, 4, 5, 16, 8, 9));
					}else if(x == 3){
						main.add(box(0, 4, 5, 5, 8, 9));
					}
				}
			}
			
			// Horizontal bars at the very top
			if(y == 16){
				if(z == 1){
					if(x == 1){
						main.add(box(9, 12, 9, 16, 16, 16));
					}else if(x == 2){
						main.add(box(0, 12, 9, 16, 16, 15));
					}else if(x == 3){
						main.add(box(0, 12, 9, 7, 16, 16));
					}
				}
				
				if(z == 2){
					if(x == 1){
						main.add(box(9, 12, 0, 15, 16, 16));
					}else if(x == 3){
						main.add(box(1, 12, 0, 7, 16, 16));
					}
				}
				
				if(z == 3){
					if(x == 1){
						main.add(box(9, 12, 0, 16, 16, 7));
					}else if(x == 2){
						main.add(box(0, 12, 1, 16, 16, 7));
					}else if(x == 3){
						main.add(box(0, 12, 0, 7, 16, 7));
					}
				}
			}
		}
		
		// Use default cube shape if nessesary
		if(main.isEmpty()){
			main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
		}
		return main;
	}
	
	/** Makes a box using texture pixel space (Assuming 16x16 p texture) */
	private static AxisAlignedBB box(double x0, double y0, double z0, double x1, double y1, double z1){
		return new AxisAlignedBB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D);
	}
}
