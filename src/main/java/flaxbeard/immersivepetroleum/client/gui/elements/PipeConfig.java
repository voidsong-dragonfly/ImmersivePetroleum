package flaxbeard.immersivepetroleum.client.gui.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.cfg.IPClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class PipeConfig extends Button{
	static final Button.IPressable NO_ACTION = b -> {
	};
	
	public static final int EMPTY = 0x00;
	
	public static final int PIPE_NORMAL = 0x01;
	
	public static final int PIPE_PERFORATED = 0x02;
	
	public static final int PIPE_PERFORATED_FIXED = 0x03;
	
	private final int dynTextureWidth, dynTextureHeight;
	private final DynamicTexture gridTexture;
	private final RenderType gridTextureRenderType;
	
	private final int pipeNormalColor;
	private final int pipePerforatedColor;
	private final int pipePerforatedFixedColor;
	
	protected Grid grid;
	protected ColumnPos tilePos;
	protected int gridWidthScaled, gridHeightScaled;
	protected int gridScale;
	public PipeConfig(DerrickTileEntity tile, int x, int y, int width, int height, int gridWidth, int gridHeight, int gridScale){
		super(x, y, width, height, StringTextComponent.EMPTY, NO_ACTION);
		this.tilePos = new ColumnPos(tile.getPos());
		
		this.grid = new Grid(gridWidth, gridHeight);
		copyGridFrom(tile.gridStorage);
		this.gridWidthScaled = gridWidth * gridScale;
		this.gridHeightScaled = gridHeight * gridScale;
		this.gridScale = MathHelper.clamp(gridScale, 1, Integer.MAX_VALUE);
		
		this.dynTextureWidth = gridWidth;
		this.dynTextureHeight = gridHeight;
		this.gridTexture = new DynamicTexture(this.dynTextureWidth, this.dynTextureHeight, true);
		ResourceLocation loc = Minecraft.getInstance().textureManager.getDynamicTextureLocation("pipegrid/" + this.hashCode(), this.gridTexture);
		this.gridTextureRenderType = RenderType.getText(loc);
		
		this.pipeNormalColor = Integer.valueOf(IPClientConfig.GRID_COLORS.pipe_normal_color.get(), 16);
		this.pipePerforatedColor = Integer.valueOf(IPClientConfig.GRID_COLORS.pipe_perforated_color.get(), 16);
		this.pipePerforatedFixedColor = Integer.valueOf(IPClientConfig.GRID_COLORS.pipe_perforated_fixed_color.get(), 16);
		
		updateTexture();
	}
	
	public void reset(DerrickTileEntity tile){
		copyGridFrom(tile.gridStorage);
		updateTexture();
	}
	
	private void copyGridFrom(PipeConfig.Grid grid){
		if(grid != null && grid.width == this.grid.width && grid.height == this.grid.height){
			for(int i = 0;i < this.grid.array.length;i++){
				this.grid.array[i] = grid.array[i];
			}
			this.grid.changed = true;
			ImmersivePetroleum.log.info("Copied grid from Derrick storage.");
		}
	}
	
	public PipeConfig.Grid getGrid(){
		return this.grid;
	}
	
	/** This has to be called at the end of its life! */
	public void dispose(){
		this.gridTexture.close();
	}
	
	public void updateTexture(){
		NativeImage image = this.gridTexture.getTextureData();
		int texCenterX = this.grid.width / this.gridScale;
		int texCenterY = this.grid.height / this.gridScale;
		
		ClientWorld world = Minecraft.getInstance().world;
		
		for(int gy = 0;gy < this.grid.getHeight();gy++){
			for(int gx = 0;gx < this.grid.getWidth();gx++){
				int color = 0;
				
				switch(this.grid.get(gx, gy)){
					case EMPTY:{
						if((gx >= texCenterX - 2 && gx <= texCenterX + 2) && (gy >= texCenterY - 2 && gy <= texCenterY + 2)){
							color = 0x000000;
						}else{
							int px = gx - (this.grid.getWidth() / 2);
							int py = gy - (this.grid.getHeight() / 2);
							
							ColumnPos c = new ColumnPos(this.tilePos.x + px, this.tilePos.z + py);
							int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(c.x, 0, c.z)).getY();
							
							int tmp = world.getBlockState(new BlockPos(c.x, y - 1, c.z)).getMaterial().getColor().colorValue;
							float f = 0.5F;
							int r = (int) (((tmp >> 16) & 0xFF) * f);
							int g = (int) (((tmp >> 8) & 0xFF) * f);
							int b = (int) (((tmp >> 0) & 0xFF) * f);
							
							color = (r << 16 | g << 8 | b);
						}
						break;
					}
					case PIPE_NORMAL:{
						color = this.pipeNormalColor;
						break;
					}
					case PIPE_PERFORATED:{
						color = this.pipePerforatedColor;
						break;
					}
					case PIPE_PERFORATED_FIXED:{
						color = this.pipePerforatedFixedColor;
						break;
					}
				}
				
				image.setPixelRGBA(gx, gy, toABGR(color));
			}
		}
		
		this.gridTexture.updateDynamicTexture();
	}
	
	@Override
	public void render(MatrixStack matrix, int mx, int my, float partialTicks){
		Minecraft mc = Minecraft.getInstance();
		IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		
		IVertexBuilder builder = buffer.getBuffer(this.gridTextureRenderType);
		matrix.push();
		{
			matrix.translate(this.x, this.y, 0);
			Matrix4f mat = matrix.getLast().getMatrix();
			int x = this.grid.width * this.gridScale;
			int y = this.grid.height * this.gridScale;
			builder.pos(mat, 0, y, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(0.0F, 1.0F).lightmap(0xF000F0).endVertex();
			builder.pos(mat, x, y, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(1.0F, 1.0F).lightmap(0xF000F0).endVertex();
			builder.pos(mat, x, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(1.0F, 0.0F).lightmap(0xF000F0).endVertex();
			builder.pos(mat, 0, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).tex(0.0F, 0.0F).lightmap(0xF000F0).endVertex();
		}
		matrix.pop();
		buffer.finish();
		
		List<ITextComponent> tooltip = new ArrayList<>();
		
		if((mx >= this.x && mx < (this.x + this.width)) && (my >= this.y && my < (this.y + this.height))){
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
				
				tooltip.add(new StringTextComponent("§n" + dir));
			}
			
			tooltip.add(new StringTextComponent(String.format(Locale.ENGLISH, "X: %d §7(%d)", (this.tilePos.x + px), px)));
			tooltip.add(new StringTextComponent(String.format(Locale.ENGLISH, "Z: %d §7(%d)", (this.tilePos.z + py), py)));
			
			int i = this.grid.get(x, y);
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
			GuiUtils.drawHoveringText(matrix, tooltip, mx, my, width, height, -1, mc.fontRenderer);
		}
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
	
	protected void onGridClick(int x, int y, int button){
		int type = this.grid.get(x, y);
		if((type == PIPE_NORMAL || type == PIPE_PERFORATED) && type != PIPE_PERFORATED_FIXED){
			int[] array = getPipeCount();
			if(type == PIPE_NORMAL && array[PIPE_PERFORATED] < 2){
				this.grid.set(x, y, PIPE_PERFORATED);
			}else{
				this.grid.set(x, y, PIPE_NORMAL);
			}
		}else{
			this.grid.clearGrid();
			this.grid.drawLine(this.grid.getWidth() / 2, this.grid.getHeight() / 2, x, y, 1);
			this.grid.set(this.grid.getWidth() / 2, this.grid.getHeight() / 2, PIPE_PERFORATED_FIXED);
			this.grid.set(MathHelper.clamp(x, 0, this.grid.getWidth() - 1), MathHelper.clamp(y, 0, this.grid.getHeight() - 1), PIPE_PERFORATED_FIXED);
		}
		updateTexture();
	}
	
	int[] pipeCountArray = new int[4];
	private int[] getPipeCount(){
		if(this.grid.changed){
			this.pipeCountArray = new int[4];
			for(int i = 0;i < this.grid.size();i++){
				int type = this.grid.get(i);
				this.pipeCountArray[type] += 1;
			}
			this.grid.changed = false;
		}
		return this.pipeCountArray;
	}
	
	@Override
	protected boolean isValidClickButton(int button){
		return button == 0;
	}
	
	public PipeConfig copyDataFrom(PipeConfig other){
		if(other != null){
			this.gridScale = other.gridScale;
			this.grid = other.grid;
			updateTexture();
		}
		return this;
	}
	
	private static int toABGR(int rgb){
		int r = rgb >> 16 & 0xFF;
		int g = rgb >> 8 & 0xFF;
		int b = rgb & 0xFF;
		return 0xFF000000 | b << 16 | g << 8 | r;
	}
	
	public static class Grid{
		private boolean changed;
		private int width, height;
		private byte[] array;
		public Grid(int width, int height){
			this.width = width;
			this.height = height;
			this.array = new byte[width * height];
		}
		
		Grid(){
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
			if(index >= 0 && index < this.array.length && this.array[index] != value){
				this.array[index] = (byte) (value & 0xFF);
				this.changed = true;
			}
		}
		
		/** returns "unsigned" byte */
		public int get(int x, int y){
			return get(index(x, y));
		}
		
		/** returns "unsigned" byte */
		public int get(int index){
			if(index >= 0 && index < this.array.length){
				return ((int) this.array[index]) & 0xFF;
			}
			return 0;
		}
		
		public void clearGrid(){
			for(int i = 0;i < size();i++){
				this.array[i] = 0;
			}
		}
		
		public void drawLine(int xa, int ya, int xb, int yb, int value){
			xa = MathHelper.clamp(xa, 0, this.width - 1);
			xb = MathHelper.clamp(xb, 0, this.width - 1);
			ya = MathHelper.clamp(ya, 0, this.height - 1);
			yb = MathHelper.clamp(yb, 0, this.height - 1);
			
			int dx = xb - xa;
			int dy = yb - ya;
			
			int length = Math.abs(dx) >= Math.abs(dy) ? Math.abs(dx) : Math.abs(dy);
			float tx = dx / (float) length;
			float ty = dy / (float) length;
			
			float x = xa, y = ya;
			int lx = -1, ly = -1;
			for(int i = 1;i <= length;i++){
				int cx = Math.round(x);
				int cy = Math.round(y);
				
				if(!(lx == cx && ly == cy)){
					set(cx, cy, value);
					lx = cx;
					ly = cy;
				}
				
				x += tx;
				y += ty;
			}
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
			Grid grid = new Grid();
			grid.width = nbt.getInt("width");
			grid.height = nbt.getInt("height");
			grid.array = nbt.getByteArray("grid");
			
			if(grid.array.length != (grid.width * grid.height)){
				throw new IllegalStateException("Grid width and height don't match array.");
			}
			
			return grid;
		}
	}
}
