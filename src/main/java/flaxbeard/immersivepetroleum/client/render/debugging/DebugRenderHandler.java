package flaxbeard.immersivepetroleum.client.render.debugging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.LubricatedTileInfo;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.client.render.IPRenderTypes;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.CokingChamber;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity.Port;
import flaxbeard.immersivepetroleum.common.items.DebugItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class DebugRenderHandler{
	public DebugRenderHandler(){
	}
	
	@SubscribeEvent
	public void renderDebuggingOverlay(RenderGameOverlayEvent.Post event){
		if(ClientUtils.mc().player != null && event.getType() == RenderGameOverlayEvent.ElementType.TEXT){
			PlayerEntity player = ClientUtils.mc().player;
			
			ItemStack main = player.getHeldItem(Hand.MAIN_HAND);
			ItemStack off = player.getHeldItem(Hand.OFF_HAND);
			
			if((main != ItemStack.EMPTY && main.getItem() == IPContent.debugItem) || (off != ItemStack.EMPTY && off.getItem() == IPContent.debugItem)){
				RayTraceResult rt = ClientUtils.mc().objectMouseOver;
				
				if(rt != null && rt.getType() == RayTraceResult.Type.BLOCK){
					BlockRayTraceResult result = (BlockRayTraceResult) rt;
					World world = player.world;
					
					List<ITextComponent> debugOut = new ArrayList<>();
					
					TileEntity te = world.getTileEntity(result.getPos());
					boolean isMBPart = te instanceof MultiblockPartTileEntity;
					if(isMBPart){
						MultiblockPartTileEntity<?> multiblock = (MultiblockPartTileEntity<?>) te;
						
						if(!multiblock.offsetToMaster.equals(BlockPos.ZERO)){
							multiblock = multiblock.master();
						}
						
						if(te instanceof DistillationTowerTileEntity){
							distillationtower(debugOut, (DistillationTowerTileEntity) multiblock);
							
						}else if(te instanceof CokerUnitTileEntity){
							cokerunit(debugOut, (CokerUnitTileEntity) multiblock);
							
						}else if(te instanceof HydrotreaterTileEntity){
							hydrotreater(debugOut, (HydrotreaterTileEntity) multiblock);
							
						}else if(te instanceof OilTankTileEntity){
							oiltank(debugOut, (OilTankTileEntity) multiblock);
						}
					}
					
					if(!debugOut.isEmpty() || isMBPart){
						if(isMBPart){
							MultiblockPartTileEntity<?> generic = (MultiblockPartTileEntity<?>) te;
							if(!generic.offsetToMaster.equals(BlockPos.ZERO)){
								generic = generic.master();
							}
							
							BlockPos pos = generic.posInMultiblock;
							BlockPos hit = result.getPos();
							Block block = generic.getBlockState().getBlock();
							
							debugOut.add(0, toText("World XYZ: " + hit.getX() + ", " + hit.getY() + ", " + hit.getZ()));
							debugOut.add(1, toText("Template XYZ: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
							
							IFormattableTextComponent name = toTranslation(block.getTranslationKey()).mergeStyle(TextFormatting.GOLD);
							
							try{
								name.appendSibling(toText(generic.isRSDisabled() ? " (Redstoned)" : "").mergeStyle(TextFormatting.RED));
							}catch(UnsupportedOperationException e){
								// Don't care, skip if this is thrown
							}
							
							if(generic instanceof PoweredMultiblockTileEntity<?, ?>){
								PoweredMultiblockTileEntity<?, ?> poweredGeneric = (PoweredMultiblockTileEntity<?, ?>) generic;
								
								name.appendSibling(toText(poweredGeneric.shouldRenderAsActive() ? " (Active)" : "").mergeStyle(TextFormatting.GREEN));
								
								debugOut.add(2, toText(poweredGeneric.energyStorage.getEnergyStored() + "/" + poweredGeneric.energyStorage.getMaxEnergyStored() + "RF"));
							}
							
							synchronized(LubricatedHandler.lubricatedTiles){
								for(LubricatedTileInfo info:LubricatedHandler.lubricatedTiles){
									if(info.pos.equals(generic.getPos())){
										name.appendSibling(toText(" (Lubricated " + info.ticks + ")").mergeStyle(TextFormatting.YELLOW));
									}
								}
							}
							
							debugOut.add(2, name);
						}
						
						MatrixStack matrix = event.getMatrixStack();
						matrix.push();
						IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
						for(int i = 0;i < debugOut.size();i++){
							int w = ClientUtils.font().getStringWidth(debugOut.get(i).getString());
							int yOff = i * (ClientUtils.font().FONT_HEIGHT + 2);
							
							matrix.push();
							matrix.translate(0, 0, 1);
							GuiHelper.drawColouredRect(1, 1 + yOff, w + 1, 10, 0xAF_000000, buffer, matrix);
							buffer.finish();
							// Draw string without shadow
							ClientUtils.font().drawText(matrix, debugOut.get(i), 2, 2 + yOff, -1);
							matrix.pop();
						}
						matrix.pop();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void reservoirDebuggingRenderLast(RenderWorldLastEvent event){
		if(ReservoirHandler.generator == null){
			return;
		}
		
		PlayerEntity player = ClientUtils.mc().player;
		
		ItemStack main = player.getHeldItem(Hand.MAIN_HAND);
		ItemStack off = player.getHeldItem(Hand.OFF_HAND);
		
		if((main != ItemStack.EMPTY && main.getItem() == IPContent.debugItem) || (off != ItemStack.EMPTY && off.getItem() == IPContent.debugItem)){
			DebugItem.Modes mode = null;
			if(main != ItemStack.EMPTY){
				mode = DebugItem.getMode(main);
			}
			if(off != ItemStack.EMPTY){
				mode = DebugItem.getMode(off);
			}
			
			if(mode == DebugItem.Modes.SEEDBASED_RESERVOIR || mode == DebugItem.Modes.SEEDBASED_RESERVOIR_AREA_TEST){
				MatrixStack matrix = event.getMatrixStack();
				World world = player.getEntityWorld();
				BlockPos playerPos = player.getPosition();
				
				matrix.push();
				{
					// Anti-Jiggle when moving
					Vector3d renderView = ClientUtils.mc().gameRenderer.getActiveRenderInfo().getProjectedView();
					matrix.translate(-renderView.x, -renderView.y, -renderView.z);
					
					matrix.push();
					{
						IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
						
						int radius = 12;
						for(int i = -radius;i <= radius;i++){
							for(int j = -radius;j <= radius;j++){
								ChunkPos cPos = new ChunkPos(playerPos.add(16*i, 0, 16*j));
								int chunkX = cPos.getXStart();
								int chunkZ = cPos.getZStart();
								
								for(int cX = 0;cX < 16;cX++){
									for(int cZ = 0;cZ < 16;cZ++){
										int x = chunkX + cX;
										int z = chunkZ + cZ;
										
										matrix.push();
										{
											DyeColor color = DyeColor.BLACK;
											
											double n = ReservoirHandler.noiseFor(x, z);
											if(n > -1){
												int c = (int) Math.round(9 * n);
												
												if(c <= 0){
													color = DyeColor.BLACK;
												}else if(c == 1){
													color = DyeColor.BLUE;
												}else if(c == 2){
													color = DyeColor.CYAN;
												}else if(c == 3){
													color = DyeColor.GREEN;
												}else if(c == 4){
													color = DyeColor.LIME;
												}else if(c == 5){
													color = DyeColor.YELLOW;
												}else if(c == 6){
													color = DyeColor.ORANGE;
												}else if(c == 7){
													color = DyeColor.RED;
												}else if(c > 7){
													color = DyeColor.WHITE;
												}
												
												int r = (color.getTextColor() & 0xFF0000) >> 16;
												int g = (color.getTextColor() & 0x00FF00) >> 8;
												int b = (color.getTextColor() & 0x0000FF);
												
												int height = world.getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, 0, z)).getY();
												
												matrix.translate(x, Math.max(63, height) + 0.0625, z);
												
												Matrix4f mat = matrix.getLast().getMatrix();
												
												IVertexBuilder builder = buffer.getBuffer(IPRenderTypes.ISLAND_DEBUGGING_POSITION_COLOR);
												builder.pos(mat, 0, 0, 0).color(r, g, b, 255).endVertex();
												builder.pos(mat, 0, 0, 1).color(r, g, b, 255).endVertex();
												builder.pos(mat, 1, 0, 1).color(r, g, b, 255).endVertex();
												builder.pos(mat, 1, 0, 0).color(r, g, b, 255).endVertex();
											}
										}
										matrix.pop();
									}
								}
							}
						}
						buffer.finish();
					}
					matrix.pop();
					
					matrix.push();
					{
						synchronized(ReservoirHandler.getReservoirIslandList()){
							IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
							
							Collection<ReservoirIsland> islands = ReservoirHandler.getReservoirIslandList().get(player.getEntityWorld().getDimensionKey());
							
							if(islands != null && !islands.isEmpty()){
								int radius = 128;
								radius = radius * radius + radius * radius;
								for(ReservoirIsland island:islands){
									BlockPos p = new BlockPos(playerPos.getX(), 0, playerPos.getZ());
									BlockPos center = island.getBoundingBox().getCenter();
									
									if(center.distanceSq(p) <= radius){
										List<ColumnPos> poly = island.getPolygon();
										
										if(poly != null && !poly.isEmpty()){
											IVertexBuilder builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_LINES);
											Matrix4f mat = matrix.getLast().getMatrix();
											
											// Draw polygon as line
											int j = poly.size() - 1;
											for(int i = 0;i < poly.size();i++){
												ColumnPos a = poly.get(j);
												ColumnPos b = poly.get(i);
												float f = i / (float) poly.size();
												
												builder.pos(mat, a.x + .5F, 128.5F, a.z + .5F).color(f, 0.0F, 1 - f, 0.5F).endVertex();
												builder.pos(mat, b.x + .5F, 128.5F, b.z + .5F).color(f, 0.0F, 1 - f, 0.5F).endVertex();
												
												j = i;
											}
											
											// Center Marker
											builder.pos(mat, center.getX() + .5F, 128F, center.getZ() + .5F).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
											builder.pos(mat, center.getX() + .5F, 129F, center.getZ() + .5F).color(0.0F, 1.0F, 0.0F, 0.5F).endVertex();
											builder.pos(mat, center.getX(), 128.5F, center.getZ() + .5F).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
											builder.pos(mat, center.getX() + 1, 128.5F, center.getZ() + .5F).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
											builder.pos(mat, center.getX() + .5F, 128.5F, center.getZ()).color(0.0F, 0.0F, 1.0F, 0.5F).endVertex();
											builder.pos(mat, center.getX() + .5F, 128.5F, center.getZ() + 1).color(0.0F, 0.0F, 1.0F, 0.5F).endVertex();
											
										}
									}
								}
							}
							
							buffer.finish();
						}
					}
					matrix.pop();
				}
				matrix.pop();
				
			}
		}
	}
	
	private static void distillationtower(List<ITextComponent> text, DistillationTowerTileEntity tower){
		for(int i = 0;i < tower.tanks.length;i++){
			text.add(toText("Tank " + (i + 1)).mergeStyle(TextFormatting.UNDERLINE));
			
			MultiFluidTank tank = tower.tanks[i];
			if(tank.fluids.size() > 0){
				for(int j = 0;j < tank.fluids.size();j++){
					FluidStack fstack = tank.fluids.get(j);
					text.add(toText("  " + fstack.getDisplayName().getString() + " (" + fstack.getAmount() + "mB)"));
				}
			}else{
				text.add(toText("  Empty"));
			}
		}
	}
	
	private static void cokerunit(List<ITextComponent> text, CokerUnitTileEntity coker){
		{
			FluidTank tank = coker.bufferTanks[CokerUnitTileEntity.TANK_INPUT];
			FluidStack fs = tank.getFluid();
			text.add(toText("In Buffer: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
		}
		
		{
			FluidTank tank = coker.bufferTanks[CokerUnitTileEntity.TANK_OUTPUT];
			FluidStack fs = tank.getFluid();
			text.add(toText("Out Buffer: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
		}
		
		for(int i = 0;i < coker.chambers.length;i++){
			CokingChamber chamber = coker.chambers[i];
			FluidTank tank = chamber.getTank();
			FluidStack fs = tank.getFluid();
			
			float completed = chamber.getTotalAmount() > 0 ? 100 * (chamber.getOutputAmount() / (float) chamber.getTotalAmount()) : 0;
			
			text.add(toText("Chamber " + i).mergeStyle(TextFormatting.UNDERLINE, TextFormatting.AQUA));
			text.add(toText("State: " + chamber.getState().toString()));
			text.add(toText("  Tank: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
			text.add(toText("  Content: " + chamber.getTotalAmount() + " / " + chamber.getCapacity()).appendString(" (" + chamber.getInputItem().getDisplayName().getString() + ")"));
			text.add(toText("  Out: " + chamber.getOutputItem().getDisplayName().getString()));
			text.add(toText("  " + MathHelper.floor(completed) + "% Completed. (Raw: " + completed + ")"));
		}
	}
	
	private static void hydrotreater(List<ITextComponent> text, HydrotreaterTileEntity treater){
		IFluidTank[] tanks = treater.getInternalTanks();
		if(tanks != null && tanks.length > 0){
			for(int i = 0;i < tanks.length;i++){
				FluidStack fs = tanks[i].getFluid();
				text.add(toText("Tank " + i + ": " + (fs.getAmount() + "/" + tanks[i].getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
			}
		}
	}
	
	private static void oiltank(List<ITextComponent> text, OilTankTileEntity tank){
		{
			BlockPos mbpos = tank.posInMultiblock;
			Port port = null;
			for(Port p:Port.values()){
				if(p.matches(mbpos)){
					port = p;
					break;
				}
			}
			
			if(port != null){
				OilTankTileEntity.PortState portState = tank.portConfig.get(port);
				boolean isInput = portState == OilTankTileEntity.PortState.INPUT;
				text.add(toText("Port: ")
						.appendSibling(toText(port != null ? port.getString() : "None"))
						.appendSibling(toText(" " + portState.getString())
								.mergeStyle(isInput ? TextFormatting.AQUA : TextFormatting.GOLD)));
			}
		}
		
		FluidStack fs = tank.tank.getFluid();
		text.add(toText("Fluid: " + (fs.getAmount() + "/" + tank.tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
	}
	
	static IFormattableTextComponent toText(String string){
		return new StringTextComponent(string);
	}
	
	static IFormattableTextComponent toTranslation(String translationKey, Object... args){
		return new TranslationTextComponent(translationKey, args);
	}
}
