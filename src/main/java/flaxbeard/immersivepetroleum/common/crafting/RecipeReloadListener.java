package flaxbeard.immersivepetroleum.common.crafting;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.api.crafting.HighPressureRefineryRecipe;
import flaxbeard.immersivepetroleum.api.crafting.IPRecipeTypes;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import flaxbeard.immersivepetroleum.client.render.dyn.DynamicTextureWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

public class RecipeReloadListener implements ResourceManagerReloadListener{
	private final ReloadableServerResources dataPackRegistries;
	public RecipeReloadListener(ReloadableServerResources dataPackRegistries){
		this.dataPackRegistries = dataPackRegistries;
	}
	
	@Override
	public void onResourceManagerReload(@Nonnull ResourceManager resourceManager){
		if(this.dataPackRegistries != null){
			updateLists(this.dataPackRegistries.getRecipeManager());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void recipesUpdated(RecipesUpdatedEvent event){
		if(!Minecraft.getInstance().hasSingleplayerServer()){
			updateLists(event.getRecipeManager());
		}
		
		DynamicTextureWrapper.clearCache();
	}
	
	static void updateLists(RecipeManager recipeManager){
		Collection<RecipeHolder<?>> recipes = recipeManager.getRecipes();
		if(recipes.size() == 0){
			return;
		}
		
		ImmersivePetroleum.log.info("Loading Distillation Recipes.");
		DistillationTowerRecipe.recipes = filterRecipes(recipes, IPRecipeTypes.DISTILLATION);
		
		ImmersivePetroleum.log.info("Loading Reservoirs.");
		ReservoirType.map = filterRecipes(recipes, IPRecipeTypes.RESERVOIR);
		
		ImmersivePetroleum.log.info("Loading Coker-Unit Recipes.");
		CokerUnitRecipe.recipes = filterRecipes(recipes, IPRecipeTypes.COKER);
		
		ImmersivePetroleum.log.info("Loading High-Pressure Refinery Recipes.");
		HighPressureRefineryRecipe.recipes = filterRecipes(recipes, IPRecipeTypes.HYDROTREATER);
	}
	
	static <R extends Recipe<?>> Map<ResourceLocation, R> filterRecipes(Collection<RecipeHolder<?>> recipes, IERecipeTypes.TypeWithClass<R> recipeType){
		return recipes.stream()
				.filter(holder -> holder.value().getType() == recipeType.type().get())
				.flatMap(Stream::of)
				.collect(Collectors.toMap(RecipeHolder::id, h -> recipeType.recipeClass().cast(h.value())));
	}
}
