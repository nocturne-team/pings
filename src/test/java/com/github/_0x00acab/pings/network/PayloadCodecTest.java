package com.github._0x00acab.pings.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayloadCodecTest {

    @Test
    void requestRoundTripsLocationPing() {
        PingRequestPayload original = new PingRequestPayload(new Vec3(12.5, -3.25, 900.0), PingRequestPayload.NO_ENTITY, 0);
        ByteBuf buf = Unpooled.buffer();

        PingRequestPayload.STREAM_CODEC.encode(buf, original);

        assertEquals(original, PingRequestPayload.STREAM_CODEC.decode(buf));
        assertEquals(0, buf.readableBytes());
    }

    @Test
    void requestRoundTripsEntityPing() {
        PingRequestPayload original = new PingRequestPayload(new Vec3(1, 2, 3), 4711, 3);
        ByteBuf buf = Unpooled.buffer();

        PingRequestPayload.STREAM_CODEC.encode(buf, original);

        assertEquals(original, PingRequestPayload.STREAM_CODEC.decode(buf));
    }

    @Test
    void broadcastRoundTripsIncludingColor() {
        PingBroadcastPayload original = new PingBroadcastPayload(new Vec3(-5, 64, 5), 42, 2, 0xFF55FF55);
        ByteBuf buf = Unpooled.buffer();

        PingBroadcastPayload.STREAM_CODEC.encode(buf, original);

        assertEquals(original, PingBroadcastPayload.STREAM_CODEC.decode(buf));
        assertEquals(0, buf.readableBytes());
    }

    @Test
    void payloadIdsAreNamespaced() {
        assertEquals("pings:ping_request", PingRequestPayload.TYPE.id().toString());
        assertEquals("pings:ping_broadcast", PingBroadcastPayload.TYPE.id().toString());
        assertEquals("pings:config_sync", PingConfigSyncPayload.TYPE.id().toString());
    }

    @Test
    void configSyncRoundTripsMaxDistance() {
        PingConfigSyncPayload original = new PingConfigSyncPayload(96.0);
        ByteBuf buf = Unpooled.buffer();

        PingConfigSyncPayload.STREAM_CODEC.encode(buf, original);

        assertEquals(original, PingConfigSyncPayload.STREAM_CODEC.decode(buf));
        assertEquals(0, buf.readableBytes());
    }
}
