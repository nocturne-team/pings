package com.github._0x00acab.pings;

import java.nio.file.Path;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public sealed interface ModLoaderAccess {
    ModLoaderAccess INSTANCE =
        //? if fabric {
        new FabricLoaderAccess();
        //?} else {
        /*new NeoForgeLoaderAccess();
        *///?}

    Path getConfigDir();

    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    //? if fabric {
    final class FabricLoaderAccess implements ModLoaderAccess {
        @Override
        public Path getConfigDir() {
            return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir();
        }

        @Override
        public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, payload);
        }
    }
    //?} else {
    /*final class NeoForgeLoaderAccess implements ModLoaderAccess {
        @Override
        public Path getConfigDir() {
            return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
        }

        @Override
        public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, payload);
        }
    }
    *///?}
}
