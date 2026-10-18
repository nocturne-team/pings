package com.github._0x00acab.pings.network;

import com.github._0x00acab.pings.PingsMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record PingRequestPayload(Vec3 position, int entityId, int categoryId) implements CustomPacketPayload {
    public static final int NO_ENTITY = -1;
    public static final Type<PingRequestPayload> TYPE = new Type<>(PingsMod.id("ping_request"));
    public static final StreamCodec<ByteBuf, PingRequestPayload> STREAM_CODEC = StreamCodec.composite(
        Vec3.STREAM_CODEC, PingRequestPayload::position,
        ByteBufCodecs.VAR_INT, PingRequestPayload::entityId,
        ByteBufCodecs.VAR_INT, PingRequestPayload::categoryId,
        PingRequestPayload::new);

    @Override
    public Type<PingRequestPayload> type() {
        return TYPE;
    }
}
