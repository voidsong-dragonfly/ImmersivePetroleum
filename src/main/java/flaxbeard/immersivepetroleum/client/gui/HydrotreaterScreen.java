package flaxbeard.immersivepetroleum.client.gui;

import java.util.ArrayList;
import java.util.List;

import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import net.minecraft.client.renderer.Rect2i;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.gui.HydrotreaterContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.GuiUtils;

import javax.annotation.Nonnull;

public class HydrotreaterScreen extends IEContainerScreen<HydrotreaterContainer>{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/hydrotreater.png");
	
	HydrotreaterTileEntity tile;
	public HydrotreaterScreen(HydrotreaterContainer inventorySlotsIn, Inventory inv, Component title){
		super(inventorySlotsIn, inv, title, GUI_TEXTURE);
		this.tile = this.menu.tile;
		
		this.imageWidth = 140;
		this.imageHeight = 69;
	}

	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(
						this.tile.tanks[HydrotreaterTileEntity.TANK_INPUT_A],
						new Rect2i(this.leftPos + 34, this.topPos + 11, 16, 47),
						140, 0, 20, 51,
						GUI_TEXTURE
				),
				new FluidInfoArea(
						this.tile.tanks[HydrotreaterTileEntity.TANK_INPUT_B],
						new Rect2i(this.leftPos + 11, this.topPos + 11, 16, 47),
						140, 0, 20, 51,
						GUI_TEXTURE
				),
				new FluidInfoArea(
						this.tile.tanks[HydrotreaterTileEntity.TANK_OUTPUT],
						new Rect2i(this.leftPos + 92, this.topPos + 11, 16, 47),
						140, 0, 20, 51,
						GUI_TEXTURE
				),
				new EnergyInfoArea(leftPos + 122, topPos + 12, tile.energyStorage)
		);
	}
}
