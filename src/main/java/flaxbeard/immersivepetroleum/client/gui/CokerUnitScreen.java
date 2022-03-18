package flaxbeard.immersivepetroleum.client.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CHAMBER_A;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CHAMBER_B;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_INPUT;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_OUTPUT;

import java.util.ArrayList;
import java.util.List;

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

public class CokerUnitScreen extends IEContainerScreen<CokerUnitContainer>{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation("immersivepetroleum", "textures/gui/coker.png");
	
	CokerUnitTileEntity tile;
	public CokerUnitScreen(CokerUnitContainer inventorySlotsIn, Inventory inv, Component title){
		super(inventorySlotsIn, inv, title);
		this.tile = menu.tile;
		
		this.imageWidth = 200;
		this.imageHeight = 187;
	}
	
	@Override
	public void render(PoseStack matrix, int mx, int my, float partialTicks){
		this.renderBackground(matrix);
		super.render(matrix, mx, my, partialTicks);
		this.renderTooltip(matrix, mx, my);
		
		List<Component> tooltip = new ArrayList<>();
		
		// Buffer tank displays
		GuiHelper.handleGuiTank(matrix, tile.bufferTanks[TANK_INPUT], leftPos + 32, topPos + 14, 16, 47, 0, 0, 0, 0, mx, my, GUI_TEXTURE, tooltip);
		GuiHelper.handleGuiTank(matrix, tile.bufferTanks[TANK_OUTPUT], leftPos + 152, topPos + 14, 16, 47, 0, 0, 0, 0, mx, my, GUI_TEXTURE, tooltip);
		
		// Chamber Stats
		chamberDisplay(matrix, leftPos + 74, topPos + 24, 6, 38, CHAMBER_A, mx, my, partialTicks, tooltip);
		chamberDisplay(matrix, leftPos + 120, topPos + 24, 6, 38, CHAMBER_B, mx, my, partialTicks, tooltip);
		
		// Power Stored
		if(mx > leftPos + 167 && mx < leftPos + 175 && my > topPos + 66 && my < topPos + 88){
			tooltip.add(new TextComponent(tile.energyStorage.getEnergyStored() + "/" + tile.energyStorage.getMaxEnergyStored() + " IF"));
		}
		
		if(!tooltip.isEmpty()){
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, font);
		}
	}
	
	private void chamberDisplay(PoseStack matrix, int x, int y, int w, int h, int chamberId, int mx, int my, float partialTicks, List<Component> tooltip){
		CokingChamber chamber = tile.chambers[chamberId];
		
		// Vertical Bar for Content amount.
		ClientUtils.bindTexture(GUI_TEXTURE);
		int scale = 38;
		int off = (int) (chamber.getTotalAmount() / (float) chamber.getCapacity() * scale);
		this.blit(matrix, x, y + scale - off, 200, 51, 6, off);
		
		// Vertical Overlay to visualize progress
		off = (int)(chamber.getTotalAmount() > 0 ? scale * (chamber.getOutputAmount() / (float)chamber.getCapacity()) : 0);
		this.blit(matrix, x, y + scale - off, 206, 51 + (scale - off), 6, off);
		
		// Chamber Tank
		GuiHelper.handleGuiTank(matrix, chamber.getTank(), x, y, 6, 38, 0, 0, 0, 0, mx, my, GUI_TEXTURE, null);
		
		// Debugging Tooltip
		/*if((mx >= x && mx < x + w) && (my >= y && my < y + h)){
			float completed = chamber.getTotalAmount() > 0 ? 100 * (chamber.getOutputAmount() / (float)chamber.getTotalAmount()) : 0;
			
			tooltip.add(new StringTextComponent("State: " + chamber.getState().toString()));
			tooltip.add(new StringTextComponent("Content: " + chamber.getTotalAmount() + " / " + chamber.getCapacity()));
			tooltip.add(new StringTextComponent("Input: ").appendString(chamber.getInputItem().getDisplayName().getString()));
			tooltip.add(new StringTextComponent("Output: ").appendString(chamber.getOutputItem().getDisplayName().getString()));
			tooltip.add(new StringTextComponent(MathHelper.floor(completed) + "% Completed. (Raw: " + completed + ")"));
			
			tooltip.add(new StringTextComponent("-------------"));
			ClientUtils.handleGuiTank(matrix, chamber.getTank(), x, y, w, x, 0, 0, 0, 0, mx, my, GUI_TEXTURE, tooltip);
		}
		//*/
	}
	
	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mx, int my){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientUtils.bindTexture(GUI_TEXTURE);
		this.blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		GuiHelper.handleGuiTank(matrix, tile.bufferTanks[TANK_INPUT], leftPos + 32, topPos + 14, 16, 47, 202, 2, 16, 47, mx, my, GUI_TEXTURE, null);
		GuiHelper.handleGuiTank(matrix, tile.bufferTanks[TANK_OUTPUT], leftPos + 152, topPos + 14, 16, 47, 202, 2, 16, 47, mx, my, GUI_TEXTURE, null);
		
		int x = leftPos + 168;
		int y = topPos + 67;
		int stored = (int) (tile.energyStorage.getEnergyStored() / (float) tile.energyStorage.getMaxEnergyStored() * 21);
		fillGradient(matrix, x, y + 21 - stored, x + 7, y + 21, 0xffb51500, 0xff600b00);
	}
}
