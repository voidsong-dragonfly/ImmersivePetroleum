package flaxbeard.immersivepetroleum.common.blocks.stone;

import java.util.List;
import java.util.Locale;

import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;

public class AsphaltBlock extends IPBlockBase{
	protected static final float SPEED_FACTOR = 1.20F;
	
	public AsphaltBlock(){
		super(Block.Properties.of(Material.STONE).speedFactor(SPEED_FACTOR).strength(2.0F, 10.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
	}
	
	@Override
	public float getSpeedFactor(){
		return speedFactor();
	}
	
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn){
		tooltip(stack, worldIn, tooltip, flagIn);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
	static void tooltip(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		if(IPServerConfig.MISCELLANEOUS.asphalt_speed.get()){
			MutableComponent out = new TranslatableComponent("desc.immersivepetroleum.flavour.asphalt", String.format(Locale.ENGLISH, "%.1f%%", (SPEED_FACTOR * 100 - 100))).withStyle(ChatFormatting.GRAY);
			
			tooltip.add(out);
		}
	}
	
	static float speedFactor(){
		if(!IPServerConfig.MISCELLANEOUS.asphalt_speed.get()){
			return 1.0F;
		}
		
		return SPEED_FACTOR;
	}
}
