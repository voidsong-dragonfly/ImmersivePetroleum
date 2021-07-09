package flaxbeard.immersivepetroleum.common.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.lwjgl.glfw.GLFW;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import blusunrize.immersiveengineering.client.ClientUtils;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.pumpjack.PumpjackHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Island;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirWorldInfo;
import flaxbeard.immersivepetroleum.client.model.IPModels;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import flaxbeard.immersivepetroleum.common.network.IPPacketHandler;
import flaxbeard.immersivepetroleum.common.network.MessageDebugSync;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("deprecation")
public class DebugItem extends IPItemBase{
	public static enum Modes{
		DISABLED("Disabled"),
		INFO_SPEEDBOAT("Info: Speedboat"),
		
		// TODO Chunk-Based Reservoir: Nuke this aswell then
		CHUNKBASED_RESERVOIR("Chunk-Based Reservoir: Create/Get"),
		CHUNKBASED_RESERVOIR_BIG_SCAN("Chunk-Based Reservoir: Scan 5 Chunk Radius Area"),
		CHUNKBASED_RESERVOIR_CLEAR_CACHE("Chunk-Based Reservoir: Clear Cache"),

		SEEDBASED_RESERVOIR("Seed-Based Reservoir: Heatmap"),
		SEEDBASED_RESERVOIR_AREA_TEST("Seed-Based Reservoir: Island Testing"),
		
		REFRESH_ALL_IPMODELS("Refresh all IPModels"),
		UPDATE_SHAPES("Does nothing without Debugging Enviroment"),
		GENERAL_TEST("This one could be dangerous to trigger!")
		;
		
		public final String display;
		private Modes(String display){
			this.display = display;
		}
	}
	
	public DebugItem(){
		super("debug");
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack){
		return new StringTextComponent("IP Debugging Tool").mergeStyle(TextFormatting.LIGHT_PURPLE);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new StringTextComponent("[Shift + Scroll-UP/DOWN] Change mode.").mergeStyle(TextFormatting.GRAY));
		Modes mode = getMode(stack);
		if(mode == Modes.DISABLED){
			tooltip.add(new StringTextComponent("  Disabled.").mergeStyle(TextFormatting.DARK_GRAY));
		}else{
			tooltip.add(new StringTextComponent("  " + mode.display).mergeStyle(TextFormatting.DARK_GRAY));
		}
		
		tooltip.add(new StringTextComponent("You're not supposed to have this.").mergeStyle(TextFormatting.DARK_RED));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		if(!worldIn.isRemote){
			Modes mode = DebugItem.getMode(playerIn.getHeldItem(handIn));
			
			switch(mode){
				case REFRESH_ALL_IPMODELS:{
					IPModels.getModels().forEach(m -> m.init());
					
					playerIn.sendStatusMessage(new StringTextComponent("Models refreshed."), true);
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				}
				case CHUNKBASED_RESERVOIR:{
					BlockPos pos = playerIn.getPosition();
					DimensionChunkCoords coords = new DimensionChunkCoords(worldIn.getDimensionKey(), (pos.getX() >> 4), (pos.getZ() >> 4));
					
					int last = PumpjackHandler.reservoirsCache.size();
					ReservoirWorldInfo info = PumpjackHandler.getOrCreateOilWorldInfo(worldIn, coords, false);
					boolean isNew = PumpjackHandler.reservoirsCache.size() != last;
					
					if(info != null){
						int cap = info.capacity;
						int cur = info.current;
						Reservoir type = info.getType();
						
						if(type!=null){
							String out = String.format(Locale.ENGLISH,
									"%s %s: %.3f/%.3f Buckets of %s%s%s",
									coords.x,
									coords.z,
									cur/1000D,
									cap/1000D,
									type.name,
									(info.overrideType!=null?" [OVERRIDDEN]":""),
									(isNew?" [NEW]":"")
							);
							
							playerIn.sendStatusMessage(new StringTextComponent(out), true);
							
							return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
						}
					}
					
					playerIn.sendStatusMessage(new StringTextComponent(String.format(Locale.ENGLISH, "%s %s: Nothing.", coords.x, coords.z)), true);
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				}
				case CHUNKBASED_RESERVOIR_BIG_SCAN:{
					BlockPos pos = playerIn.getPosition();
					int r = 5;
					int cx = (pos.getX() >> 4);
					int cz = (pos.getZ() >> 4);
					ImmersivePetroleum.log.info(worldIn.getDimensionKey());
					for(int i = -r;i <= r;i++){
						for(int j = -r;j <= r;j++){
							int x = cx + i;
							int z = cz + j;
							
							DimensionChunkCoords coords = new DimensionChunkCoords(worldIn.getDimensionKey(), x, z);
							
							ReservoirWorldInfo info = PumpjackHandler.getOrCreateOilWorldInfo(worldIn, coords, false);
							if(info != null && info.getType() != null){
								Reservoir reservoir = info.getType();
								
								int cap = info.capacity;
								int cur = info.current;
								
								String out = String.format(Locale.ENGLISH, "%s %s:\t%.3f/%.3f Buckets of %s", coords.x, coords.z, cur / 1000D, cap / 1000D, reservoir.name);
								
								ImmersivePetroleum.log.info(out);
							}
						}
					}
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				}
				case CHUNKBASED_RESERVOIR_CLEAR_CACHE:{
					int contentSize = PumpjackHandler.reservoirsCache.size();
					
					PumpjackHandler.reservoirsCache.clear();
					PumpjackHandler.recalculateChances();
					
					IPSaveData.setDirty();
					
					playerIn.sendStatusMessage(new StringTextComponent("Cleared Oil Cache. (Removed " + contentSize + ")"), true);
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				}
				case SEEDBASED_RESERVOIR:{
					if(worldIn instanceof ServerWorld){
						if(ReservoirHandler.generator == null){
							ReservoirHandler.generator = new PerlinNoiseGenerator(new SharedSeedRandom(((ISeedReader) worldIn).getSeed()), IntStream.of(0));
						}
					}
					
					BlockPos playerPos = playerIn.getPosition();
					
					ChunkPos cPos = new ChunkPos(playerPos);
					int chunkX = cPos.getXStart();
					int chunkZ = cPos.getZStart();
					
					// Does the whole 0-15 local chunk block thing
					int x = playerPos.getX() - cPos.getXStart();
					int z = playerPos.getZ() - cPos.getZStart();
					
					double noise = ReservoirHandler.noiseFor((chunkX + x), (chunkZ + z));
					
					playerIn.sendStatusMessage(new StringTextComponent((chunkX + " " + chunkZ) + ": " + noise), true);
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				}
				case SEEDBASED_RESERVOIR_AREA_TEST:{
					if(worldIn instanceof ServerWorld){
						if(ReservoirHandler.generator == null){
							ReservoirHandler.generator = new PerlinNoiseGenerator(new SharedSeedRandom(((ISeedReader) worldIn).getSeed()), IntStream.of(0));
						}
					}
					
					BlockPos playerPos = playerIn.getPosition();
					ColumnPos playerColumn = new ColumnPos(playerPos);
					
					ChunkPos cPos = new ChunkPos(playerPos);
					int cx = cPos.getXStart();
					int cz = cPos.getZStart();
					
					ColumnPos current = Island.getFirst(cx, cz);
					if(current != null){
						long timer = System.currentTimeMillis();
						List<ColumnPos> list = new ArrayList<>();
						
						Island.next(list, current.x, current.z);
						
						// Keep edges/corners and dump the rest
						{
							list = list.stream().filter(pos -> {
								boolean b0 = ReservoirHandler.noiseFor(pos.x + 1, pos.z) == -1;
								boolean b1 = ReservoirHandler.noiseFor(pos.x - 1, pos.z) == -1;
								boolean b2 = ReservoirHandler.noiseFor(pos.x, pos.z + 1) == -1;
								boolean b3 = ReservoirHandler.noiseFor(pos.x, pos.z - 1) == -1;
								
								return b0 || b1 || b2 || b3;
							}).collect(Collectors.toList());
							
							// Just Debugging, do not include in final!
							for(int i = 0;i < list.size();i++){
								ColumnPos pos = list.get(i);
								worldIn.setBlockState(new BlockPos(pos.x, 128, pos.z), Blocks.WHITE_CONCRETE.getDefaultState());
							}
						}
						
						// Give this some direction
						// Result can end up being either clockwise or counter-clockwise!
						{
							List<ColumnPos> ll = new ArrayList<>();
							ll.add(list.remove(0));
							int a = 0;
							while(list.size() > 0){
								final ColumnPos col = ll.get(a);
								
								check: {
									for(int j = -1;j <= 1;j++){
										for(int i = -1;i <= 1;i++){
											ColumnPos p = new ColumnPos(col.x + i, col.z + j);
											
											if(list.remove(p) && ll.add(p)){
												a++;
												break check;
											}
										}
									}
									
									ImmersivePetroleum.log.info("This should not happen, but it did..");
									break;
								}
							}
							list = ll;
						}
						
						// Straight Line Optimizations (Cut down on number of Points)
						// Avoid things like XXXX and turn them into X--X
						// Where X is a Block, and - is just an imaginary connection.
						{
							// X Optimization
							{
								for(int i = 0;i < list.size();i++){
									ColumnPos pos0 = list.get(i);
								}
							}
							
							// Z Optimization
							{
							}
						}
						
						/*
						 * After this point the list would be stored inside the
						 * Island class and everything below would be there
						 * instead
						 */
						
						// Point inside Polygon Test
						{
							boolean inside = inPoly(playerColumn, list);
							playerIn.sendStatusMessage(new StringTextComponent("Inside: " + inside), true);
						}
						
						timer = System.currentTimeMillis() - timer;
						ImmersivePetroleum.log.info("Time: {}ms", timer);
					}else{
						ImmersivePetroleum.log.info("Nothing here.");
					}
					
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
				}
				default:
					break;
			}
			return new ActionResult<ItemStack>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
		}
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	// TODO Move to Island class
	// Based on http://www.alienryderflex.com/polygon/
	boolean inPoly(ColumnPos vec, List<ColumnPos> poly){
		float x = vec.x;
		float y = vec.z;
		
		boolean ret = false;
		int j = poly.size() - 1;
		for(int i = 0;i < poly.size();i++){
			ColumnPos a = poly.get(i);
			ColumnPos b = poly.get(j);
			
			float ax = a.x, az = a.z;
			float bx = b.x, bz = b.z;
			
			if(((az < y && bz >= y) || (bz < y && az >= y)) && (ax <= x || bx <= x)){
				ret ^= (ax + (y - az) / (bz - az) * (bx - ax) < x);
			}
			
			j = i;
		}
		
		return ret;
	}
	
	boolean pointInPolygon(int polyCorners, float[] polyX, float[] polyY, float x, float y){
		return false;
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		PlayerEntity player = context.getPlayer();
		if(player == null){
			return ActionResultType.PASS;
		}
		
		ItemStack held = player.getHeldItem(context.getHand());
		Modes mode = DebugItem.getMode(held);
		
		TileEntity te = context.getWorld().getTileEntity(context.getPos());
		switch(mode){
			case GENERAL_TEST:{
				if(context.getWorld().isRemote){
					// Client
				}else{
					// Server
				}
				
				return ActionResultType.PASS;
			}
			case UPDATE_SHAPES:{
				if(te instanceof CokerUnitTileEntity){
					CokerUnitTileEntity.updateShapes = true;
					return ActionResultType.SUCCESS;
				}
				
				if(te instanceof DerrickTileEntity){
					DerrickTileEntity.updateShapes = true;
					return ActionResultType.SUCCESS;
				}
				
				if(te instanceof OilTankTileEntity){
					OilTankTileEntity.updateShapes = true;
					return ActionResultType.SUCCESS;
				}
				
				return ActionResultType.PASS;
			}
			default:
				break;
		}
		
		return ActionResultType.PASS;
	}
	
	public void onSpeedboatClick(MotorboatEntity speedboatEntity, PlayerEntity player, ItemStack debugStack){
		if(speedboatEntity.world.isRemote || DebugItem.getMode(debugStack) != Modes.INFO_SPEEDBOAT){
			return;
		}
		
		IFormattableTextComponent textOut = new StringTextComponent("-- Speedboat --\n");
		
		FluidStack fluid = speedboatEntity.getContainedFluid();
		if(fluid == FluidStack.EMPTY){
			textOut.appendString("Tank: Empty");
		}else{
			textOut.appendString("Tank: " + fluid.getAmount() + "/" + speedboatEntity.getMaxFuel() + "mB of ").appendSibling(fluid.getDisplayName());
		}
		
		IFormattableTextComponent upgradesText = new StringTextComponent("\n");
		NonNullList<ItemStack> upgrades = speedboatEntity.getUpgrades();
		int i = 0;
		for(ItemStack upgrade:upgrades){
			if(upgrade == null || upgrade == ItemStack.EMPTY){
				upgradesText.appendString("Upgrade " + (++i) + ": Empty\n");
			}else{
				upgradesText.appendString("Upgrade " + (i++) + ": ").appendSibling(upgrade.getDisplayName()).appendString("\n");
			}
		}
		textOut.appendSibling(upgradesText);
		
		player.sendMessage(textOut, Util.DUMMY_UUID);
	}
	
	@SuppressWarnings("unused")
	private void analyze(ItemUseContext context, BlockState state, PumpjackTileEntity te){
	}
	
	public static void setModeServer(ItemStack stack, Modes mode){
		CompoundNBT nbt = getSettings(stack);
		nbt.putInt("mode", mode.ordinal());
	}
	
	public static void setModeClient(ItemStack stack, Modes mode){
		CompoundNBT nbt = getSettings(stack);
		nbt.putInt("mode", mode.ordinal());
		IPPacketHandler.sendToServer(new MessageDebugSync(nbt));
	}
	
	public static Modes getMode(ItemStack stack){
		CompoundNBT nbt = getSettings(stack);
		if(nbt.contains("mode")){
			int mode = nbt.getInt("mode");
			
			if(mode < 0 || mode >= Modes.values().length)
				mode = 0;
			
			return Modes.values()[mode];
		}
		return Modes.DISABLED;
	}
	
	public static CompoundNBT getSettings(ItemStack stack){
		return stack.getOrCreateChildTag("settings");
	}
	
	@Mod.EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT)
	public static class ClientInputHandler{
		static boolean shiftHeld = false;
		
		@SubscribeEvent
		public static void handleScroll(InputEvent.MouseScrollEvent event){
			double delta = event.getScrollDelta();
			
			if(shiftHeld && delta != 0.0){
				PlayerEntity player = ClientUtils.mc().player;
				ItemStack mainItem = player.getHeldItemMainhand();
				ItemStack secondItem = player.getHeldItemOffhand();
				boolean main = !mainItem.isEmpty() && mainItem.getItem() == IPContent.debugItem;
				boolean off = !secondItem.isEmpty() && secondItem.getItem() == IPContent.debugItem;
				
				if(main || off){
					ItemStack target = main ? mainItem : secondItem;
					
					Modes mode = DebugItem.getMode(target);
					int id = mode.ordinal() + (int) delta;
					if(id < 0){
						id = Modes.values().length - 1;
					}
					if(id >= Modes.values().length){
						id = 0;
					}
					mode = Modes.values()[id];
					
					DebugItem.setModeClient(target, mode);
					player.sendStatusMessage(new StringTextComponent(mode.display), true);
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void handleKey(InputEvent.KeyInputEvent event){
			if(event.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT || event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT){
				switch(event.getAction()){
					case GLFW.GLFW_PRESS:{
						shiftHeld = true;
						return;
					}
					case GLFW.GLFW_RELEASE:{
						shiftHeld = false;
						return;
					}
				}
			}
		}
	}
}
