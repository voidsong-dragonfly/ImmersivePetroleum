package flaxbeard.immersivepetroleum.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.render.DerrickRenderer;
import flaxbeard.immersivepetroleum.client.render.SeismicSurveyBarrelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

/** A central place for all of ImmersivePetroleums Models, including some OBJ Models */
@Mod.EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class IPModels{
	
	@SubscribeEvent
	public static void init(FMLConstructModEvent event){
		add(ModelPumpjack.ID, new ModelPumpjack());
		
		add(ModelLubricantPipes.Crusher.ID, new ModelLubricantPipes.Crusher());
		
		add(ModelLubricantPipes.Excavator.ID_NORMAL, new ModelLubricantPipes.Excavator(false));
		add(ModelLubricantPipes.Excavator.ID_MIRRORED, new ModelLubricantPipes.Excavator(true));
		
		add(ModelLubricantPipes.Pumpjack.ID_NORMAL, new ModelLubricantPipes.Pumpjack(false));
		add(ModelLubricantPipes.Pumpjack.ID_MIRRORED, new ModelLubricantPipes.Pumpjack(true));
	}
	
	@SubscribeEvent
	public static void registerDynamicOBJModels(ModelEvent.RegisterAdditional event){
		event.register(SeismicSurveyBarrelRenderer.BARREL);
		event.register(DerrickRenderer.DRILL);
		event.register(DerrickRenderer.PIPE_SEGMENT);
		event.register(DerrickRenderer.PIPE_TOP);
	}
	
	private static final Map<String, IPModel> MODELS = new HashMap<>();
	
	/**
	 * @param id    The String-ID of the Model.
	 * @param model The {@link IPModel} model
	 */
	public static void add(String id, IPModel model){
		if(MODELS.containsKey(id)){
			ImmersivePetroleum.log.error("Duplicate ID, \"{}\" already used by {}. Skipping.", id, MODELS.get(id).getClass());
		}else{
			model.init();
			MODELS.put(id, model);
		}
	}
	
	/**
	 * @param id The String-ID of the Model.
	 * @return The Model assigned to <code>id</code> or <code>null</code>
	 */
	public static Supplier<IPModel> getSupplier(String id){
		return () -> MODELS.get(id);
	}
	
	/**
	 * @return An unmodifiable collection of all added Models
	 */
	public static Collection<IPModel> getModels(){
		return Collections.unmodifiableCollection(MODELS.values());
	}
}
