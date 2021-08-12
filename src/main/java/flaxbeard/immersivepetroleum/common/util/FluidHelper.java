package flaxbeard.immersivepetroleum.common.util;

import net.minecraftforge.fluids.FluidStack;

public class FluidHelper{
	public static FluidStack copyFluid(FluidStack fluid, int amount, boolean pressurize){
		FluidStack fs = new FluidStack(fluid, amount);
		if(pressurize && amount > 50){
			fs.getOrCreateTag().putBoolean("pressurized", true);
		}
		return fs;
	}
}
