package com.github._0x00acab.callouts.network;

//? if >=1.21 {
import com.github._0x00acab.callouts.CalloutsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}

//? if >=1.21 {
public record PingConfigSyncPayload(double maxPingDistance) implements CustomPacketPayload {
	public static final Type<PingConfigSyncPayload> TYPE = new Type<>(CalloutsMod.id("config_sync"));
	public static final StreamCodec<ByteBuf, PingConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, PingConfigSyncPayload::maxPingDistance,
		PingConfigSyncPayload::new);

	@Override
	public Type<PingConfigSyncPayload> type() {
		return TYPE;
	}
}
//?} else {
/*public record PingConfigSyncPayload(double maxPingDistance) {
	public void write(FriendlyByteBuf buf) {
		buf.writeDouble(maxPingDistance);
	}

	public static PingConfigSyncPayload read(FriendlyByteBuf buf) {
		return new PingConfigSyncPayload(buf.readDouble());
	}
}
*///?}
