package flaxbeard.immersivepetroleum.common.data;

import flaxbeard.immersivepetroleum.common.data.loot.IPLootGenerator;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPDataGenerator{
	public static final Logger log = LogManager.getLogger(ImmersivePetroleum.MODID + "/DataGenerator");
	
	@SubscribeEvent
	public static void generate(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper exhelper = event.getExistingFileHelper();
		StaticTemplateManager.EXISTING_HELPER = exhelper;
		
		if(event.includeServer()){
			IPBlockTags blockTags = new IPBlockTags(generator, exhelper);
			generator.addProvider(blockTags);
			generator.addProvider(new IPItemTags(generator, blockTags, exhelper));
			generator.addProvider(new IPFluidTags(generator, exhelper));
			generator.addProvider(new IPLootGenerator(generator));
			generator.addProvider(new IPRecipes(generator));
		}
		
		if(event.includeClient()){
			generator.addProvider(new IPBlockStates(generator, exhelper));
			generator.addProvider(new IPItemModels(generator, exhelper));
		}
	}
}
