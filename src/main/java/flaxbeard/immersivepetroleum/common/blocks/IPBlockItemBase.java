package flaxbeard.immersivepetroleum.common.blocks;

import java.util.List;

import blusunrize.immersiveengineering.api.Lib;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import javax.annotation.Nonnull;

public class IPBlockItemBase extends BlockItem{
	public IPBlockItemBase(Block blockIn, Properties builder){
		super(blockIn, builder);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn){
		if(stack.hasTag()){
			// Display Stored Tank Information
			if(stack.getTag().contains("tank")){
				CompoundTag tank = stack.getTag().getCompound("tank");
				
				FluidStack fluidstack = FluidStack.loadFluidStackFromNBT(tank);
				if(fluidstack.getAmount() > 0){
					tooltip.add(((MutableComponent) fluidstack.getDisplayName()).append(" " + fluidstack.getAmount() + "mB").withStyle(ChatFormatting.GRAY));
				}else{
					tooltip.add(new TranslatableComponent(Lib.GUI + "empty").withStyle(ChatFormatting.GRAY));
				}
			}
			
			// Display Stored Energy Information
			if(stack.getTag().contains("energy")){
				CompoundTag energy = stack.getTag().getCompound("energy");
				int flux = energy.getInt("ifluxEnergy");
				tooltip.add(new TextComponent(flux + "RF").withStyle(ChatFormatting.GRAY));
			}
		}
		
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}
