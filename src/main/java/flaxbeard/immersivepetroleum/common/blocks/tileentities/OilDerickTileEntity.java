package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import java.util.Set;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.OilDerickMultiblock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class OilDerickTileEntity extends PoweredMultiblockTileEntity<OilDerickTileEntity, MultiblockRecipe> implements IBlockBounds{
	public OilDerickTileEntity(){
		super(OilDerickMultiblock.INSTANCE, 16000, true, null);
	}
	
	@Override
	public TileEntityType<?> getType(){
		return IPTileTypes.DERICK.get();
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
	public VoxelShape getBlockBounds(ISelectionContext ctx){
		return null;
	}

	@Override
	protected MultiblockRecipe getRecipeForId(ResourceLocation id){
		return null;
	}

	@Override
	public Set<BlockPos> getEnergyPos(){
		return null;
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
}
