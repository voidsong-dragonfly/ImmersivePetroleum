package flaxbeard.immersivepetroleum.common.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
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
				if(world.isAreaLoaded(this.derrickPos, 1)){
					TileEntity te = world.getTileEntity(this.derrickPos);
					if(te instanceof DerrickTileEntity){
						WellTileEntity well = ((DerrickTileEntity) te).getOrCreateWell();
						
						if(well != null){
							int additionalPipes = 0;
							List<ColumnPos> list = new ArrayList<>();
							PipeConfig.Grid grid = PipeConfig.Grid.fromCompound(this.nbt);
							for(int j = 0;j < grid.getHeight();j++){
								for(int i = 0;i < grid.getWidth();i++){
									int type = grid.get(i, j);
									
									if(type > 0){
										switch(type){
											case PipeConfig.PIPE_PERFORATED:
											case PipeConfig.PIPE_PERFORATED_FIXED:{
												int x = i - (grid.getWidth() / 2);
												int z = j - (grid.getHeight() / 2);
												ColumnPos pos = new ColumnPos(this.derrickPos.getX() + x, this.derrickPos.getZ() + z);
												ImmersivePetroleum.log.info("x{} z{} -> {}", x, z, pos);
												list.add(pos);
											}
											case PipeConfig.PIPE_NORMAL:{
												additionalPipes++;
											}
										}
									}
								}
							}
							
							well.tappedIslands = list;
							well.additionalPipes = additionalPipes;
							well.markDirty();
						}
					}
				}
			}
		});
	}
}
