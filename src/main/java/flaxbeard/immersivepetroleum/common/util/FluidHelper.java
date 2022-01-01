package flaxbeard.immersivepetroleum.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.ItemHandlerHelper;

public class FluidHelper{
	
	/** No pressure variant of {@link #copyFluid(FluidStack, int, boolean)} */
	public static FluidStack copyFluid(FluidStack fluid, int amount){
		FluidStack fs = new FluidStack(fluid.getFluid(), amount);
		return fs;
	}
	
	public static FluidStack copyFluid(FluidStack fluid, int amount, boolean pressurize){
		FluidStack fs = new FluidStack(fluid.getFluid(), amount);
		if(pressurize && amount > IFluidPipe.AMOUNT_UNPRESSURIZED){
			
			fs.getOrCreateTag().putBoolean(IFluidPipe.NBT_PRESSURIZED, true);
		}
		return fs;
	}
	
	/**
	 * Originally in IE as
	 * {@link blusunrize.immersiveengineering.common.util.Utils#isFluidContainerFull(ItemStack)}
	 */
	public static boolean isFluidContainerFull(ItemStack stack){
		return FluidUtil.getFluidHandler(stack).map(handler -> {
			for(int t = 0;t < handler.getTanks();++t)
				if(handler.getFluidInTank(t).getAmount() < handler.getTankCapacity(t))
					return false;
			return true;
		}).orElse(true);
	}
	
	/**
	 * Originally in IE as
	 * {@link blusunrize.immersiveengineering.common.util.Utils#fillFluidContainer(IFluidHandler, ItemStack, ItemStack, PlayerEntity)}
	 */
	public static ItemStack fillFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut, @Nullable PlayerEntity player){
		if(containerIn == null || containerIn.isEmpty())
			return ItemStack.EMPTY;
		
		FluidActionResult result = FluidUtil.tryFillContainer(containerIn, handler, Integer.MAX_VALUE, player, false);
		if(result.isSuccess()){
			final ItemStack full = result.getResult();
			if((containerOut.isEmpty() || ItemHandlerHelper.canItemStacksStack(containerOut, full))){
				if(!containerOut.isEmpty() && containerOut.getCount() + full.getCount() > containerOut.getMaxStackSize())
					return ItemStack.EMPTY;
				result = FluidUtil.tryFillContainer(containerIn, handler, Integer.MAX_VALUE, player, true);
				if(result.isSuccess()){
					return result.getResult();
				}
			}
		}
		return ItemStack.EMPTY;
	}
	
	/**
	 * FluidStack based version of
	 * {@link blusunrize.immersiveengineering.common.util.Utils#fillFluidContainer(IFluidHandler, ItemStack, ItemStack, PlayerEntity)}
	 * minus the useless bits :D
	 */
	public static ItemStack fillFluidContainer(IFluidTank tank, FluidStack fluid, ItemStack containerIn, ItemStack containerOut){
		if(containerIn == null || containerIn.isEmpty())
			return ItemStack.EMPTY;
		
		FluidActionResult result = tryFillContainer(tank, fluid, containerIn, false);
		if(result.isSuccess()){
			final ItemStack full = result.getResult();
			if((containerOut.isEmpty() || ItemHandlerHelper.canItemStacksStack(containerOut, full))){
				if(!containerOut.isEmpty() && containerOut.getCount() + full.getCount() > containerOut.getMaxStackSize()){
					return ItemStack.EMPTY;
				}
				
				result = tryFillContainer(tank, fluid, containerIn, true);
				if(result.isSuccess()){
					return result.getResult();
				}
			}
		}
		
		return ItemStack.EMPTY;
	}
	
	/**
	 * FluidStack based version of
	 * {@link net.minecraftforge.fluids.FluidUtil#tryFillContainer(ItemStack, IFluidHandler, int, PlayerEntity, boolean)}
	 * minus the useless bits :D
	 */
	static FluidActionResult tryFillContainer(IFluidTank tank, FluidStack fluidSource, @Nonnull ItemStack container, boolean doFill){
		ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
		return FluidUtil.getFluidHandler(containerCopy).map(containerFluidHandler -> {
			
			int fillableAmount = containerFluidHandler.fill(fluidSource, FluidAction.SIMULATE);
			if(fillableAmount > 0){
				if(doFill){
					FluidStack fs = new FluidStack(fluidSource, Math.min(fluidSource.getAmount(), fillableAmount));
					containerFluidHandler.fill(fs, FluidAction.EXECUTE);
					tank.drain(fs, FluidAction.EXECUTE);
				}
				
				ItemStack resultContainer = containerFluidHandler.getContainer();
				return new FluidActionResult(resultContainer);
			}
			
			return FluidActionResult.FAILURE;
		}).orElse(FluidActionResult.FAILURE);
	}
	
	private FluidHelper(){
	}
}
