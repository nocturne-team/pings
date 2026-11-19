package com.github._0x00acab.callouts.network;

//? if <1.21 {
/*import com.github._0x00acab.callouts.CalloutsMod;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

// Forge 47 SimpleChannel. Optional on both sides, so vanilla clients and servers can still connect.
public final class LegacyNetwork {
	private static final String VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(CalloutsMod.id("main"),
		() -> VERSION, NetworkRegistry.acceptMissingOr(VERSION), NetworkRegistry.acceptMissingOr(VERSION));

	// Set by the client entrypoint; client-bound payloads are handed here on the main thread.
	public static Consumer<Object> clientReceiver = payload -> {};

	private LegacyNetwork() {}

	public static void register() {
		CHANNEL.messageBuilder(PingRequestPayload.class, 0, NetworkDirection.PLAY_TO_SERVER)
			.encoder(PingRequestPayload::write)
			.decoder(PingRequestPayload::read)
			.consumerMainThread((payload, context) -> {
				ServerPlayer sender = context.get().getSender();
				if (sender != null) {
					CalloutsMod.requestHandler().handle(sender, payload);
				}
			})
			.add();
		toClient(PingBroadcastPayload.class, 1, PingBroadcastPayload::write, PingBroadcastPayload::read);
		toClient(PingConfigSyncPayload.class, 2, PingConfigSyncPayload::write, PingConfigSyncPayload::read);
		toClient(PingRejectedPayload.class, 3, PingRejectedPayload::write, PingRejectedPayload::read);
	}

	private static <T> void toClient(Class<T> type, int id, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder) {
		CHANNEL.messageBuilder(type, id, NetworkDirection.PLAY_TO_CLIENT)
			.encoder(encoder)
			.decoder(decoder)
			.consumerMainThread((T payload, Supplier<NetworkEvent.Context> context) -> clientReceiver.accept(payload))
			.add();
	}

	public static boolean isPresent(Connection connection) {
		return CHANNEL.isRemotePresent(connection);
	}

	public static void sendToPlayer(ServerPlayer player, Object payload) {
		if (isPresent(player.connection.connection)) {
			CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
		}
	}
}
*///?}
