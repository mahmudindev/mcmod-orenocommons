package com.github.mahmudindev.mcmod.orenocommons.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class UnifiedNetworkPacket {
    public interface Context {
        MinecraftServer server();

        ServerPlayer player();

        void execute(Runnable task);
    }

    @FunctionalInterface
    public interface Handler {
        void handle(Context ctx, FriendlyByteBuf buf);
    }
}
