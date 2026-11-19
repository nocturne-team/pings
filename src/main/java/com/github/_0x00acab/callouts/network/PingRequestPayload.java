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
public record PingRequestPayload(Vec3 position, int entityId, int categoryId) implements CustomPacketPayload {
//?} else {
/*public record PingRequestPayload(Vec3 position, int entityId, int categoryId) {
*///?}
	public static final int NO_ENTITY = -1;
	//? if >=1.21 {
	public static final Type<PingRequestPayload> TYPE = new Type<>(CalloutsMod.id("ping_request"));
	public static final StreamCodec<ByteBuf, PingRequestPayload> STREAM_CODEC = StreamCodec.composite(
		Vec3.STREAM_CODEC, PingRequestPayload::position,
		ByteBufCodecs.VAR_INT, PingRequestPayload::entityId,
		ByteBufCodecs.VAR_INT, PingRequestPayload::categoryId,
		PingRequestPayload::new);

	@Override
	public Type<PingRequestPayload> type() {
		return TYPE;
	}
	//?} else {
	/*public void write(FriendlyByteBuf buf) {
		LegacyCodecs.writeVec3(buf, position);
		buf.writeVarInt(entityId);
		buf.writeVarInt(categoryId);
	}

	public static PingRequestPayload read(FriendlyByteBuf buf) {
		return new PingRequestPayload(LegacyCodecs.readVec3(buf), buf.readVarInt(), buf.readVarInt());
	}
	*///?}
}
