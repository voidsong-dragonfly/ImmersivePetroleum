package flaxbeard.immersivepetroleum.common.blocks.stone;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellPipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class WellPipeBlock extends IPBlockBase{
	public WellPipeBlock(){
		super("well_pipe", Block.Properties.create(Material.ROCK, MaterialColor.OBSIDIAN).hardnessAndResistance(150.0F, 3000.0F).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
	}
	
	@Override
	protected BlockItem createBlockItem(){
		// Nobody is supposed to have this in their inventory
		return null;
	}
	
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){
		int d = pos.getY() - neighbor.getY();
		if(d > 0 && world.getBlockState(pos.up()).getBlock() != this){
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving){
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){
		WellPipeTileEntity tile = IPTileTypes.WELL_PIPE.get().create();
		return tile;
	}
}
