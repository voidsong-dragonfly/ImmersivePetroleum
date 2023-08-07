package flaxbeard.immersivepetroleum.common.util.compat.jei;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class DistillationRecipeCategory extends IPRecipeCategory<DistillationTowerRecipe>{
	public static final ResourceLocation ID = ResourceUtils.ip("distillation");
	
	private final IDrawableStatic tankOverlay;
	public DistillationRecipeCategory(IGuiHelper guiHelper){
		super(DistillationTowerRecipe.class, guiHelper, ID, "block.immersivepetroleum.distillation_tower");
		ResourceLocation background = ResourceUtils.ip("textures/gui/distillation.png");
		setBackground(guiHelper.createDrawable(background, 51, 0, 81, 77));
		setIcon(new ItemStack(IPContent.Multiblock.DISTILLATIONTOWER.get()));
		this.tankOverlay = guiHelper.createDrawable(background, 177, 31, 20, 51);
	}
	
	@Override
	public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, DistillationTowerRecipe recipe, @Nonnull IFocusGroup focuses){
		int outputTotal = 0;
		List<FluidStack> list = recipe.getFluidOutputs();
		if(!list.isEmpty()){
			for(FluidStack f:list){
				outputTotal += f.getAmount();
			}

			int currentHeight = 0;
			for(FluidStack f:list){
				builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 21+currentHeight)
					.setFluidRenderer(f.getAmount(), false, 20, (int)(50*(((float)f.getAmount())/outputTotal)))
					.setOverlay(this.tankOverlay, 0, -(currentHeight+2))
					.addIngredient(ForgeTypes.FLUID_STACK, f);
				currentHeight += (int)(50*(((float)f.getAmount())/outputTotal));
			}
		}

		if(recipe.getInputFluid() != null){
			builder.addSlot(RecipeIngredientRole.INPUT, 9, 19)
				.setFluidRenderer(outputTotal, false, 20, 51)
				.setOverlay(this.tankOverlay, 0, 0)
				.addIngredients(ForgeTypes.FLUID_STACK, recipe.getInputFluid().getMatchingFluidStacks());
		}
	}
	
	@Override
	public void draw(@Nonnull DistillationTowerRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull PoseStack matrix, double mouseX, double mouseY){
	}
	
	@Override
	@Deprecated
	public ResourceLocation getUid(){
		return null;
	}
	
	@Override
	@Deprecated
	public Class<? extends DistillationTowerRecipe> getRecipeClass(){
		return null;
	}
}
