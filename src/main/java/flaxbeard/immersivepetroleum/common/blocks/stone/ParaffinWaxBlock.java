package flaxbeard.immersivepetroleum.common.blocks.stone;

import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class ParaffinWaxBlock extends IPBlockBase{
	
	public ParaffinWaxBlock(){
		super(Properties.of(Material.ICE_SOLID, MaterialColor.COLOR_YELLOW).strength(0.5F, 0.4F).sound(SoundType.HONEY_BLOCK).speedFactor(0.95F).friction(1.05F));
	}
	
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn){
		tooltip(stack, worldIn, tooltip, flagIn);
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
	static void tooltip(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("desc.immersivepetroleum.flavour.paraffin_wax").withStyle(ChatFormatting.GRAY));
	}
}
