package com.github._0x00acab.callouts.network;

import com.github._0x00acab.callouts.CalloutsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Tells the sender why a ping was dropped, so the client can show it and play the error sound.
public record PingRejectedPayload(int reason, double value) implements CustomPacketPayload {
	public static final int COOLDOWN = 0;
	// value: seconds of quiet needed
	public static final int LOCKED_OUT = 1;
	// value: max ping distance in blocks
	public static final int TOO_FAR = 2;

	public static final Type<PingRejectedPayload> TYPE = new Type<>(CalloutsMod.id("ping_rejected"));
	public static final StreamCodec<ByteBuf, PingRejectedPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, PingRejectedPayload::reason,
		ByteBufCodecs.DOUBLE, PingRejectedPayload::value,
		PingRejectedPayload::new);

	@Override
	public Type<PingRejectedPayload> type() {
		return TYPE;
	}
}
