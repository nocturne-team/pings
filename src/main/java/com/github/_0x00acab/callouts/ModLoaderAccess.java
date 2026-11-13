package com.github._0x00acab.callouts;

import java.nio.file.Path;
//? if >=1.21 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
import net.minecraft.server.level.ServerPlayer;

public sealed interface ModLoaderAccess {
	ModLoaderAccess INSTANCE =
		//? if fabric {
		new FabricLoaderAccess();
		//?} else {
		/*new NeoForgeLoaderAccess();
		*///?}

	Path getConfigDir();

	//? if >=1.21 {
	void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
	//?} else {
	/*// One of the payload records in the network package.
	void sendToPlayer(ServerPlayer player, Object payload);
	*///?}

	//? if fabric {
	final class FabricLoaderAccess implements ModLoaderAccess {
		@Override
		public Path getConfigDir() {
			return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir();
		}

		@Override
		public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
			if (net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player, payload.type())) {
				net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, payload);
			}
		}
	}
	//?}

	//? if neoforge && >=1.21 {
	/*final class NeoForgeLoaderAccess implements ModLoaderAccess {
		@Override
		public Path getConfigDir() {
			return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
		}

		@Override
		public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
			if (player.connection.hasChannel(payload)) {
				net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, payload);
			}
		}
	}
	*///?}

	//? if neoforge && <1.21 {
	/*final class NeoForgeLoaderAccess implements ModLoaderAccess {
		@Override
		public Path getConfigDir() {
			return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
		}

		@Override
		public void sendToPlayer(ServerPlayer player, Object payload) {
			com.github._0x00acab.callouts.network.LegacyNetwork.sendToPlayer(player, payload);
		}
	}
	*///?}
}
