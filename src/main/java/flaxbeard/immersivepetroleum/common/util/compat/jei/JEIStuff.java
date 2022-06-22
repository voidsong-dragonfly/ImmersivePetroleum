package flaxbeard.immersivepetroleum.common.util.compat.jei;

import java.text.DecimalFormat;
import java.util.ArrayList;

import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.api.crafting.SulfurRecoveryRecipe;
import flaxbeard.immersivepetroleum.client.gui.CokerUnitScreen;
import flaxbeard.immersivepetroleum.client.gui.DistillationTowerScreen;
import flaxbeard.immersivepetroleum.client.gui.HydrotreaterScreen;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEIStuff implements IModPlugin{
	private static final ResourceLocation ID = ResourceUtils.ip("main");
	
	public static final DecimalFormat FORMATTER = new DecimalFormat("#.##");
	
	private RecipeType<DistillationRecipe> distillation_type;
	private RecipeType<CokerUnitRecipe> coker_type;
	private RecipeType<SulfurRecoveryRecipe> recovery_type;
	
	@Override
	public ResourceLocation getPluginUid(){
		return ID;
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration){
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		
		DistillationRecipeCategory distillation = new DistillationRecipeCategory(guiHelper);
		CokerUnitRecipeCategory coker = new CokerUnitRecipeCategory(guiHelper);
		SulfurRecoveryRecipeCategory recovery = new SulfurRecoveryRecipeCategory(guiHelper);
		
		this.distillation_type = distillation.getRecipeType();
		this.coker_type = coker.getRecipeType();
		this.recovery_type = recovery.getRecipeType();
		
		registration.addRecipeCategories(distillation, coker, recovery);
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration){
		registration.addRecipes(this.distillation_type, new ArrayList<>(DistillationRecipe.recipes.values()));
		registration.addRecipes(this.coker_type, new ArrayList<>(CokerUnitRecipe.recipes.values()));
		registration.addRecipes(this.recovery_type, new ArrayList<>(SulfurRecoveryRecipe.recipes.values()));
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(new ItemStack(IPContent.Multiblock.DISTILLATIONTOWER.get()), this.distillation_type);
		registration.addRecipeCatalyst(new ItemStack(IPContent.Multiblock.COKERUNIT.get()), this.coker_type);
		registration.addRecipeCatalyst(new ItemStack(IPContent.Multiblock.HYDROTREATER.get()), this.recovery_type);
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration){
		registration.addRecipeClickArea(DistillationTowerScreen.class, 85, 19, 18, 51, this.distillation_type);
		registration.addRecipeClickArea(CokerUnitScreen.class, 59, 21, 82, 67, this.coker_type);
		registration.addRecipeClickArea(HydrotreaterScreen.class, 55, 9, 32, 51, this.recovery_type);
	}
}
