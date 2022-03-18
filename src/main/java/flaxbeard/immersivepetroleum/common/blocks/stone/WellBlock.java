package flaxbeard.immersivepetroleum.common.blocks.stone;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class WellBlock extends IPBlockBase implements EntityBlock{
	public WellBlock(){
		super("well", Block.Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).noDrops().isValidSpawn((s, r, p, e) -> false));
	}
	
	@Override
	protected BlockItem createBlockItem(){
		// Nobody is supposed to have this in their inventory
		return null;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		WellTileEntity tile = IPTileTypes.WELL.get().create(pPos, pState);
		return tile;
	}
}
