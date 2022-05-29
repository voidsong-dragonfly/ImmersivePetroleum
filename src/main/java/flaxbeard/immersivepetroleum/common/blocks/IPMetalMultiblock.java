package flaxbeard.immersivepetroleum.common.blocks;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class IPMetalMultiblock<T extends MultiblockPartBlockEntity<T> & IPServerTickableTile & IPClientTickableTile> extends MetalMultiblockBlock<T>{
	private final MultiblockBEType<T> multiblockBEType;
	
	public IPMetalMultiblock(MultiblockBEType<T> te){
		super(te, Block.Properties.of(Material.METAL)
				.sound(SoundType.METAL)
				.strength(3, 15)
				.requiresCorrectToolForDrops()
				.isViewBlocking((state, blockReader, pos) -> false)
				.noOcclusion()
		);
		this.multiblockBEType = te;
	}
	
	@Override
	public <T2 extends BlockEntity> BlockEntityTicker<T2> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T2> type){
		return IPBlockBase.createTickerHelper(world.isClientSide, type, multiblockBEType.master());
	}
}
