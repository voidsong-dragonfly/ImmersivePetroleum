package flaxbeard.immersivepetroleum.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public abstract class IPTemplateMultiblock extends TemplateMultiblock{
	private final MultiblockRegistration<?> logic;
	
	public IPTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic){
		super(loc, masterFromOrigin, triggerFromOrigin, size);
		this.logic = logic;
	}
	
	@Override
	protected void replaceStructureBlock(StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster){
	}
	
	public Vec3i getSize(){
		return this.size;
	}
	
	public ResourceLocation getBlockName(){
		return this.logic.id();
	}
	
	@Override
	public Component getDisplayName(){
		return this.logic.block().get().getName();
	}
	
	@Override
	public Block getBlock(){
		return this.logic.block().get();
	}
	
	@Deprecated(forRemoval = true)
	public Block getBaseBlock(){
		throw new UnsupportedOperationException();
	}
}
