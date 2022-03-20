package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.function.Supplier;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

// TODO this is a hack, ideally we'd remove the non-API dependency here
public abstract class IPTemplateMultiblock extends IETemplateMultiblock{
	private final Supplier<? extends Block> baseState;
	
	public IPTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, Supplier<? extends Block> baseState){
		// TODO check that this doesn't run too early to query the RegObject
		super(loc, masterFromOrigin, triggerFromOrigin, size, new IEBlocks.BlockEntry<>(baseState.get()));
		this.baseState = baseState;
	}
	
	public Block getBaseBlock(){
		return baseState.get();
	}
}
