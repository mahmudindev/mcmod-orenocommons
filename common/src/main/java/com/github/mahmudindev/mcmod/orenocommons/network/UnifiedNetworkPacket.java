package com.github.mahmudindev.mcmod.orenocommons.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class UnifiedNetworkPacket {
    public interface Context {
        MinecraftServer server();

        ServerPlayer player();

        void execute(Runnable task);
    }

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(Context ctx, T payload);
    }
}
