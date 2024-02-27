package flaxbeard.immersivepetroleum.common.data;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockAdvancementTrigger;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.data.AdvancementBuilder;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class IPAdvancements extends AdvancementProvider{
	
	public IPAdvancements(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper existingFileHelper){
		super(output, registries, existingFileHelper, List.of(IPAdvancements::registerAdvancements));
	}
	
	private static void registerAdvancements(HolderLookup.Provider lookup, Consumer<AdvancementHolder> consumer, ExistingFileHelper exHelper){
		AdvancementBuilder.setPage("main");
		
		
		AdvancementHolder start = Advancement.Builder.advancement()
			.display(IPContent.Blocks.SEISMIC_SURVEY.get(),
				Component.translatable("advancement.immersivepetroleum.root"),
				Component.translatable("advancement.immersivepetroleum.root.desc"),
				ResourceUtils.ip("textures/block/asphalt.png"),
				AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/root"), exHelper);
		
		AdvancementHolder derrick = advancement(start, IPContent.Multiblock.DERRICK.get(), "mb_derrick", AdvancementType.GOAL, true, true, false)
			.addCriterion("derrick", createMultiblockTrigger("derrick"))
			.save(consumer, ResourceUtils.ip("main/mb_derrick"), exHelper);
		
		AdvancementHolder pumpjack = advancement(derrick, IPContent.Multiblock.PUMPJACK.get(), "mb_pumpjack", AdvancementType.GOAL, true, true, false)
			.addCriterion("pumpjack", createMultiblockTrigger("pumpjack"))
			.save(consumer, ResourceUtils.ip("main/mb_pumpjack"), exHelper);
		
		advancement(derrick, IPContent.Multiblock.OILTANK.get(), "mb_oiltank", AdvancementType.GOAL, true, true, false)
			.addCriterion("oiltank", createMultiblockTrigger("oiltank"))
			.save(consumer, ResourceUtils.ip("main/mb_oiltank"), exHelper);
		
		AdvancementHolder tower = advancement(pumpjack, IPContent.Multiblock.DISTILLATIONTOWER.get(), "mb_distillationtower", AdvancementType.GOAL, true, true, false)
			.addCriterion("distillationtower", createMultiblockTrigger("distillationtower"))
			.save(consumer, ResourceUtils.ip("main/mb_distillationtower"), exHelper);
		
		AdvancementHolder bitumen = advancement(tower, IPContent.Items.BITUMEN.get(), "bitumen", AdvancementType.TASK, true, true, false)
			.addCriterion("bitumen", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Items.BITUMEN.get()))
			.save(consumer, ResourceUtils.ip("main/bitumen"), exHelper);
		
		AdvancementHolder cokerunit = advancement(bitumen, IPContent.Multiblock.COKERUNIT.get(), "mb_cokerunit", AdvancementType.GOAL, true, true, false)
			.addCriterion("cokerunit", createMultiblockTrigger("cokerunit"))
			.rewards(reward(10, ResourceUtils.ip("advancements/forming_coker_reward")))
			.save(consumer, ResourceUtils.ip("main/mb_cokerunit"), exHelper);
		
		advancement(cokerunit, IPContent.Items.PETCOKE.get(), "petcoke", AdvancementType.TASK, true, true, false)
			.addCriterion("petcoke", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Items.PETCOKE.get()))
			.save(consumer, ResourceUtils.ip("main/petcoke"), exHelper);
		
		AdvancementHolder hydrotreater = advancement(tower, IPContent.Multiblock.HYDROTREATER.get(), "mb_hydrotreater", AdvancementType.GOAL, true, true, false)
			.addCriterion("hydrotreater", createMultiblockTrigger("hydrotreater"))
			.save(consumer, ResourceUtils.ip("main/mb_hydrotreater"), exHelper);
		
		motorboat(consumer, tower, exHelper);
		
		advancement(tower, IPContent.Blocks.GAS_GENERATOR.get(), "gas_generator", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/gas_generator"), exHelper);
		
		advancement(tower, IPContent.Blocks.AUTO_LUBRICATOR.get(), "auto_lubricator", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/auto_lubricator"), exHelper);
		
		advancement(tower, IPContent.Items.OIL_CAN.get(), "oil_can", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/oil_can"), exHelper);
		
		advancement(start, IPContent.Items.PROJECTOR.get(), "projector", AdvancementType.TASK, true, true, false)
			.addCriterion("projector", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Items.PROJECTOR.get()))
			.save(consumer, ResourceUtils.ip("main/projector"), exHelper);
		
		advancement(start, IPContent.Blocks.FLARESTACK.get(), "flarestack", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/flarestack"), exHelper);
		
		AdvancementHolder cracking = advancement(hydrotreater, IPContent.Fluids.NAPHTHA_CRACKED.bucket().get(), "cracking", AdvancementType.TASK, true, true, false)
			.addCriterion("cracking", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.NAPHTHA_CRACKED.bucket().get()))
			.save(consumer, ResourceUtils.ip("main/cracking"), exHelper);
		
		AdvancementHolder naphtha_distillates = advancement(cracking, IPContent.Fluids.BENZENE.bucket().get(), "naphtha_distillates", AdvancementType.GOAL, true, true, false)
			.addCriterion("benzene", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.BENZENE.bucket().get()))
			.addCriterion("propylene", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.PROPYLENE.bucket().get()))
			.addCriterion("ethylene", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.ETHYLENE.bucket().get()))
			.save(consumer, ResourceUtils.ip("main/naphtha_distillates"), exHelper);
		
		advancement(naphtha_distillates, IEBlocks.StoneDecoration.DUROPLAST.get().asItem(), "duroplast", AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("benzene", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.BENZENE.bucket().get()))
			.addCriterion("propylene", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.PROPYLENE.bucket().get()))    //These are here to make sure the player made their duroplast from naphtha
			.addCriterion("ethylene", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.ETHYLENE.bucket().get()))
			.addCriterion("duroplast_block", InventoryChangeTrigger.TriggerInstance.hasItems(IEBlocks.StoneDecoration.DUROPLAST.get().asItem()))
			.save(consumer, ResourceUtils.ip("main/duroplast"), exHelper);
		
		AdvancementHolder kerosene = advancement(tower, IPContent.Fluids.GASOLINE_ADDITIVES.bucket().get(), "kerosene", AdvancementType.TASK, true, true, false)
			.addCriterion("kerosene", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.GASOLINE_ADDITIVES.bucket().get()))
			.save(consumer, ResourceUtils.ip("main/kerosene"), exHelper);
		
		AdvancementHolder gasoline = advancement(kerosene, IPContent.Fluids.GASOLINE.bucket().get(), "gasoline", AdvancementType.GOAL, true, true, false)
			.addCriterion("gasoline", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.GASOLINE.bucket().get()))
			.save(consumer, ResourceUtils.ip("main/gasoline"), exHelper);
		
		advancement(gasoline, IPContent.Fluids.NAPALM.bucket().get(), "napalm", AdvancementType.TASK, true, true, false)
			.addCriterion("napalm", InventoryChangeTrigger.TriggerInstance.hasItems(IPContent.Fluids.NAPALM.bucket().get()))
			.save(consumer, ResourceUtils.ip("main/napalm"), exHelper);
	}
	
	private static void motorboat(Consumer<AdvancementHolder> consumer, AdvancementHolder start, ExistingFileHelper exHelper){
		AdvancementHolder fill_motorboat = advancement(start, IPContent.Items.SPEEDBOAT.get(), "motorboat", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/motorboat"), exHelper);
		
		advancement(fill_motorboat, IPContent.BoatUpgrades.ICE_BREAKER.get(), "ice_breaker", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/ice_breaker"), exHelper);
		
		advancement(fill_motorboat, IPContent.BoatUpgrades.PADDLES.get(), "paddles", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/paddles"), exHelper);
		
		advancement(fill_motorboat, IPContent.BoatUpgrades.REINFORCED_HULL.get(), "reinforced_hull", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/reinforced_hull"), exHelper);
		
		advancement(fill_motorboat, IPContent.BoatUpgrades.RUDDERS.get(), "rudders", AdvancementType.CHALLENGE, true, true, false)
			.rewards(reward(50, ResourceUtils.ie("advancements/shader_rare")))
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/rudders"), exHelper);
		
		advancement(fill_motorboat, IPContent.BoatUpgrades.TANK.get(), "tank", AdvancementType.TASK, true, true, false)
			.addCriterion("code_trigger", CodeTrigger())
			.save(consumer, ResourceUtils.ip("main/tank"), exHelper);
	}
	
	static final ItemPredicate HAMMER = ItemPredicate.Builder.item().of(Tools.HAMMER).build();
	protected static Criterion<?> createMultiblockTrigger(String mbName){
		return MultiblockAdvancementTrigger.create(ResourceUtils.ip("multiblocks/" + mbName), HAMMER);
	}
	
	protected static Advancement.Builder advancement(AdvancementHolder parent, ItemLike display, String name, AdvancementType frame, boolean showToast, boolean announceToChat, boolean hidden){
		Advancement.Builder builder = Advancement.Builder.advancement()
			.parent(parent)
			.display(display,
				Component.translatable("advancement.immersivepetroleum." + name),
				Component.translatable("advancement.immersivepetroleum." + name + ".desc"),
			null, frame, showToast, announceToChat, hidden);
		return builder;
	}
	
	protected static Advancement.Builder advancement(AdvancementHolder parent, ItemStack display, String name, AdvancementType frame, boolean showToast, boolean announceToChat, boolean hidden){
		Advancement.Builder builder = Advancement.Builder.advancement()
			.parent(parent)
			.display(display,
				Component.translatable("advancement.immersivepetroleum." + name),
				Component.translatable("advancement.immersivepetroleum." + name + ".desc"),
			null, frame, showToast, announceToChat, hidden);
		return builder;
	}
	
	protected static AdvancementRewards reward(int exp, @Nullable ResourceLocation lootTable){
		List<ResourceLocation> loot = lootTable == null ? List.of() : List.of(lootTable);
		return new AdvancementRewards(exp, loot, List.of(), Optional.empty());
	}
	
	private static final Criterion<TriggerInstance> CodeTrigger(){
		return CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance());
	}
}
