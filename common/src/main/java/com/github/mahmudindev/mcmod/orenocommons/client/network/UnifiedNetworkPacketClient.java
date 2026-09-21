package com.github.mahmudindev.mcmod.orenocommons.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class UnifiedNetworkPacketClient {
    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(T payload, Minecraft client);
    }
}
