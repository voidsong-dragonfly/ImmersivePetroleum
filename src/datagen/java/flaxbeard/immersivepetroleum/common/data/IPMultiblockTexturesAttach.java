package flaxbeard.immersivepetroleum.common.data;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class IPMultiblockTexturesAttach extends SpriteSourceProvider{
	public IPMultiblockTexturesAttach(PackOutput output, ExistingFileHelper fileHelper){
		super(output, fileHelper, ImmersivePetroleum.MODID);
	}
	
	@Override
	protected void addSources(){
		final SourceList blockAtlas = atlas(SpriteSourceProvider.BLOCKS_ATLAS);
		
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("multiblock/cokerunit"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("multiblock/derrick"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("multiblock/distillation_tower"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("multiblock/distillation_tower_active"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("multiblock/hydrotreater"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("multiblock/oiltank"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("multiblock/pumpjack_base"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("models/lubricator"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("models/pumpjack_armature"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(ResourceUtils.ip("projectors/projector"), Optional.empty()));
	}
}
