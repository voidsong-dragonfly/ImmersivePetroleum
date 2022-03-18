package flaxbeard.immersivepetroleum.client;

import blusunrize.immersiveengineering.common.register.IEBlocks.StoneDecoration;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IPCoreSampleModelHandler{
	public static IPCoreSampleModelHandler instance = new IPCoreSampleModelHandler();
	
	@SuppressWarnings("unused")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onModelBakeEvent(ModelBakeEvent event){
		
		ModelResourceLocation mLoc = new ModelResourceLocation(StoneDecoration.CORESAMPLE.get().getRegistryName(), "inventory");
		// event.getModelRegistry().put(mLoc, new ModelCoresampleExtended());
	}
}
