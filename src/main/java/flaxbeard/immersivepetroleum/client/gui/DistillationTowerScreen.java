package flaxbeard.immersivepetroleum.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.gui.info.MultitankArea;
import net.minecraft.client.renderer.Rect2i;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.gui.DistillationTowerContainer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.fluids.FluidStack;
import zsynthetic.FunctionIntTToBool;

import javax.annotation.Nonnull;

public class DistillationTowerScreen extends IEContainerScreen<DistillationTowerContainer>{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/distillation.png");
	
	DistillationTowerTileEntity tile;
	
	public DistillationTowerScreen(DistillationTowerContainer container, Inventory playerInventory, Component title){
		super(container, playerInventory, title, GUI_TEXTURE);
		this.tile = container.tile;
	}

	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(tile.tanks[0], new Rect2i(leftPos + 62, topPos + 21, 16, 47), 177, 31, 20, 51, GUI_TEXTURE),
				new EnergyInfoArea(leftPos + 158, topPos + 22, tile.energyStorage),
				new MultitankArea(new Rect2i(leftPos + 112, topPos + 21, 16, 47), tile.tanks[1])
		);
	}
}
