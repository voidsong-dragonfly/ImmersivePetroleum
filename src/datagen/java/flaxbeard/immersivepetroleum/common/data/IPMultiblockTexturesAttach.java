package flaxbeard.immersivepetroleum.common.data;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
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
		
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "multiblock/cokerunit"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "multiblock/derrick"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "multiblock/distillation_tower"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "multiblock/distillation_tower_active"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "multiblock/hydrotreater"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "multiblock/oiltank"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "multiblock/pumpjack_base"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "models/lubricator"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "models/pumpjack_armature"), Optional.empty()));
		blockAtlas.addSource(new SingleFile(new ResourceLocation(ImmersivePetroleum.MODID, "projectors/projector"), Optional.empty()));
	}
}
