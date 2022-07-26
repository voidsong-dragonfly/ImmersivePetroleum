package flaxbeard.immersivepetroleum.common.util;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 * General purpose Utilities
 * 
 * @author TwistedGate
 */
public class Utils{
	/** Copy of {@link blusunrize.immersiveengineering.common.util.Utils#unlockIEAdvancement(Player, String)} */
	public static void unlockIPAdvancement(Player player, String name){
		if(player instanceof ServerPlayer){
			PlayerAdvancements advancements = ((ServerPlayer) player).getAdvancements();
			ServerAdvancementManager manager = ((ServerLevel) player.getCommandSenderWorld()).getServer().getAdvancements();
			Advancement advancement = manager.getAdvancement(new ResourceLocation(ImmersivePetroleum.MODID, name));
			if(advancement != null)
				advancements.award(advancement, "code_trigger");
		}
	}
	
	/** Copy of {@link blusunrize.immersiveengineering.common.util.Utils#isFluidRelatedItemStack(ItemStack)} */
	public static boolean isFluidRelatedItemStack(ItemStack stack){
		if(stack.isEmpty())
			return false;
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
	}
}
