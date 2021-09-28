package flaxbeard.immersivepetroleum.client.gui;

import static flaxbeard.immersivepetroleum.ImmersivePetroleum.MODID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.gui.elements.PipeGrid;
import flaxbeard.immersivepetroleum.common.network.MessageDerrick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DerrickSettingsScreen extends Screen{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MODID, "textures/gui/derrick_settings.png");
	
	private int xSize = 158;
	private int ySize = 176;
	private int guiLeft;
	private int guiTop;
	private PipeGrid pipeGrid;
	
	final DerrickScreen derrickScreen;
	final BlockPos derrickWorldPos;
	public DerrickSettingsScreen(DerrickScreen derrickScreen){
		super(new StringTextComponent("DerrickSettings"));
		this.derrickScreen = derrickScreen;
		this.derrickWorldPos = derrickScreen.tile.getPos();
	}
	
	@Override
	protected void init(){
		this.width = this.minecraft.getMainWindow().getScaledWidth();
		this.height = this.minecraft.getMainWindow().getScaledHeight();
		
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		addButton(new Button(this.guiLeft + (this.xSize / 2) - 54, this.guiTop + this.ySize - 25, 50, 20, new StringTextComponent("Set"), b -> {
			ImmersivePetroleum.log.info("Button: Set");
			MessageDerrick.sendToServer(this.derrickWorldPos, this.pipeGrid.getGrid());
		}));
		
		addButton(new Button(this.guiLeft + (this.xSize / 2) + 5, this.guiTop + this.ySize - 25, 50, 20, new StringTextComponent("Close"), b -> {
			ImmersivePetroleum.log.info("Button: Close");
			DerrickSettingsScreen.this.closeScreen();
		}, (button, matrix, mx, my) -> {
			List<ITextComponent> list = new ArrayList<>();
			list.add(new StringTextComponent("§4§n§lBefore clicking"));
			list.add(new StringTextComponent("If you re-open this Config window it will not restore what has been set previously!"));
			GuiUtils.drawHoveringText(matrix, list, mx, my, width, height, -1, font);
		}));
		
		this.pipeGrid = new PipeGrid(new ColumnPos(this.derrickWorldPos), this.guiLeft + 10, this.guiTop + 10, 138, 138, 69, 69, 2);
		addButton(this.pipeGrid);
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
	public void onClose(){
		
	}
	
	@Override
	public void closeScreen(){
		this.minecraft.displayGuiScreen(this.derrickScreen);
		this.pipeGrid.dispose();
	}
	
	@Override
	public void resize(Minecraft minecraft, int width, int height){
		PipeGrid oldGrid = this.pipeGrid;
		super.resize(minecraft, width, height);
		this.pipeGrid.copyDataFrom(oldGrid);
		oldGrid.dispose();
	}
	
	private void background(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
		blit(matrix, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
	
	@Deprecated
	static class PButton extends AbstractButton{
		protected final List<ITextComponent> hoverText;
		protected final int iconX;
		protected final int iconY;
		protected int iconWidth = 10;
		protected int iconHeight = 10;
		protected Consumer<PButton> action;
		
		public PButton(int x, int y, int width, int height, int iconX, int iconY, Consumer<PButton> action, ITextComponent... hoverText){
			this(x, y, width, height, iconX, iconY, width, height, action, hoverText);
		}
		
		public PButton(int x, int y, int width, int height, int iconX, int iconY, int iconWidth, int iconHeight, Consumer<PButton> action, ITextComponent... hoverText){
			super(x, y, width, height, StringTextComponent.EMPTY);
			this.action = action;
			this.iconX = iconX;
			this.iconY = iconY;
			this.iconWidth = iconWidth;
			this.iconHeight = iconHeight;
			this.hoverText = (hoverText != null && hoverText.length > 0) ? Arrays.asList(hoverText) : null;
		}
		
		@Override
		public void renderWidget(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
			Minecraft.getInstance().getTextureManager().bindTexture(GUI_TEXTURE);
			blit(matrix, this.x, this.y, this.iconX, this.iconY, this.iconWidth, this.iconHeight);
			Minecraft.getInstance().fontRenderer.drawString(matrix, "X", this.x + 2, this.y + 2, 0);
			if(isHovered() && this.hoverText != null){
				int screenWidth = Minecraft.getInstance().getMainWindow().getScaledWidth();
				int screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
				FontRenderer font = Minecraft.getInstance().fontRenderer;
				fill(matrix, this.x, this.y + 1, this.x + this.iconWidth, this.y + this.iconHeight, 0x7FFFFFFF);
				GuiUtils.drawHoveringText(matrix, hoverText, this.x, this.y + 1, screenWidth, screenHeight, -1, font);
			}
		}
		
		@Override
		public void onPress(){
			this.action.accept(this);
		}
	}
}
