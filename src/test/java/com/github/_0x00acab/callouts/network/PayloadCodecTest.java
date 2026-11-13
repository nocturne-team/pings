package com.github._0x00acab.callouts.network;

import io.netty.buffer.Unpooled;
//? if >=1.21 {
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayloadCodecTest {

	@Test
	void requestRoundTripsLocationPing() {
		PingRequestPayload original = new PingRequestPayload(new Vec3(12.5, -3.25, 900.0), PingRequestPayload.NO_ENTITY, 0);
		//? if >=1.21 {
		assertRoundTrips(PingRequestPayload.STREAM_CODEC, original);
		//?} else {
		/*assertRoundTrips(PingRequestPayload::write, PingRequestPayload::read, original);
		*///?}
	}

	@Test
	void requestRoundTripsEntityPing() {
		PingRequestPayload original = new PingRequestPayload(new Vec3(1, 2, 3), 4711, 3);
		//? if >=1.21 {
		assertRoundTrips(PingRequestPayload.STREAM_CODEC, original);
		//?} else {
		/*assertRoundTrips(PingRequestPayload::write, PingRequestPayload::read, original);
		*///?}
	}

	@Test
	void broadcastRoundTripsIncludingColor() {
		PingBroadcastPayload original = new PingBroadcastPayload(new Vec3(-5, 64, 5), 42, 2, 0xFF55FF55);
		//? if >=1.21 {
		assertRoundTrips(PingBroadcastPayload.STREAM_CODEC, original);
		//?} else {
		/*assertRoundTrips(PingBroadcastPayload::write, PingBroadcastPayload::read, original);
		*///?}
	}

	//? if >=1.21 {
	@Test
	void payloadIdsAreNamespaced() {
		assertEquals("callouts:ping_request", PingRequestPayload.TYPE.id().toString());
		assertEquals("callouts:ping_broadcast", PingBroadcastPayload.TYPE.id().toString());
		assertEquals("callouts:config_sync", PingConfigSyncPayload.TYPE.id().toString());
		assertEquals("callouts:ping_rejected", PingRejectedPayload.TYPE.id().toString());
	}
	//?}

	@Test
	void configSyncRoundTripsMaxDistance() {
		PingConfigSyncPayload original = new PingConfigSyncPayload(96.0);
		//? if >=1.21 {
		assertRoundTrips(PingConfigSyncPayload.STREAM_CODEC, original);
		//?} else {
		/*assertRoundTrips(PingConfigSyncPayload::write, PingConfigSyncPayload::read, original);
		*///?}
	}

	@Test
	void rejectionRoundTrips() {
		PingRejectedPayload original = new PingRejectedPayload(PingRejectedPayload.LOCKED_OUT, 5.0);
		//? if >=1.21 {
		assertRoundTrips(PingRejectedPayload.STREAM_CODEC, original);
		//?} else {
		/*assertRoundTrips(PingRejectedPayload::write, PingRejectedPayload::read, original);
		*///?}
	}

	//? if >=1.21 {
	private static <T> void assertRoundTrips(StreamCodec<ByteBuf, T> codec, T original) {
		ByteBuf buf = Unpooled.buffer();
		codec.encode(buf, original);
		assertEquals(original, codec.decode(buf));
		assertEquals(0, buf.readableBytes());
	}
	//?} else {
	/*private static <T> void assertRoundTrips(BiConsumer<T, FriendlyByteBuf> write, Function<FriendlyByteBuf, T> read, T original) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		write.accept(original, buf);
		assertEquals(original, read.apply(buf));
		assertEquals(0, buf.readableBytes());
	}
	*///?}
}
