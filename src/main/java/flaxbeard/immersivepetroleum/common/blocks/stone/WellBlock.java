package flaxbeard.immersivepetroleum.common.blocks.stone;

import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class WellBlock extends IPBlockBase{
	
	public WellBlock(){
		super("reservoir_well", Block.Properties.create(Material.ROCK, MaterialColor.OBSIDIAN).hardnessAndResistance(150.0F, 3000.0F).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
	}
	
	@Override
	protected BlockItem createBlockItem(){
		// Nobody is supposed to have this in their inventory
		return null;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){
		WellTileEntity tile = IPTileTypes.WELL.get().create();
		return tile;
	}
}
