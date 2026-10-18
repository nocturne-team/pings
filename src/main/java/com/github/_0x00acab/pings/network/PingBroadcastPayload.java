package com.github._0x00acab.pings.network;

import com.github._0x00acab.pings.PingsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record PingBroadcastPayload(Vec3 position, int entityId, int categoryId, int color) implements CustomPacketPayload {
    // Entity markers float this far above the entity's head, on both sides.
    public static final double ENTITY_MARKER_OFFSET = 0.5;
    public static final Type<PingBroadcastPayload> TYPE = new Type<>(PingsMod.id("ping_broadcast"));
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
}
