package flaxbeard.immersivepetroleum.client.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CHAMBER_A;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CHAMBER_B;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_INPUT;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_OUTPUT;

import java.util.ArrayList;
import java.util.List;

import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import flaxbeard.immersivepetroleum.client.gui.elements.CokerChamberInfoArea;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CokingChamber;
import flaxbeard.immersivepetroleum.common.gui.CokerUnitContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.GuiUtils;

import javax.annotation.Nonnull;

public class CokerUnitScreen extends IEContainerScreen<CokerUnitContainer>{
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/coker.png");
	
	CokerUnitTileEntity tile;
	public CokerUnitScreen(CokerUnitContainer inventorySlotsIn, Inventory inv, Component title){
		super(inventorySlotsIn, inv, title, GUI_TEXTURE);
		this.tile = menu.tile;
		
		this.imageWidth = 200;
		this.imageHeight = 187;
	}

	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(
						tile.bufferTanks[TANK_INPUT],
						new Rect2i(leftPos + 32, topPos + 14, 16, 47),
						202, 2, 16, 47,
						GUI_TEXTURE
				),
				new FluidInfoArea(
						tile.bufferTanks[TANK_OUTPUT],
						new Rect2i(leftPos + 152, topPos + 14, 16, 47),
						202, 2, 16, 47,
						GUI_TEXTURE
				),
				new EnergyInfoArea(leftPos + 168, topPos + 67, tile.energyStorage),
				new CokerChamberInfoArea(tile.chambers[CHAMBER_A], new Rect2i(leftPos + 74, topPos + 24, 6, 38)),
				new CokerChamberInfoArea(tile.chambers[CHAMBER_B], new Rect2i(leftPos + 120, topPos + 24, 6, 38))
		);
	}
}
