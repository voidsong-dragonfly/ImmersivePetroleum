package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.multiblocks.OilTankMultiblock;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OilTankTileEntity extends MultiblockPartTileEntity<OilTankTileEntity>{
	public FluidTank tank = new FluidTank(1024*FluidAttributes.BUCKET_VOLUME);
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
	protected IFluidTank[] getAccessibleFluidTanks(Direction side){
		return null;
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
