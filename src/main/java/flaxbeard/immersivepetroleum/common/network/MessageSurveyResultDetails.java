package flaxbeard.immersivepetroleum.common.network;

import static flaxbeard.immersivepetroleum.common.util.survey.SurveyScan.SCAN_RADIUS;
import static flaxbeard.immersivepetroleum.common.util.survey.SurveyScan.SCAN_SIZE;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.client.gui.SeismicSurveyScreen;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.survey.SurveyScan;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MessageSurveyResultDetails{
	
	/** Request the server to give more information on the provided scan */
	public static void sendRequestToServer(SurveyScan scan){
		IPPacketHandler.sendToServer(new MessageSurveyResultDetails.ClientToServer(scan));
	}
	
	public static class ClientToServer implements INetMessage{
		public static final ResourceLocation ID = ResourceUtils.ip("surveyresultdetails_clienttoserver");
		
		private int x, z;
		private UUID scanId;
		public ClientToServer(SurveyScan scan){
			this.x = scan.getX();
			this.z = scan.getZ();
			this.scanId = scan.getUuid();
		}
		
		public ClientToServer(FriendlyByteBuf buf){
			this.x = buf.readInt();
			this.z = buf.readInt();
			this.scanId = buf.readUUID();
		}
		
		@Override
		public void write(FriendlyByteBuf buf){
			buf.writeInt(this.x);
			buf.writeInt(this.z);
			buf.writeUUID(this.scanId);
		}
		
		@Override
		public ResourceLocation id(){
			return ID;
		}
		
		@Override
		public void process(PlayPayloadContext context){
			context.workHandler().execute(() -> {
				if(context.flow().getReceptionSide() == LogicalSide.SERVER){
					final ServerPlayer sPlayer = (ServerPlayer) context.player().orElseThrow();
					final ServerLevel sLevel = (ServerLevel) sPlayer.level();
					
					if(sLevel.isAreaLoaded(new BlockPos(this.x, 0, this.z), SCAN_RADIUS)){
						final BitSet set = compileBitSet(sLevel);
						
						context.replyHandler().send(new MessageSurveyResultDetails.ServerToClient(this.scanId, set));
					}
				}
			});
		}
		
		private BitSet compileBitSet(Level level){
			final List<ReservoirIsland> islandCache = new ArrayList<>();
			final BitSet set = new BitSet(SCAN_SIZE * SCAN_SIZE);
			final int r = SCAN_RADIUS;
			for(int j = -r,a = 0;j <= r;j++,a++){
				for(int i = -r,b = 0;i <= r;i++,b++){
					int x = this.x - i;
					int z = this.z - j;
					
					double current = ReservoirHandler.getValueOf(level, x, z);
					if(current != -1){
						Optional<ReservoirIsland> optional = islandCache.stream().filter(res -> {
							return res.contains(x, z);
						}).findFirst();
						
						ReservoirIsland nearbyIsland = optional.isPresent() ? optional.get() : null;
						if(nearbyIsland == null){
							nearbyIsland = ReservoirHandler.getIslandNoCache(level, new ColumnPos(x, z));
							
							if(nearbyIsland != null){
								islandCache.add(nearbyIsland);
							}
						}
						
						if(nearbyIsland != null){
							set.set(a * SCAN_SIZE + b);
						}
					}
				}
			}
			return set;
		}
	}
	
	public static class ServerToClient implements INetMessage{
		public static final ResourceLocation ID = ResourceUtils.ip("surveyresultdetails_servertoclient");
		
		private BitSet replyBitSet;
		private UUID scanId;
		public ServerToClient(UUID scanId, BitSet replyBitSet){
			this.scanId = scanId;
			this.replyBitSet = replyBitSet;
		}
		
		public ServerToClient(FriendlyByteBuf buf){
			this.scanId = buf.readUUID();
			this.replyBitSet = buf.readBitSet();
		}
		
		@Override
		public void write(FriendlyByteBuf buf){
			buf.writeUUID(this.scanId);
			buf.writeBitSet(this.replyBitSet);
		}
		
		@Override
		public ResourceLocation id(){
			return ID;
		}
		
		@Override
		public void process(PlayPayloadContext context){
			// TODO This may be broken AF
			context.workHandler().execute(() -> {
				if(context.flow().getReceptionSide() == LogicalSide.CLIENT){
					if(MCUtil.getScreen() instanceof SeismicSurveyScreen surveyScreen && this.scanId.equals(surveyScreen.scan.getUuid())){
						surveyScreen.setBitSet(this.replyBitSet);
					}
				}
			});
		}
	}
}
