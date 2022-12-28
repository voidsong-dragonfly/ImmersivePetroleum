package flaxbeard.immersivepetroleum.common.data;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.IPTags;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IPBlockTags extends BlockTagsProvider{
	public IPBlockTags(DataGenerator dataGen, ExistingFileHelper exFileHelper){
		super(dataGen, ImmersivePetroleum.MODID, exFileHelper);
	}
	
	@Override
	protected void addTags(){
		// IP Tags
		
		tag(IPTags.Blocks.asphalt).add(IPContent.Blocks.ASPHALT.get());
		tag(IPTags.Blocks.petcoke).add(IPContent.Blocks.PETCOKE.get());
		
		tag(IPTags.Blocks.waxBlock).add(IPContent.Blocks.PARAFFIN_WAX.get());
		tag(IPTags.Blocks.paraffinWaxBlock).add(IPContent.Blocks.PARAFFIN_WAX.get());
		
		// Minecraft Tags
		
		tag(BlockTags.STAIRS).add(IPContent.Blocks.ASPHALT_STAIR.get());
		tag(BlockTags.SLABS).add(IPContent.Blocks.ASPHALT_SLAB.get());
		
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
			IPContent.Blocks.ASPHALT.get(),
			IPContent.Blocks.ASPHALT_STAIR.get(),
			IPContent.Blocks.ASPHALT_SLAB.get(),
			IPContent.Blocks.PETCOKE.get(),
			IPContent.Blocks.GAS_GENERATOR.get(),
			IPContent.Blocks.FLARESTACK.get(),
			IPContent.Blocks.WELL_PIPE.get(),
			IPContent.Blocks.SEISMIC_SURVEY.get(),
			//MBs
			IPContent.Multiblock.DERRICK.get(),
			IPContent.Multiblock.PUMPJACK.get(),
			IPContent.Multiblock.OILTANK.get(),
			IPContent.Multiblock.DISTILLATIONTOWER.get(),
			IPContent.Multiblock.COKERUNIT.get(),
			IPContent.Multiblock.HYDROTREATER.get()
		);
		
		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(IPContent.Blocks.PARAFFIN_WAX.get());
		
		tag(BlockTags.MINEABLE_WITH_AXE).add(
			IPContent.Blocks.AUTO_LUBRICATOR.get()
		);
		
		tag(BlockTags.NEEDS_STONE_TOOL).add(
			IPContent.Blocks.ASPHALT.get(),
			IPContent.Blocks.ASPHALT_STAIR.get(),
			IPContent.Blocks.ASPHALT_SLAB.get(),
			IPContent.Blocks.PETCOKE.get(),
			IPContent.Blocks.WELL_PIPE.get(),
			IPContent.Blocks.AUTO_LUBRICATOR.get()
		);
		
		tag(BlockTags.NEEDS_IRON_TOOL).add(
			IPContent.Blocks.GAS_GENERATOR.get(),
			IPContent.Blocks.FLARESTACK.get(),
			IPContent.Blocks.SEISMIC_SURVEY.get()
		);
	}
}
