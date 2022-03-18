package flaxbeard.immersivepetroleum.common.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;

public interface INetMessage{
	void toBytes(FriendlyByteBuf buf);
	void process(Supplier<Context> context);
}
