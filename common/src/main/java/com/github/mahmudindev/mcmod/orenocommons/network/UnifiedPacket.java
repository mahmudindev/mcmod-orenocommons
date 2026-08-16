package com.github.mahmudindev.mcmod.orenocommons.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class UnifiedPacket {
    @FunctionalInterface
    public interface Handler {
        void handle(MinecraftServer server, ServerPlayer player, FriendlyByteBuf buf);
    }
}
