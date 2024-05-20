package flaxbeard.immersivepetroleum.common.blocks.multiblocks.util;

import javax.annotation.Nullable;

import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.IFluidTank;

public interface IMultiblockRecipeProcessor{
	public IFluidTank[] getInternalTanks();
	public int[] getOutputTanks();
	
	@Nullable
	IEnergyStorage getPowerSupply();
}
