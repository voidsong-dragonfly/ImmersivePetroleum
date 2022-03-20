package flaxbeard.immersivepetroleum.common.blocks;

import static flaxbeard.immersivepetroleum.common.blocks.IPBlockBase.createTickerHelper;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.TickableBE;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class IPMetalMultiblock<T extends MultiblockPartBlockEntity<T> & TickableBE> extends MetalMultiblockBlock<T>{
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
	
	/*
	public IPMetalMultiblock(String name, Supplier<BlockEntityType<T>> te){
		super(name, te);
		
		// Nessesary hacks
		if(!FMLLoader.isProduction()){
			IEContent.registeredIEBlocks.remove(this);
			Iterator<Item> it = IEContent.registeredIEItems.iterator();
			while(it.hasNext()){
				Item item = it.next();
				if(item instanceof BlockItemIE && ((BlockItemIE) item).getBlock() == this){
					it.remove();
					break;
				}
			}
		}
		
		IPContent.registeredIPBlocks.add(this);
		
		BlockItem bItem = new BlockItemIE(this, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
		IPContent.registeredIPItems.add(bItem.setRegistryName(getRegistryName()));
	}
	*/
	
	@Override
	public <T2 extends BlockEntity> BlockEntityTicker<T2> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T2> type){
		return createTickerHelper(type, multiblockBEType.master(), TickableBE.makeTicker());
	}
}
