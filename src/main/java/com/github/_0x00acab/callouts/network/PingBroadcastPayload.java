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
import net.minecraft.world.phys.Vec3;

//? if >=1.21 {
public record PingBroadcastPayload(Vec3 position, int entityId, int categoryId, int color) implements CustomPacketPayload {
//?} else {
/*public record PingBroadcastPayload(Vec3 position, int entityId, int categoryId, int color) {
*///?}
	// Entity markers float this far above the entity's head, on both sides.
	public static final double ENTITY_MARKER_OFFSET = 0.5;
	//? if >=1.21 {
	public static final Type<PingBroadcastPayload> TYPE = new Type<>(CalloutsMod.id("ping_broadcast"));
	public static final StreamCodec<ByteBuf, PingBroadcastPayload> STREAM_CODEC = StreamCodec.composite(
		Vec3.STREAM_CODEC, PingBroadcastPayload::position,
		ByteBufCodecs.VAR_INT, PingBroadcastPayload::entityId,
		ByteBufCodecs.VAR_INT, PingBroadcastPayload::categoryId,
		ByteBufCodecs.INT, PingBroadcastPayload::color,
		PingBroadcastPayload::new);

	@Override
	public Type<PingBroadcastPayload> type() {
		return TYPE;
	}
	//?} else {
	/*public void write(FriendlyByteBuf buf) {
		LegacyCodecs.writeVec3(buf, position);
		buf.writeVarInt(entityId);
		buf.writeVarInt(categoryId);
		buf.writeInt(color);
	}

	public static PingBroadcastPayload read(FriendlyByteBuf buf) {
		return new PingBroadcastPayload(LegacyCodecs.readVec3(buf), buf.readVarInt(), buf.readVarInt(), buf.readInt());
	}
	*///?}
}
