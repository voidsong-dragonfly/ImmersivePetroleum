package flaxbeard.immersivepetroleum.common.network;

import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MessageDebugSync implements INetMessage{
	public static final ResourceLocation ID = ResourceUtils.ip("debugsync");
	
	CompoundTag nbt;
	public MessageDebugSync(CompoundTag nbt){
		this.nbt = nbt;
	}
	
	public MessageDebugSync(FriendlyByteBuf buf){
		this.nbt = buf.readNbt();
	}
	
	@Override
	public void write(FriendlyByteBuf buf){
		buf.writeNbt(this.nbt);
	}
	
	@Override
	public ResourceLocation id(){
		return ID;
	}
	
	@Override
	public void process(PlayPayloadContext context){
		context.workHandler().execute(() -> {
			if(context.flow().getReceptionSide() == LogicalSide.SERVER){
				Player player = context.player().orElseThrow();
				
				ItemStack mainItem = player.getMainHandItem();
				ItemStack secondItem = player.getOffhandItem();
				boolean main = !mainItem.isEmpty() && mainItem.getItem() == IPContent.DEBUGITEM.get();
				boolean off = !secondItem.isEmpty() && secondItem.getItem() == IPContent.DEBUGITEM.get();
				
				if(main || off){
					ItemStack target = main ? mainItem : secondItem;
					
					CompoundTag targetNBT = target.getOrCreateTagElement("settings");
					targetNBT.merge(this.nbt);
				}
			}
		});
	}
}
