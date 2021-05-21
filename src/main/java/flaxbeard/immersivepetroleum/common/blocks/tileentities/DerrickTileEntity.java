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

public class DerrickTileEntity extends PoweredMultiblockTileEntity<DerrickTileEntity, MultiblockRecipe> implements IBlockBounds{
	
	/** Template-Location of the Fluid Input Port. (0 0 0)<br> */
	public static final BlockPos Fluid_IN = new BlockPos(2, 0, 0);
	
	/** Template-Location of the Fluid Output Port. (0 0 0)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(2, 0, 4);
	
	/** Template-Location of the Energy Input Ports.<br><pre>0 0 0</pre><br> */
	public static final Set<BlockPos> Energy_IN = ImmutableSet.of(new BlockPos(0, 0, 0));
	
	/** Template-Location of the Redstone Input Port. (0 0 0)<br> */
	public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(1, 0, 0));
	
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
		
		// TODO Bounds
		
		// Use default cube shape if nessesary
		if(main.isEmpty()){
			main.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
		}
		return main;
	}
}
