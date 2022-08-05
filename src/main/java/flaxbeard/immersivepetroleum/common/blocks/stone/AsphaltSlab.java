package flaxbeard.immersivepetroleum.common.blocks.stone;

import java.util.List;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.common.blocks.IPBlockSlab;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;

public class AsphaltSlab extends IPBlockSlab<AsphaltBlock>{
	public AsphaltSlab(AsphaltBlock base){
		super(base);
	}
	
	@Override
	public float getSpeedFactor(){
		return AsphaltBlock.speedFactor();
	}
	
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn){
		AsphaltBlock.tooltip(stack, worldIn, tooltip, flagIn);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
}