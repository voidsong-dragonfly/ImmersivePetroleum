package flaxbeard.immersivepetroleum.client.gui.elements;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class PipeGrid extends Button{
	static final Button.IPressable NO_ACTION = b -> {
	};
	
	private final int dynTextureWidth, dynTextureHeight;
	private final DynamicTexture gridTexture;
	private final RenderType gridTextureRenderType;
	
	protected Grid grid;
	protected int gridWidthScaled, gridHeightScaled;
	protected int gridScale;
	public PipeGrid(int x, int y, int width, int height, int gridWidth, int gridHeight, int gridScale){
		super(x, y, width, height, StringTextComponent.EMPTY, NO_ACTION);
		this.grid = new Grid(gridWidth, gridHeight);
		this.gridWidthScaled = gridWidth * gridScale;
		this.gridHeightScaled = gridHeight * gridScale;
		this.gridScale = gridScale;
		
		this.dynTextureWidth = gridWidth;
		this.dynTextureHeight = gridHeight;
		this.gridTexture = new DynamicTexture(this.dynTextureWidth, this.dynTextureHeight, true);
		ResourceLocation loc = Minecraft.getInstance().textureManager.getDynamicTextureLocation("pipegrid/" + this.hashCode(), this.gridTexture);
		this.gridTextureRenderType = RenderType.getText(loc);
		updateTexture();
		
		ImmersivePetroleum.log.info("Created PipeGrid({}, {}, {}, {}, {}, {}, {})[{}]", x, y, width, height, gridWidth, gridHeight, gridScale, this.hashCode());
	}
	
	public void setType(int gridX, int gridY, int value){
		if(valid(gridX, gridY)){
			this.grid.set(index(gridX, gridY), value);
		}
	}
	
	public int getType(int gridX, int gridY){
		if(valid(gridX, gridY)){
			return this.grid.get(index(gridX, gridY));
		}
		return 0;
	}
	
	private boolean valid(int gridX, int gridY){
		return (gridX >= 0 && gridX < this.grid.getWidth()) && (gridY >= 0 && gridY < this.grid.getHeight());
	}
	
	private int index(int gridX, int gridY){
		return this.grid.getWidth() * gridY + gridX;
	}
	
	private void drawLine(int xa, int ya, int xb, int yb, int value){
		xa = MathHelper.clamp(xa, 0, this.grid.getWidth() - 1);
		xb = MathHelper.clamp(xb, 0, this.grid.getWidth() - 1);
		ya = MathHelper.clamp(ya, 0, this.grid.getHeight() - 1);
		yb = MathHelper.clamp(yb, 0, this.grid.getHeight() - 1);
		
		int dx = xb - xa;
		int dy = yb - ya;
		
		int length = Math.abs(dx) >= Math.abs(dy) ? Math.abs(dx) : Math.abs(dy);
		float tx = dx / (float) length;
		float ty = dy / (float) length;
		
		float x = xa, y = ya;
		int lx = -1, ly = -1;
		for(int i = 0;i <= length;i++){
			int cx = Math.round(x);
			int cy = Math.round(y);
			
			if(!(lx == cx && ly == cy)){
				setType(cx, cy, value);
				lx = cx;
				ly = cy;
			}
			
			x += tx;
			y += ty;
		}
	}
	
	private void clearGrid(){
		for(int i = 0;i < this.grid.size();i++){
			this.grid.set(i, 0);
		}
	}
	
	public static final int EMPTY = 0x00;
	public static final int PIPE_NORMAL = 0x01;
	public static final int PIPE_PERFORATED = 0x02;
	public static final int PIPE_PERFORATED_FIXED = 0x03;
	
	public void updateTexture(){
		NativeImage image = this.gridTexture.getTextureData();
		int texCenterX = this.dynTextureWidth / 2;
		int texCenterY = this.dynTextureHeight / 2;
		for(int y = 0;y < this.dynTextureHeight;y++){
			for(int x = 0;x < this.dynTextureWidth;x++){
				int i = this.dynTextureWidth * y + x;
				
				int color = 0;
				switch(this.grid.get(i)){
					case EMPTY:{
						color = (i % 2 == 0) ? 0xFF373737 : 0xFF545454;
						if((x >= texCenterX - 2 && x <= texCenterX + 2) && (y >= texCenterY - 2 && y <= texCenterY + 2)){
							color = 0xFF000000;
						}
						break;
					}
					case PIPE_NORMAL:{
						color = 0xFF8CC5FF;
						break;
					}
					case PIPE_PERFORATED:{
						color = 0xFFFFFF5E;
						break;
					}
					case PIPE_PERFORATED_FIXED:{
						color = 0xFFFF5157;
						break;
					}
				}
				
				// that is a lie, it's ABGR!
				// figured that out by trial and error
				image.setPixelRGBA(x, y, toABGR(color));
			}
		}
		this.gridTexture.updateDynamicTexture();
	}
	
	private int toABGR(int argb){
		int a = argb & 0xFF000000;
		
		int r = argb >> 16 & 0xFF;
		int g = argb >> 8 & 0xFF;
		int b = argb & 0xFF;
		return a | b << 16 | g << 8 | r;
	}
	
	@Override
	public void render(MatrixStack matrix, int mx, int my, float partialTicks){
		Minecraft mc = Minecraft.getInstance();
		FontRenderer font = mc.fontRenderer;
		IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		
		IVertexBuilder builder = buffer.getBuffer(this.gridTextureRenderType);
		matrix.push();
		{
			matrix.translate(this.x, this.y, 0);
			Matrix4f mat = matrix.getLast().getMatrix();
			int x = this.dynTextureWidth * this.gridScale;
			int y = this.dynTextureHeight * this.gridScale;
			builder.pos(mat, 0, y, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(0.0F, 1.0F).lightmap(0xF000F0).endVertex();
			builder.pos(mat, x, y, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(1.0F, 1.0F).lightmap(0xF000F0).endVertex();
			builder.pos(mat, x, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(1.0F, 0.0F).lightmap(0xF000F0).endVertex();
			builder.pos(mat, 0, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(0.0F, 0.0F).lightmap(0xF000F0).endVertex();
		}
		matrix.pop();
		buffer.finish();
		
		List<ITextComponent> tooltip = new ArrayList<>();
		
		if((mx >= this.x && mx < (this.x + this.gridWidthScaled)) && (my >= this.y && my < (this.y + this.gridHeightScaled))){
			int x = (mx - this.x) / this.gridScale;
			int y = (my - this.y) / this.gridScale;
			
			int px = x - (this.grid.getWidth() / 2);
			int py = y - (this.grid.getHeight() / 2);
			
			if((px >= -2 && px <= 2) && (py >= -2 && py <= 2)){
				tooltip.add(new StringTextComponent("Center (Derrick)"));
			}else{
				String dir = "";
				if(py < 0){
					dir += "North";
				}else if(py > 0){
					dir += "South";
				}
				if(px < 0 || px > 0){
					if(dir.length() > 0){
						dir += "-";
					}
					
					if(px < 0){
						dir += "West";
					}else if(px > 0){
						dir += "East";
					}
				}
				
				tooltip.add(new StringTextComponent(dir));
			}
			
			tooltip.add(new StringTextComponent("X: " + px));
			tooltip.add(new StringTextComponent("Z: " + py));
			
			int i = getType(x, y);
			if(i > EMPTY){
				if(i == PIPE_NORMAL){
					tooltip.add(new StringTextComponent("Normal Pipe"));
				}else if(i == PIPE_PERFORATED){
					tooltip.add(new StringTextComponent("Perforated Pipe"));
				}else if(i == PIPE_PERFORATED_FIXED){
					tooltip.add(new StringTextComponent("Perforated Pipe §c(Fixed)§r"));
				}
			}
			
			int xa = this.x + (x * this.gridScale);
			int ya = this.y + (y * this.gridScale);
			AbstractGui.fill(matrix, xa, ya, xa + this.gridScale, ya + this.gridScale, 0x7FFFFFFF);
		}
		
		if(!tooltip.isEmpty()){
			int width = mc.getMainWindow().getScaledWidth();
			int height = mc.getMainWindow().getScaledHeight();
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, font);
		}
	}
	
	protected void onGridClick(int x, int y, int button){
		ImmersivePetroleum.log.info("onGridClick({}, {}, {})", x, y, button);
		
		if(button == 0){
			int type = getType(x, y);
			if((type == PIPE_NORMAL || type == PIPE_PERFORATED) && type != PIPE_PERFORATED_FIXED){
				int[] array = getPipeCount();
				if(type == PIPE_NORMAL && array[PIPE_PERFORATED] < 2){
					setType(x, y, PIPE_PERFORATED);
				}else{
					setType(x, y, PIPE_NORMAL);
				}
			}
		}else if(button == 1){
			clearGrid();
		}
		
		updateTexture();
	}
	
	private int[] getPipeCount(){
		int[] array = new int[4];
		for(int i = 0;i < this.grid.size();i++){
			int type = this.grid.get(i);
			array[type] += 1;
		}
		return array;
	}
	
	protected void onGridDrag(int x, int y, int dragX, int dragY, int button){
		ImmersivePetroleum.log.info("onGridDrag({}, {}, {}, {}, {})", x, y, dragX, dragY, button);
		
		clearGrid();
		drawLine(this.grid.getWidth() / 2, this.grid.getHeight() / 2, x, y, 1);
		setType(this.grid.getWidth() / 2, this.grid.getHeight() / 2, PIPE_PERFORATED_FIXED);
		setType(MathHelper.clamp(x, 0, this.grid.getWidth() - 1), MathHelper.clamp(y, 0, this.grid.getHeight() - 1), PIPE_PERFORATED_FIXED);
		updateTexture();
	}
	
	protected void onGridRelease(int x, int y, int button){
		ImmersivePetroleum.log.info("onGridRelease({}, {}, {})", x, y, button);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		if(isValidClickButton(button)){
			boolean flag = this.clicked(mouseX, mouseY);
			if(flag){
				this.playDownSound(Minecraft.getInstance().getSoundHandler());
				onGridClick((int) (mouseX - this.x) / this.gridScale, (int) (mouseY - this.y) / this.gridScale, button);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button){
		if(isValidClickButton(button)){
			onGridRelease((int) (mouseX - this.x) / this.gridScale, (int) (mouseY - this.y) / this.gridScale, button);
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY){
		if(isValidClickButton(button)){
			onGridDrag((int) (mouseX - this.x) / this.gridScale, (int) (mouseY - this.y) / this.gridScale, (int) dragX, (int) dragY, button);
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	protected boolean isValidClickButton(int button){
		return button == 0 || button == 1;
	}
	
	/** This has to be called at the end of its life! */
	public void dispose(){
		this.gridTexture.close();
		ImmersivePetroleum.log.info("Disposing GridPipe[{}] texture.", this.hashCode());
	}
	
	public PipeGrid copyDataFrom(PipeGrid other){
		if(other != null){
			this.gridScale = other.gridScale;
			this.grid = other.grid;
			updateTexture();
			
			ImmersivePetroleum.log.info("Copied data from GridPipe[{}] to GridPipe[{}].", other.hashCode(), this.hashCode());
		}
		return this;
	}
	
	public static class Grid{
		private int width, height;
		private byte[] array;
		public Grid(int width, int height){
			this.width = width;
			this.height = height;
			this.array = new byte[width * height];
		}
		
		public Grid(byte[] array, int width, int height){
			if(array == null)
				throw new NullPointerException("Null grid array");
			if(array.length != width * height)
				throw new IllegalStateException("Grid width and height dont match array. (" + (width * height) + " != " + array.length + ")");
			
			this.width = width;
			this.height = height;
			this.array = array;
		}
		
		public int getWidth(){
			return this.width;
		}
		
		public int getHeight(){
			return this.height;
		}
		
		public int size(){
			return this.array.length;
		}
		
		public void set(int x, int y, int value){
			set(index(x, y), value);
		}
		
		public void set(int index, int value){
			this.array[index] = (byte) (value & 0xFF);
		}
		
		/** returns "unsigned" byte */
		public int get(int x, int y){
			return get(index(x, y));
		}
		
		/** returns "unsigned" byte */
		public int get(int index){
			return ((int) this.array[index]) & 0xFF;
		}
		
		int index(int x, int y){
			return this.width * y + x;
		}
		
		public CompoundNBT toCompound(){
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("width", this.width);
			nbt.putInt("height", this.height);
			nbt.putByteArray("grid", this.array);
			return nbt;
		}
		
		public static Grid fromCompound(CompoundNBT nbt){
			int width = nbt.getInt("width");
			int height = nbt.getInt("height");
			byte[] array = nbt.getByteArray("grid");
			return new Grid(array, width, height);
		}
	}
}
