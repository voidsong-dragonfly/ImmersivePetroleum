package flaxbeard.immersivepetroleum.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.lib.manual.ManualElementCrafting;
import blusunrize.lib.manual.ManualElementTable;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.EntryData;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree.InnerNode;
import blusunrize.lib.manual.utils.ManualRecipeRef;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.api.crafting.FlarestackHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.api.energy.FuelHandler;
import flaxbeard.immersivepetroleum.client.gui.CokerUnitScreen;
import flaxbeard.immersivepetroleum.client.gui.DerrickScreen;
import flaxbeard.immersivepetroleum.client.gui.DistillationTowerScreen;
import flaxbeard.immersivepetroleum.client.gui.HydrotreaterScreen;
import flaxbeard.immersivepetroleum.client.render.SeismicResultRenderer;
import flaxbeard.immersivepetroleum.client.render.debugging.DebugRenderHandler;
import flaxbeard.immersivepetroleum.common.CommonProxy;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPMenuTypes;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.RecipeReloadListener;
import flaxbeard.immersivepetroleum.common.items.ProjectorItem;
import flaxbeard.immersivepetroleum.common.items.ProjectorItem.ClientInputHandler;
import flaxbeard.immersivepetroleum.common.multiblocks.CokerUnitMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.DistillationTowerMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.HydroTreaterMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.OilTankMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.PumpjackMultiblock;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientProxy extends CommonProxy{
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(ImmersivePetroleum.MODID + "/ClientProxy");
	public static final String CAT_IP = "ip";
	
	@Override
	public void setup(){
	}
	
	@Override
	public void registerContainersAndScreens(){
		MenuScreens.register(IPMenuTypes.DISTILLATION_TOWER.getType(), DistillationTowerScreen::new);
		MenuScreens.register(IPMenuTypes.COKER.getType(), CokerUnitScreen::new);
		MenuScreens.register(IPMenuTypes.DERRICK.getType(), DerrickScreen::new);
		MenuScreens.register(IPMenuTypes.HYDROTREATER.getType(), HydrotreaterScreen::new);
	}
	
	@Override
	public void completed(ParallelDispatchEvent event){
		event.enqueueWork(() -> ManualHelper.addConfigGetter(str -> switch(str){
			case "distillationtower_operationcost" -> (int) (2048 * IPServerConfig.REFINING.distillationTower_energyModifier.get());
			case "coker_operationcost" -> (int) (1024 * IPServerConfig.REFINING.cokerUnit_energyModifier.get());
			case "hydrotreater_operationcost" -> (int) (512 * IPServerConfig.REFINING.hydrotreater_energyModifier.get());
			case "pumpjack_consumption" -> IPServerConfig.EXTRACTION.pumpjack_consumption.get();
			case "pumpjack_speed" -> IPServerConfig.EXTRACTION.pumpjack_speed.get();
			case "pumpjack_days" -> {
				int oil_min = 1000000;
				int oil_max = 5000000;
				for (Reservoir reservoir : Reservoir.map.values()){
					if(reservoir.name.equals("oil")){
						oil_min = reservoir.minSize;
						oil_max = reservoir.maxSize;
						break;
					}
				}
				
				float averageSize = (oil_min + oil_max) / 2F;
				float pumpspeed = IPServerConfig.EXTRACTION.pumpjack_speed.get();
				yield Mth.floor((averageSize / pumpspeed) / 24000F);
			}
			case "autolubricant_speedup" -> 1.25D;
			case "portablegenerator_flux" -> FuelHandler.getFluxGeneratedPerTick(IPContent.Fluids.GASOLINE.still().get());
			default -> {
				// Last resort
				Config cfg = IPServerConfig.getRawConfig();
				if(cfg.contains(str)){
					yield cfg.get(str);
				}
				yield null;
			}
		}));
		
		setupManualPages();
	}
	
	@Override
	public void preInit(){
	}
	
	@Override
	public void preInitEnd(){
	}
	
	@Override
	public void init(){
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new RecipeReloadListener(null));
		
		MinecraftForge.EVENT_BUS.register(new DebugRenderHandler());
		MinecraftForge.EVENT_BUS.register(new SeismicResultRenderer());
		
		ProjectorItem.ClientInputHandler.keybind_preview_flip.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(ClientInputHandler.keybind_preview_flip);
	}
	
	@Override
	public void renderTile(BlockEntity te, VertexConsumer iVertexBuilder, PoseStack transform, MultiBufferSource buffer){
		BlockEntityRenderer<BlockEntity> tesr = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(te);
		
		// Crash prevention
		if(tesr == null) return;
		
		if(te instanceof PumpjackTileEntity pumpjack){
			transform.pushPose();
			transform.mulPose(new Quaternion(0, -90, 0, true));
			transform.translate(1, 1, -2);
			
			float pt = 0;
			if(MCUtil.getPlayer() != null){
				pumpjack.activeTicks = MCUtil.getPlayer().tickCount;
				pt = Minecraft.getInstance().getFrameTime();
			}
			
			tesr.render(pumpjack, pt, transform, buffer, 0xF000F0, 0);
			transform.popPose();
		}else{
			transform.pushPose();
			transform.mulPose(new Quaternion(0, -90, 0, true));
			transform.translate(0, 1, -4);
			
			tesr.render(te, 0, transform, buffer, 0xF000F0, 0);
			transform.popPose();
		}
	}
	
	@Override
	public void drawUpperHalfSlab(PoseStack transform, ItemStack stack){
		
		// Render slabs on top half
		BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		BlockState state = IEBlocks.MetalDecoration.STEEL_SCAFFOLDING.get(MetalScaffoldingType.STANDARD).defaultBlockState();
		BakedModel model = blockRenderer.getBlockModelShaper().getBlockModel(state);
		
		MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		
		transform.pushPose();
		transform.translate(0.0F, 0.5F, 1.0F);
		blockRenderer.getModelRenderer().renderModel(transform.last(), buffers.getBuffer(RenderType.solid()), state, model, 1.0F, 1.0F, 1.0F, -1, -1, EmptyModelData.INSTANCE);
		transform.popPose();
	}
	
	@Override
	public Level getClientWorld(){
		return MCUtil.getLevel();
	}
	
	@Override
	public Player getClientPlayer(){
		return MCUtil.getPlayer();
	}
	
	@Override
	public void handleEntitySound(SoundEvent soundEvent, Entity entity, boolean active, float volume, float pitch){
		// TODO Sound: Restore motorboat audio
	}
	
	@Override
	public void handleTileSound(SoundEvent soundEvent, BlockEntity te, boolean active, float volume, float pitch){
		// TODO Sound: Perhaps give some MBs some audio
	}
	
	private static ManualRecipeRef[][] singleRecipeRef(ItemStack stack){
		return new ManualRecipeRef[][]{{new ManualRecipeRef(stack)}};
	}
	
	/** ImmersivePetroleum's Manual Category */
	private static InnerNode<ResourceLocation, ManualEntry> IP_CATEGORY;
	
	public void setupManualPages(){
		ManualInstance man = ManualHelper.getManual();
		
		IP_CATEGORY = man.getRoot().getOrCreateSubnode(ResourceUtils.ip("main"), 100);
		
		int priority = 0;
		
		pumpjack(ResourceUtils.ip("pumpjack"), priority++);
		distillation(ResourceUtils.ip("distillationtower"), priority++);
		coker(ResourceUtils.ip("cokerunit"), priority++);
		hydrotreater(ResourceUtils.ip("hydrotreater"), priority++);
		derrick(ResourceUtils.ip("derrick"), priority++);
		oiltank(ResourceUtils.ip("oiltank"), priority++);
		
		handleReservoirManual(ResourceUtils.ip("reservoir"), priority++);
		
		lubricant(ResourceUtils.ip("lubricant"), priority++);
		man.addEntry(IP_CATEGORY, ResourceUtils.ip("asphalt"), priority++);
		projector(ResourceUtils.ip("projector"), priority++);
		speedboat(ResourceUtils.ip("speedboat"), priority++);
		man.addEntry(IP_CATEGORY, ResourceUtils.ip("napalm"), priority++);
		generator(ResourceUtils.ip("portablegenerator"), priority++);
		autolube(ResourceUtils.ip("automaticlubricator"), priority++);
		flarestack(ResourceUtils.ip("flarestack"), priority++);
	}
	
	private static void flarestack(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("flarestack0", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.Blocks.FLARESTACK.get())))));
		builder.addSpecialElement(new SpecialElementData("flarestack1", 0, () -> {
			Set<TagKey<Fluid>> fluids = FlarestackHandler.getSet();
			List<Component[]> list = new ArrayList<>();
			for(TagKey<Fluid> tag:fluids){
				ResourceLocation rl = tag.registry().location();
				Fluid f = ForgeRegistries.FLUIDS.getValue(rl);
				Component[] entry = new Component[]{TextComponent.EMPTY, new FluidStack(f, 1).getDisplayName()};
				
				list.add(entry);
			}
			
			return new ManualElementTable(man, list.toArray(new Component[0][]), false);
		}));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	private static void autolube(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("automaticlubricator0", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.Blocks.AUTO_LUBRICATOR.get())))));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	private static void generator(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("portablegenerator0", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.Blocks.GAS_GENERATOR.get())))));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	private static void speedboat(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("speedboat0", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.Items.SPEEDBOAT.get())))));
		builder.addSpecialElement(new SpecialElementData("speedboat1", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.BoatUpgrades.TANK.get())))));
		builder.addSpecialElement(new SpecialElementData("speedboat2", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.BoatUpgrades.RUDDERS.get())))));
		builder.addSpecialElement(new SpecialElementData("speedboat3", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.BoatUpgrades.ICE_BREAKER.get())))));
		builder.addSpecialElement(new SpecialElementData("speedboat4", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.BoatUpgrades.REINFORCED_HULL.get())))));
		builder.addSpecialElement(new SpecialElementData("speedboat5", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.BoatUpgrades.PADDLES.get())))));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	private static void lubricant(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("lubricant1", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.Items.OIL_CAN.get())))));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	private static void pumpjack(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("pumpjack0", 0, () -> new ManualElementMultiblock(man, PumpjackMultiblock.INSTANCE)));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	private static void distillation(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("distillationtower0", 0, () -> new ManualElementMultiblock(man, DistillationTowerMultiblock.INSTANCE)));
		builder.addSpecialElement(new SpecialElementData("distillationtower1", 0, () -> {
			Collection<DistillationRecipe> recipeList = DistillationRecipe.recipes.values();
			List<Component[]> list = new ArrayList<>();
			for(DistillationRecipe recipe:recipeList){
				boolean first = true;
				for(FluidStack output:recipe.getFluidOutputs()){
					Component outputName = output.getDisplayName();
					
					Component[] entry = new Component[]{
							first ? new TextComponent(recipe.getInputFluid().getAmount() + "mB ").append(recipe.getInputFluid().getMatchingFluidStacks().get(0).getDisplayName()) : TextComponent.EMPTY,
									new TextComponent(output.getAmount() + "mB ").append(outputName)
					};
					
					list.add(entry);
					first = false;
				}
			}
			
			return new ManualElementTable(man, list.toArray(new Component[0][]), false);
		}));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	protected static void coker(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("cokerunit0", 0, () -> new ManualElementMultiblock(man, CokerUnitMultiblock.INSTANCE)));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	protected static void hydrotreater(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("hydrotreater0", 0, () -> new ManualElementMultiblock(man, HydroTreaterMultiblock.INSTANCE)));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	protected static void derrick(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("derrick0", 0, () -> new ManualElementMultiblock(man, DerrickMultiblock.INSTANCE)));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	protected static void oiltank(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("oiltank0", 0, () -> new ManualElementMultiblock(man, OilTankMultiblock.INSTANCE)));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	protected static void projector(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ItemStack projectorWithNBT = new ItemStack(IPContent.Items.PROJECTOR.get());
		ItemNBTHelper.putString(projectorWithNBT, "multiblock", IEMultiblocks.ARC_FURNACE.getUniqueName().toString());
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.addSpecialElement(new SpecialElementData("projector0", 0, new ManualElementCrafting(man, singleRecipeRef(new ItemStack(IPContent.Items.PROJECTOR.get())))));
		builder.addSpecialElement(new SpecialElementData("projector1", 0, new ManualElementCrafting(man, singleRecipeRef(projectorWithNBT))));
		builder.readFromFile(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	private static void handleReservoirManual(ResourceLocation location, int priority){
		ManualInstance man = ManualHelper.getManual();
		
		ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
		builder.setContent(ClientProxy::createContent);
		builder.setLocation(location);
		man.addEntry(IP_CATEGORY, builder.create(), priority);
	}
	
	protected static EntryData createContent(){
		ArrayList<ItemStack> list = new ArrayList<>();
		final Reservoir[] reservoirs = Reservoir.map.values().toArray(new Reservoir[0]);
		
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append(I18n.get("ie.manual.entry.reservoirs.oil0"));
		contentBuilder.append(I18n.get("ie.manual.entry.reservoirs.oil1"));
		
		for(int i = 0;i < reservoirs.length;i++){
			Reservoir reservoir = reservoirs[i];
			
			ImmersivePetroleum.log.debug("Creating entry for " + reservoir);
			
			String name = "desc.immersivepetroleum.info.reservoir." + reservoir.name;
			String localizedName = I18n.get(name);
			if(localizedName.equalsIgnoreCase(name))
				localizedName = reservoir.name;
			
			char c = localizedName.toLowerCase().charAt(0);
			boolean isVowel = (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u');
			String aOrAn = I18n.get(isVowel ? "ie.manual.entry.reservoirs.vowel" : "ie.manual.entry.reservoirs.consonant");
			
			String dimBLWL = "";
			if(reservoir.dimWhitelist != null && reservoir.dimWhitelist.size() > 0){
				StringBuilder validDims = new StringBuilder();
				for(ResourceLocation rl:reservoir.dimWhitelist){
					validDims.append((validDims.length() > 0) ? ", " : "").append("<dim;").append(rl).append(">");
				}
				dimBLWL = I18n.get("ie.manual.entry.reservoirs.dim.valid", localizedName, validDims.toString(), aOrAn);
			}else if(reservoir.dimBlacklist != null && reservoir.dimBlacklist.size() > 0){
				StringBuilder invalidDims = new StringBuilder();
				for(ResourceLocation rl:reservoir.dimBlacklist){
					invalidDims.append((invalidDims.length() > 0) ? ", " : "").append("<dim;").append(rl).append(">");
				}
				dimBLWL = I18n.get("ie.manual.entry.reservoirs.dim.invalid", localizedName, invalidDims.toString(), aOrAn);
			}else{
				dimBLWL = I18n.get("ie.manual.entry.reservoirs.dim.any", localizedName, aOrAn);
			}
			
			String bioBLWL = "";
			if(reservoir.bioWhitelist != null && reservoir.bioWhitelist.size() > 0){
				StringBuilder validBiomes = new StringBuilder();
				for(ResourceLocation rl:reservoir.bioWhitelist){
					Biome bio = ForgeRegistries.BIOMES.getValue(rl);
					validBiomes.append((validBiomes.length() > 0) ? ", " : "").append(bio != null ? bio.toString() : rl);
				}
				bioBLWL = I18n.get("ie.manual.entry.reservoirs.bio.valid", validBiomes.toString());
			}else if(reservoir.bioBlacklist != null && reservoir.bioBlacklist.size() > 0){
				StringBuilder invalidBiomes = new StringBuilder();
				for(ResourceLocation rl:reservoir.bioBlacklist){
					Biome bio = ForgeRegistries.BIOMES.getValue(rl);
					invalidBiomes.append((invalidBiomes.length() > 0) ? ", " : "").append(bio != null ? bio.toString() : rl);
				}
				bioBLWL = I18n.get("ie.manual.entry.reservoirs.bio.invalid", invalidBiomes.toString());
			}else{
				bioBLWL = I18n.get("ie.manual.entry.reservoirs.bio.any");
			}
			
			String fluidName = "";
			Fluid fluid = reservoir.getFluid();
			if(fluid != null){
				fluidName = new FluidStack(fluid, 1).getDisplayName().getString();
			}
			
			String repRate = "";
			if(reservoir.residual > 0){
				repRate = I18n.get("ie.manual.entry.reservoirs.replenish", reservoir.residual, fluidName);
			}
			contentBuilder.append(I18n.get("ie.manual.entry.reservoirs.content", dimBLWL, fluidName, Utils.fDecimal(reservoir.minSize), Utils.fDecimal(reservoir.maxSize), repRate, bioBLWL));
			
			if(i < (reservoirs.length - 1))
				contentBuilder.append("<np>");
			
			list.add(new ItemStack(fluid.getBucket()));
		}
		
		String translatedTitle = I18n.get("ie.manual.entry.reservoirs.title");
		String tanslatedSubtext = I18n.get("ie.manual.entry.reservoirs.subtitle");
		String formattedContent = contentBuilder.toString().replaceAll("\r\n|\r|\n", "\n");
		return new EntryData(translatedTitle, tanslatedSubtext, formattedContent, List.of());
	}
}
