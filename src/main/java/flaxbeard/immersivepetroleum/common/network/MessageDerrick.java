package flaxbeard.immersivepetroleum.common.network;

import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MessageDerrick implements INetMessage{
	public static final ResourceLocation ID = ResourceUtils.ip("derrick");
	
	public static void sendToServer(BlockPos derrickPos, PipeConfig.Grid grid){
		IPPacketHandler.sendToServer(new MessageDerrick(derrickPos, grid));
	}
	
	BlockPos derrickPos;
	CompoundTag nbt;
	
	private MessageDerrick(BlockPos derrick, PipeConfig.Grid grid){
		this.derrickPos = derrick;
		this.nbt = grid.toCompound();
	}
	
	public MessageDerrick(FriendlyByteBuf buf){
		this.nbt = buf.readNbt();
		this.derrickPos = buf.readBlockPos();
	}
	
	@Override
	public void write(FriendlyByteBuf buf){
		buf.writeNbt(this.nbt);
		buf.writeBlockPos(this.derrickPos);
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
				
				if(player.level() instanceof ServerLevel world){
					if(world.isAreaLoaded(this.derrickPos, 2)){
						BlockEntity te = world.getBlockEntity(this.derrickPos);
						
						/*// TODO Gridstorage transfer
						if(te instanceof DerrickTileEntity derrick){
							
							derrick.gridStorage = PipeConfig.Grid.fromCompound(this.nbt);
							derrick.updateMasterBlock(null, true);
							
							WellTileEntity well = derrick.getWell();
							derrick.transferGridDataToWell(well);
						}
						*/
					}
				}
				
			}
		});
	}
}
