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

// Tells the sender why a ping was dropped, so the client can show it and play the error sound.
//? if >=1.21 {
public record PingRejectedPayload(int reason, double value) implements CustomPacketPayload {
//?} else {
/*public record PingRejectedPayload(int reason, double value) {
*///?}
	public static final int COOLDOWN = 0;
	// value: seconds of quiet needed
	public static final int LOCKED_OUT = 1;
	// value: max ping distance in blocks
	public static final int TOO_FAR = 2;

	//? if >=1.21 {
	public static final Type<PingRejectedPayload> TYPE = new Type<>(CalloutsMod.id("ping_rejected"));
	public static final StreamCodec<ByteBuf, PingRejectedPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, PingRejectedPayload::reason,
		ByteBufCodecs.DOUBLE, PingRejectedPayload::value,
		PingRejectedPayload::new);

	@Override
	public Type<PingRejectedPayload> type() {
		return TYPE;
	}
	//?} else {
	/*public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(reason);
		buf.writeDouble(value);
	}

	public static PingRejectedPayload read(FriendlyByteBuf buf) {
		return new PingRejectedPayload(buf.readVarInt(), buf.readDouble());
	}
	*///?}
}
