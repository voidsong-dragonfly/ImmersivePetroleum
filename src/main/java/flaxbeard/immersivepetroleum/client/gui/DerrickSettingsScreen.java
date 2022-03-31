package flaxbeard.immersivepetroleum.client.gui;

import static flaxbeard.immersivepetroleum.ImmersivePetroleum.MODID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.client.ClientUtils;
import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.network.MessageDerrick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class DerrickSettingsScreen extends Screen{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MODID, "textures/gui/derrick_settings.png");
	
	private int xSize = 158;
	private int ySize = 176;
	private int guiLeft;
	private int guiTop;
	private PipeConfig pipeConfig;
	
	final DerrickScreen derrickScreen;
	public DerrickSettingsScreen(DerrickScreen derrickScreen){
		super(new TextComponent("DerrickSettings"));
		this.derrickScreen = derrickScreen;
	}
	
	@Override
	protected void init(){
		this.width = this.minecraft.getWindow().getGuiScaledWidth();
		this.height = this.minecraft.getWindow().getGuiScaledHeight();
		
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		this.pipeConfig = new PipeConfig(this.derrickScreen.tile, this.guiLeft + 10, this.guiTop + 10, 138, 138, 69, 69, 2);
		addRenderableWidget(this.pipeConfig);
		
		addRenderableWidget(new Button(this.guiLeft + (this.xSize / 2) - 65, this.guiTop + this.ySize - 25, 40, 20, new TextComponent("Set"), b -> {
			MessageDerrick.sendToServer(this.derrickScreen.tile.getBlockPos(), this.pipeConfig.getGrid());
		}, (button, matrix, mx, my) -> {
			List<Component> list = new ArrayList<>();
			list.add(new TextComponent("Applies the Path to Derrick"));
			renderTooltip(matrix, list, Optional.empty(), mx, my);
		}));
		
		addRenderableWidget(new Button(this.guiLeft + (this.xSize / 2) - 20, this.guiTop + this.ySize - 25, 40, 20, new TextComponent("Reload"), b -> {
			this.pipeConfig.reset(this.derrickScreen.tile);
		}, (button, matrix, mx, my) -> {
			List<Component> list = new ArrayList<>();
			list.add(new TextComponent("Loads the already saved config again."));
			renderTooltip(matrix, list, Optional.empty(), mx, my);
		}));
		
		addRenderableWidget(new Button(this.guiLeft + (this.xSize / 2) + 25, this.guiTop + this.ySize - 25, 40, 20, new TextComponent("Close"), b -> {
			DerrickSettingsScreen.this.onClose();
		}, (button, matrix, mx, my) -> {
			List<Component> list = new ArrayList<>();
			list.add(new TextComponent("Return to Derrick"));
			renderTooltip(matrix, list, Optional.empty(), mx, my);
		}));
	}
	
	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick){
		background(pPoseStack, pMouseX, pMouseY, pPartialTick);
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
	}
	
	@Override
	public boolean isPauseScreen(){
		return false;
	}

	@Override
	public void onClose(){
		this.minecraft.setScreen(this.derrickScreen);
		this.pipeConfig.dispose();
	}
	
	@Override
	public void resize(Minecraft minecraft, int width, int height){
		PipeConfig oldGrid = this.pipeConfig;
		super.resize(minecraft, width, height);
		this.pipeConfig.copyDataFrom(oldGrid);
		oldGrid.dispose();
	}
	
	private void background(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		ClientUtils.bindTexture(GUI_TEXTURE);
		blit(matrix, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
}
