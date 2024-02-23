package flaxbeard.immersivepetroleum.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public interface INetMessage extends CustomPacketPayload{
	void process(PlayPayloadContext context);
}
