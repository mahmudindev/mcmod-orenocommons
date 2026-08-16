package com.github.mahmudindev.mcmod.orenocommons.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class UnifiedPacketClient {
    @FunctionalInterface
    public interface Handler {
        void handle(Minecraft client, FriendlyByteBuf buf);
    }
}
