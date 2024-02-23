package flaxbeard.immersivepetroleum.common.network;

import java.util.Optional;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class IPPacketHandler{
	
	/**
	 * Sends a server message directly to the player. Will not do anything if the provided instance is not a {@link ServerPlayer} instance
	 * 
	 * @param player  The {@link Player} to send to
	 * @param message The message to send
	 */
	public static <MSG extends INetMessage> void sendToPlayer(Player player, @Nonnull MSG message){
		if(message != null && player instanceof ServerPlayer serverPlayer){
			PacketDistributor.PLAYER.with(serverPlayer).send(message);
		}
	}
	
	/** Client -> Server */
	public static <MSG extends INetMessage> void sendToServer(MSG message){
		if(message == null)
			return;
		
		PacketDistributor.SERVER.noArg().send(message);
	}
	
	/**
	 * Sends a packet to everyone in the specified dimension.
	 * 
	 * <pre>
	 * Server -> Client
	 * </pre>
	 */
	public static <MSG extends INetMessage> void sendToDimension(ResourceKey<Level> dim, MSG message){
		if(message == null)
			return;
		
		PacketDistributor.DIMENSION.with(dim).send(message);
	}
	
	public static <MSG extends INetMessage> void sendAll(MSG message){
		if(message == null)
			return;
		
		PacketDistributor.ALL.noArg().send(message);
	}
	
	public static class Register{
		private static IPayloadRegistrar REGISTRAR;
		public static void init(RegisterPayloadHandlerEvent ev){
			REGISTRAR = ev.registrar(ImmersivePetroleum.MODID);
			registerMessages();
			REGISTRAR = null;
		}
		
		private static void registerMessages(){
			registerMessage(MessageDebugSync.ID, MessageDebugSync::new, PacketFlow.SERVERBOUND);
			registerMessage(MessageConsumeBoatFuel.ID, MessageConsumeBoatFuel::new, PacketFlow.SERVERBOUND);
			registerMessage(MessageProjectorSync.ID, MessageProjectorSync::new);
			registerMessage(MessageDerrick.ID, MessageDerrick::new, PacketFlow.SERVERBOUND);
			
			registerMessage(MessageSurveyResultDetails.ClientToServer.ID, MessageSurveyResultDetails.ClientToServer::new, PacketFlow.SERVERBOUND);
			registerMessage(MessageSurveyResultDetails.ServerToClient.ID, MessageSurveyResultDetails.ServerToClient::new, PacketFlow.CLIENTBOUND);
		}
		
		private static <T extends INetMessage> void registerMessage(ResourceLocation id, FriendlyByteBuf.Reader<T> reader){
			registerMessage(id, reader, Optional.empty());
		}
		
		private static <T extends INetMessage> void registerMessage(ResourceLocation id, FriendlyByteBuf.Reader<T> reader, PacketFlow direction){
			registerMessage(id, reader, Optional.of(direction));
		}
		
		private static <T extends INetMessage> void registerMessage(ResourceLocation id, FriendlyByteBuf.Reader<T> reader, Optional<PacketFlow> direction){
			if(direction.isPresent()){
				REGISTRAR.play(id, reader, builder -> {
					if(direction.get() == PacketFlow.CLIENTBOUND){
						builder.client(T::process);
					}else{
						builder.server(T::process);
					}
				});
			}else{
				REGISTRAR.play(id, reader, T::process);
			}
		}
	}
}
