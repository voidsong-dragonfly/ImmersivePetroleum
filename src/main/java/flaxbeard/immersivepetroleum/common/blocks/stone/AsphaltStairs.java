package flaxbeard.immersivepetroleum.common.blocks.stone;

import java.util.List;

import flaxbeard.immersivepetroleum.common.blocks.IPBlockStairs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;

public class AsphaltStairs extends IPBlockStairs<AsphaltBlock>{
	public AsphaltStairs(AsphaltBlock base){
		super(base);
	}
	
	@Override
	public float getSpeedFactor(){
		return AsphaltBlock.speedFactor();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		AsphaltBlock.tooltip(stack, worldIn, tooltip, flagIn);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}