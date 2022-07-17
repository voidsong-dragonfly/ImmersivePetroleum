package flaxbeard.immersivepetroleum.common.crafting;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.api.crafting.IPRecipeTypes;
import flaxbeard.immersivepetroleum.api.crafting.SulfurRecoveryRecipe;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.client.render.dyn.DynamicTextureWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

public class RecipeReloadListener implements ResourceManagerReloadListener{
	private final ReloadableServerResources dataPackRegistries;
	public RecipeReloadListener(ReloadableServerResources dataPackRegistries){
		this.dataPackRegistries = dataPackRegistries;
	}
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager){
		if(dataPackRegistries != null){
			lists(dataPackRegistries.getRecipeManager());
		}
		DynamicTextureWrapper.clearCache();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void recipesUpdated(RecipesUpdatedEvent event){
		if(!Minecraft.getInstance().hasSingleplayerServer()){
			lists(event.getRecipeManager());
		}
	}
	
	static void lists(RecipeManager recipeManager){
		Collection<Recipe<?>> recipes = recipeManager.getRecipes();
		if(recipes.size() == 0){
			return;
		}
		
		ImmersivePetroleum.log.info("Loading Distillation Recipes.");
		DistillationRecipe.recipes = filterRecipes(recipes, DistillationRecipe.class, IPRecipeTypes.DISTILLATION);
		
		ImmersivePetroleum.log.info("Loading Reservoirs.");
		Reservoir.map = filterRecipes(recipes, Reservoir.class, IPRecipeTypes.RESERVOIR);

		ImmersivePetroleum.log.info("Loading Coker-Unit Recipes.");
		CokerUnitRecipe.recipes = filterRecipes(recipes, CokerUnitRecipe.class, IPRecipeTypes.COKER);
		
		ImmersivePetroleum.log.info("Loading Sulfur Recovery Recipes.");
		SulfurRecoveryRecipe.recipes = filterRecipes(recipes, SulfurRecoveryRecipe.class, IPRecipeTypes.SULFUR_RECOVERY);
	}
	
	static <R extends Recipe<?>> Map<ResourceLocation, R> filterRecipes(
			Collection<Recipe<?>> recipes, Class<R> recipeClass, RegistryObject<RecipeType<R>> recipeType
	){
		return recipes.stream()
				.filter(iRecipe -> iRecipe.getType() == recipeType.get())
				.map(recipeClass::cast)
				.collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
	}
}
