package flaxbeard.immersivepetroleum.common.network;

import java.util.Objects;
import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MessageDerrick implements INetMessage{
	
	public static void sendToServer(BlockPos derrickPos, PipeConfig.Grid grid){
		IPPacketHandler.sendToServer(new MessageDerrick(derrickPos, grid));
	}
	
	BlockPos derrickPos;
	CompoundNBT nbt;
	
	private MessageDerrick(BlockPos derrick, PipeConfig.Grid grid){
		this.derrickPos = derrick;
		this.nbt = grid.toCompound();
	}
	
	public MessageDerrick(PacketBuffer buf){
		this.nbt = buf.readCompoundTag();
		this.derrickPos = buf.readBlockPos();
	}
	
	@Override
	public void toBytes(PacketBuffer buf){
		buf.writeCompoundTag(this.nbt);
		buf.writeBlockPos(this.derrickPos);
	}
	
	@Override
	public void process(Supplier<Context> context){
		context.get().enqueueWork(() -> {
			Context con = context.get();
			
			if(con.getDirection().getReceptionSide() == LogicalSide.SERVER){
				ServerWorld world = Objects.requireNonNull(con.getSender()).getServerWorld();
				if(world.isAreaLoaded(this.derrickPos, 2)){
					TileEntity te = world.getTileEntity(this.derrickPos);
					if(te instanceof DerrickTileEntity){
						DerrickTileEntity derrick = (DerrickTileEntity) te;
						
						derrick.gridStorage = PipeConfig.Grid.fromCompound(this.nbt);
						derrick.updateMasterBlock(null, true);
						
						boolean use = false;
						if(use){
							// TODO Reuse this whole thing!
							WellTileEntity well = derrick.getOrCreateWell(false);
							derrick.transferGridDataToWell(well);
						}
					}
				}
			}
		});
	}
}
