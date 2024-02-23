package flaxbeard.immersivepetroleum.common.blocks.stone;

import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PetcokeBlock extends IPBlockBase{
	public PetcokeBlock(){
		super(stoneProperty().strength(2, 10).requiresCorrectToolForDrops());
	}
	
	@Override
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new IPBlockItemBase(this, new Item.Properties()){
			@Override
			public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType){
				return 32000;
			}
		};
	}
	
	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face){
		return 100;
	}
}
