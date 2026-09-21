package com.github.mahmudindev.mcmod.orenocommons.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class UnifiedNetworkPacket {
    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(T payload, MinecraftServer server, ServerPlayer player);
    }
}
