package flaxbeard.immersivepetroleum.common.items;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import flaxbeard.immersivepetroleum.client.model.IPModel;
import javax.annotation.Nonnull;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Multimap;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.client.MCUtil;
import flaxbeard.immersivepetroleum.client.model.IPModels;
import flaxbeard.immersivepetroleum.client.render.dyn.DynamicTextureWrapper;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import flaxbeard.immersivepetroleum.common.network.IPPacketHandler;
import flaxbeard.immersivepetroleum.common.network.MessageDebugSync;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;

public class DebugItem extends IPItemBase{
	public enum Modes{
		DISABLED("Disabled"),
		INFO_SPEEDBOAT("Info: Speedboat"),
		
		SEEDBASED_RESERVOIR("Seed-Based Reservoir: Heatmap"),
		SEEDBASED_RESERVOIR_AREA_TEST("Seed-Based Reservoir: Island Testing"),
		
		REFRESH_ALL_IPMODELS("Refresh all IPModels"),
		UPDATE_SHAPES("Does nothing without Debugging Enviroment"),
		GENERAL_TEST("This one could be dangerous to trigger!")
		;
		
		public final String display;
		Modes(String display){
			this.display = display;
		}
	}
	
	public DebugItem(){
		super();
	}
	
	@Override
	@Nonnull
	public Component getName(@Nonnull ItemStack stack){
		return new TextComponent("IP Debugging Tool").withStyle(ChatFormatting.LIGHT_PURPLE);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@Nonnull ItemStack stack, Level worldIn, List<Component> tooltip, @Nonnull TooltipFlag flagIn){
		tooltip.add(new TextComponent("[Shift + Scroll-UP/DOWN] Change mode.").withStyle(ChatFormatting.GRAY));
		Modes mode = getMode(stack);
		if(mode == Modes.DISABLED){
			tooltip.add(new TextComponent("  Disabled.").withStyle(ChatFormatting.DARK_GRAY));
		}else{
			tooltip.add(new TextComponent("  " + mode.display).withStyle(ChatFormatting.DARK_GRAY));
		}
		
		tooltip.add(new TextComponent("You're not supposed to have this.").withStyle(ChatFormatting.DARK_RED));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items){
	}
	
	@Override
	@Nonnull
	public InteractionResultHolder<ItemStack> use(Level worldIn, @Nonnull Player playerIn, @Nonnull InteractionHand handIn){
		if(!worldIn.isClientSide){
			Modes mode = DebugItem.getMode(playerIn.getItemInHand(handIn));

			switch(mode){
				case GENERAL_TEST -> {
					return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
				}
				case REFRESH_ALL_IPMODELS -> {
					try{
						IPModels.getModels().forEach(IPModel::init);

						playerIn.displayClientMessage(new TextComponent("Models refreshed."), true);
					} catch (Exception e){
						e.printStackTrace();
					}


					return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
				}
				case SEEDBASED_RESERVOIR -> {
					BlockPos playerPos = playerIn.blockPosition();

					ChunkPos cPos = new ChunkPos(playerPos);
					int chunkX = cPos.getMinBlockX();
					int chunkZ = cPos.getMinBlockZ();

					// Does the whole 0-15 local chunk block thing
					int x = playerPos.getX() - cPos.getMinBlockX();
					int z = playerPos.getZ() - cPos.getMinBlockZ();

					double noise = ReservoirHandler.getValueOf(worldIn, (chunkX + x), (chunkZ + z));

					playerIn.displayClientMessage(new TextComponent((chunkX + " " + chunkZ) + ": " + noise), true);

					return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
				}
				case SEEDBASED_RESERVOIR_AREA_TEST -> {
					BlockPos playerPos = playerIn.blockPosition();

					if(ReservoirHandler.getIsland(worldIn, playerPos) != null){
						ReservoirIsland island = ReservoirHandler.getIsland(worldIn, playerPos);

						int x = playerPos.getX();
						int z = playerPos.getZ();

						float pressure = island.getPressure(worldIn, x, z);

						if(playerIn.isShiftKeyDown()){
							island.setAmount(island.getCapacity());
							IPSaveData.markInstanceAsDirty();
							playerIn.displayClientMessage(new TextComponent("Island Refilled."), true);
							return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
						}

						String out = String.format(Locale.ENGLISH,
								"Noise: %.3f, Amount: %d/%d, Pressure: %.3f, Flow: %d, Type: %s",
								ReservoirHandler.getValueOf(worldIn, x, z),
								island.getAmount(),
								island.getCapacity(),
								pressure,
								ReservoirIsland.getFlow(pressure),
								new FluidStack(island.getFluid(), 1).getDisplayName().getString());

						playerIn.displayClientMessage(new TextComponent(out), true);

					}else{
						final Multimap<ResourceKey<Level>, ReservoirIsland> islands = ReservoirHandler.getReservoirIslandList();

						for (ResourceKey<Level> key : islands.keySet()){
							Collection<ReservoirIsland> list = islands.get(key);

							String str = key.location() + " has " + list.size() + " islands.";

							playerIn.displayClientMessage(new TextComponent(str), false);
						}
					}

					return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
				}
				default -> {
				}
			}
			return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
		}
		
		return super.use(worldIn, playerIn, handIn);
	}
	
	@Override
	@Nonnull
	public InteractionResult useOn(UseOnContext context){
		Player player = context.getPlayer();
		if(player == null){
			return InteractionResult.PASS;
		}
		
		ItemStack held = player.getItemInHand(context.getHand());
		Modes mode = DebugItem.getMode(held);
		
		BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
		switch(mode){
			case GENERAL_TEST -> {
				Level world = context.getLevel();
				if(world.isClientSide){
					// Client

					player.displayClientMessage(new TextComponent(DynamicTextureWrapper.DYN_TEXTURE_CACHE.size() + ""), false);

				}else{
					// Server

					if(te instanceof WellTileEntity well){

						if(well.tappedIslands.isEmpty()){
							well.tappedIslands.add(new ColumnPos(well.getBlockPos()));
						}
					}
				}

				return InteractionResult.SUCCESS;
			}
			case UPDATE_SHAPES -> {
				if(te instanceof CokerUnitTileEntity){
					CokerUnitTileEntity.updateShapes = true;
					return InteractionResult.SUCCESS;
				}

				if(te instanceof DerrickTileEntity){
					DerrickTileEntity.updateShapes = true;
					return InteractionResult.SUCCESS;
				}

				if(te instanceof OilTankTileEntity){
					OilTankTileEntity.updateShapes = true;
					return InteractionResult.SUCCESS;
				}

				return InteractionResult.PASS;
			}
			default -> {
			}
		}
		
		return InteractionResult.PASS;
	}
	
	public void onSpeedboatClick(MotorboatEntity speedboatEntity, Player player, ItemStack debugStack){
		if(speedboatEntity.level.isClientSide || DebugItem.getMode(debugStack) != Modes.INFO_SPEEDBOAT){
			return;
		}
		
		MutableComponent textOut = new TextComponent("-- Speedboat --\n");
		
		FluidStack fluid = speedboatEntity.getContainedFluid();
		if(fluid == FluidStack.EMPTY){
			textOut.append("Tank: Empty");
		}else{
			textOut.append("Tank: " + fluid.getAmount() + "/" + speedboatEntity.getMaxFuel() + "mB of ").append(fluid.getDisplayName());
		}
		
		MutableComponent upgradesText = new TextComponent("\n");
		NonNullList<ItemStack> upgrades = speedboatEntity.getUpgrades();
		int i = 0;
		for(ItemStack upgrade:upgrades){
			if(upgrade == null || upgrade == ItemStack.EMPTY){
				upgradesText.append("Upgrade " + (++i) + ": Empty\n");
			}else{
				upgradesText.append("Upgrade " + (i++) + ": ").append(upgrade.getHoverName()).append("\n");
			}
		}
		textOut.append(upgradesText);
		
		player.sendMessage(textOut, Util.NIL_UUID);
	}
	
	public static void setModeServer(ItemStack stack, Modes mode){
		CompoundTag nbt = getSettings(stack);
		nbt.putInt("mode", mode.ordinal());
	}
	
	public static void setModeClient(ItemStack stack, Modes mode){
		CompoundTag nbt = getSettings(stack);
		nbt.putInt("mode", mode.ordinal());
		IPPacketHandler.sendToServer(new MessageDebugSync(nbt));
	}
	
	public static Modes getMode(ItemStack stack){
		CompoundTag nbt = getSettings(stack);
		if(nbt.contains("mode")){
			int mode = nbt.getInt("mode");
			
			if(mode < 0 || mode >= Modes.values().length)
				mode = 0;
			
			return Modes.values()[mode];
		}
		return Modes.DISABLED;
	}
	
	public static CompoundTag getSettings(ItemStack stack){
		return stack.getOrCreateTagElement("settings");
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT)
	public static class ClientInputHandler{
		static boolean shiftHeld = false;
		
		@SubscribeEvent
		public static void handleScroll(InputEvent.MouseScrollEvent event){
			double delta = event.getScrollDelta();
			
			if(shiftHeld && delta != 0.0){
				Player player = MCUtil.getPlayer();
				ItemStack mainItem = player.getMainHandItem();
				ItemStack secondItem = player.getOffhandItem();
				boolean main = !mainItem.isEmpty() && mainItem.getItem() == IPContent.DEBUGITEM.get();
				boolean off = !secondItem.isEmpty() && secondItem.getItem() == IPContent.DEBUGITEM.get();
				
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
					player.displayClientMessage(new TextComponent(mode.display), true);
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void handleKey(InputEvent.KeyInputEvent event){
			if(event.getKey() == GLFW.GLFW_KEY_RIGHT_SHIFT || event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT){
				switch(event.getAction()){
					case GLFW.GLFW_PRESS -> {
						shiftHeld = true;
					}
					case GLFW.GLFW_RELEASE -> {
						shiftHeld = false;
					}
				}
			}
		}
	}
}
