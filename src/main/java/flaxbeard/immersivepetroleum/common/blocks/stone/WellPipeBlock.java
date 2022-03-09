package flaxbeard.immersivepetroleum.common.blocks.stone;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellPipeTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class WellPipeBlock extends IPBlockBase{
	
	public static final BooleanProperty BROKEN = BooleanProperty.create("broken");
	
	public WellPipeBlock(){
		super("well_pipe", Block.Properties.create(Material.ROCK, MaterialColor.OBSIDIAN).hardnessAndResistance(75.0F, 10.0F).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
		
		setDefaultState(getStateContainer().getBaseState().with(BROKEN, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder){
		builder.add(BROKEN);
	}
	
	@Override
	protected BlockItem createBlockItem(){
		// Nobody is supposed to have this in their inventory
		return null;
	}
	
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){
//		int d = pos.getY() - neighbor.getY();
//		if(d > 0 && world.getBlockState(pos.up()).getBlock() != this){
//		}
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving){
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos){
		float f = state.getBlockHardness(worldIn, pos);
		
		if(state.get(BROKEN)){
			f /= 5F;
		}
		
		if(f == -1.0F){
			return 0.0F;
		}else{
			int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(state, player, worldIn, pos) ? 30 : 100;
			return player.getDigSpeed(state, pos) / f / (float) i;
		}
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(state.hasTileEntity() && (!state.matchesBlock(newState.getBlock()) || !newState.hasTileEntity())){
			removed(state, world, pos);
			world.removeTileEntity(pos);
		}
	}
	
	private void removed(BlockState state, World world, BlockPos pos){
		if(world.isRemote){
			return;
		}
		
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof WellPipeTileEntity){
			WellTileEntity well = null;
			
			// TODO !Replace "y >= 0" in 1.18 with something that can go negative
			for(int y = pos.getY() - 1;y >= 0;y--){
				TileEntity teLow = world.getTileEntity(new BlockPos(pos.getX(), y, pos.getZ()));
				
				if(teLow instanceof WellTileEntity){
					well = (WellTileEntity) teLow;
					break;
				}
			}
			
			if(well != null && !well.pastPhyiscalPart){
				well.phyiscalPipesList.remove(Integer.valueOf(pos.getY()));
				if(well.wellPipeLength > 0){
					well.wellPipeLength -= 1;
				}
				well.markDirty();
			}
		}
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
