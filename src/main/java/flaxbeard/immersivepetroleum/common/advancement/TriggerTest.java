package flaxbeard.immersivepetroleum.common.advancement;

import com.google.gson.JsonObject;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class TriggerTest extends SimpleCriterionTrigger<TriggerTest.TriggerInstance>{
	// TODO Definitely make a shorter name Lol
	static final ResourceLocation ID = ResourceUtils.ip("threesixty_no_scope_skelly_kill_while_in_motorboat");
	
	@Override
	public ResourceLocation getId(){
		return ID;
	}
	
	@Override
	protected TriggerInstance createInstance(JsonObject pJson, Composite pPlayer, DeserializationContext pContext){
		return null;
	}
	
	public static class TriggerInstance extends AbstractCriterionTriggerInstance{
		public TriggerInstance(EntityPredicate.Composite pPlayer){
			super(ID, pPlayer);
		}

		public static TriggerTest.TriggerInstance create(){
			KilledTrigger.TriggerInstance.playerKilledEntity(
					EntityPredicate.Builder.entity().of(EntityType.SKELETON),
					DamageSourcePredicate.Builder.damageType().isProjectile(true)
				);
			
			return null;
		}
	}
}
