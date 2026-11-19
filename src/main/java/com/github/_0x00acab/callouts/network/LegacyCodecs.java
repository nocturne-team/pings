package com.github._0x00acab.callouts.network;

//? if <1.21 {
/*import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

// 1.20.1 has no StreamCodec, so payloads write themselves.
final class LegacyCodecs {
	private LegacyCodecs() {}

	static void writeVec3(FriendlyByteBuf buf, Vec3 vec) {
		buf.writeDouble(vec.x);
		buf.writeDouble(vec.y);
		buf.writeDouble(vec.z);
	}

	static Vec3 readVec3(FriendlyByteBuf buf) {
		return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
}
*///?}
