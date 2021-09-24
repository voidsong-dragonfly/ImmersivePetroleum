package flaxbeard.immersivepetroleum.client.gui;

import static flaxbeard.immersivepetroleum.ImmersivePetroleum.MODID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DerrickSettingsScreen extends Screen{
	static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MODID, "textures/gui/derrick_settings.png");
	
	private int xSize = 135;
	private int ySize = 120;
	private int guiLeft;
	private int guiTop;
	
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
		
		addButton(new PButton(this.guiLeft + 243, this.guiTop + 4, 9, 9, 0, 101, button -> {
			DerrickSettingsScreen.this.closeScreen();
		}, new StringTextComponent("Return")));
		
		addButton(new Button(this.guiLeft + this.xSize / 2 - 54, this.guiTop + this.ySize - 25, 50, 20, new StringTextComponent("Confirm"), b -> {
		}));
		
		addButton(new Button(this.guiLeft + this.xSize / 2+5, this.guiTop + this.ySize - 25, 50, 20, new StringTextComponent("Cancel"), b -> {
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
		
		{
			int x = this.guiLeft + 33;
			int y = this.guiTop + 13;
			int w = 69;
			int h = 69;
			if(mx >= x && my >= y && mx < x + w && my < y + h){
				int px = (mx - x) - (w / 2);
				int py = (my - y) - (h / 2);
				
				if(!(px >= -2 && px <= 2 && py >= -2 && py <= 2)){
					tooltip.add(new StringTextComponent("X: " + px));
					tooltip.add(new StringTextComponent("Z: " + py));
					int xa = x + (mx - x);
					int ya = y + (my - y);
					AbstractGui.fill(matrix, xa, ya, xa + 1, ya + 1, 0x7FFFFFFF);
				}
			}
		}
		
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
	}
	
	private void background(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
		blit(matrix, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		
		this.minecraft.fontRenderer.drawString(matrix, "N", this.guiLeft + 65, this.guiTop + 4, 0);
		this.minecraft.fontRenderer.drawString(matrix, "E", this.guiLeft + 104, this.guiTop + 45, 0);
		this.minecraft.fontRenderer.drawString(matrix, "S", this.guiLeft + 65, this.guiTop + 85, 0);
		this.minecraft.fontRenderer.drawString(matrix, "W", this.guiLeft + 26, this.guiTop + 45, 0);
	}
	
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
