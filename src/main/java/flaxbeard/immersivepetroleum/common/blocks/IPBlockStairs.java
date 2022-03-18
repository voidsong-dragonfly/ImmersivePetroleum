package flaxbeard.immersivepetroleum.common.blocks;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.StairBlock;

public class IPBlockStairs<B extends IPBlockBase> extends StairBlock{
	public IPBlockStairs(B base){
		super(base::defaultBlockState, Properties.copy(base));
	}
}
