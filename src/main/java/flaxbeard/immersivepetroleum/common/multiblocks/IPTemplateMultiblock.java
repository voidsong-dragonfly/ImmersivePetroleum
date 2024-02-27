package flaxbeard.immersivepetroleum.common.multiblocks;

import java.util.function.Supplier;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public abstract class IPTemplateMultiblock extends TemplateMultiblock{
	private final Supplier<? extends Block> baseState;
	
	public IPTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size){// , Supplier<? extends Block> baseState){
		super(loc, masterFromOrigin, triggerFromOrigin, size);//, new IEBlocks.BlockEntry<>(baseState.get()));
		this.baseState = () -> null;
	}
	
	@Deprecated(forRemoval = true)
	public Block getBaseBlock(){
		return baseState.get();
	}
}
