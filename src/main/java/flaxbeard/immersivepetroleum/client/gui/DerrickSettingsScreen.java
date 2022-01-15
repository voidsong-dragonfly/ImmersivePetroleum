package flaxbeard.immersivepetroleum.client.gui;

import static flaxbeard.immersivepetroleum.ImmersivePetroleum.MODID;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.network.MessageDerrick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DerrickSettingsScreen extends Screen{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MODID, "textures/gui/derrick_settings.png");
	
	private int xSize = 158;
	private int ySize = 176;
	private int guiLeft;
	private int guiTop;
	private PipeConfig pipeConfig;
	
	final DerrickScreen derrickScreen;
	public DerrickSettingsScreen(DerrickScreen derrickScreen){
		super(new StringTextComponent("DerrickSettings"));
		this.derrickScreen = derrickScreen;
	}
	
	@Override
	protected void init(){
		this.width = this.minecraft.getMainWindow().getScaledWidth();
		this.height = this.minecraft.getMainWindow().getScaledHeight();
		
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		this.pipeConfig = new PipeConfig(this.derrickScreen.tile, this.guiLeft + 10, this.guiTop + 10, 138, 138, 69, 69, 2);
		addButton(this.pipeConfig);
		
		addButton(new Button(this.guiLeft + (this.xSize / 2) - 65, this.guiTop + this.ySize - 25, 40, 20, new StringTextComponent("Set"), b -> {
			MessageDerrick.sendToServer(this.derrickScreen.tile.getPos(), this.pipeConfig.getGrid());
		}, (button, matrix, mx, my) -> {
			List<ITextComponent> list = new ArrayList<>();
			list.add(new StringTextComponent("Applies the Path to Derrick"));
			GuiUtils.drawHoveringText(matrix, list, mx, my, this.width, this.height, -1, this.font);
		}));
		
		addButton(new Button(this.guiLeft + (this.xSize / 2) - 20, this.guiTop + this.ySize - 25, 40, 20, new StringTextComponent("Reload"), b -> {
			this.pipeConfig.reset(this.derrickScreen.tile);
		}, (button, matrix, mx, my) -> {
			List<ITextComponent> list = new ArrayList<>();
			list.add(new StringTextComponent("Loads the already saved config again."));
			GuiUtils.drawHoveringText(matrix, list, mx, my, this.width, this.height, -1, this.font);
		}));
		
		addButton(new Button(this.guiLeft + (this.xSize / 2) + 25, this.guiTop + this.ySize - 25, 40, 20, new StringTextComponent("Close"), b -> {
			DerrickSettingsScreen.this.closeScreen();
		}, (button, matrix, mx, my) -> {
			List<ITextComponent> list = new ArrayList<>();
			list.add(new StringTextComponent("Return to Derrick"));
			GuiUtils.drawHoveringText(matrix, list, mx, my, this.width, this.height, -1, this.font);
		}));
	}
	
	@Override
	public boolean isPauseScreen(){
		return false;
	}
	
	@Override
	public void render(MatrixStack matrix, int mx, int my, float partialTicks){
		List<ITextComponent> tooltip = new ArrayList<>();
		
		background(matrix, mx, my, partialTicks);
		super.render(matrix, mx, my, partialTicks);
		
		if(!tooltip.isEmpty()){
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, font);
		}
	}
	
	@Override
	public void closeScreen(){
		this.minecraft.displayGuiScreen(this.derrickScreen);
		this.pipeConfig.dispose();
	}
	
	@Override
	public void resize(Minecraft minecraft, int width, int height){
		PipeConfig oldGrid = this.pipeConfig;
		super.resize(minecraft, width, height);
		this.pipeConfig.copyDataFrom(oldGrid);
		oldGrid.dispose();
	}
	
	private void background(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
		blit(matrix, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
}
