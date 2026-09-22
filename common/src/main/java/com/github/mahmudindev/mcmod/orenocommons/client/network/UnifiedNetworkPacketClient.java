package com.github.mahmudindev.mcmod.orenocommons.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class UnifiedNetworkPacketClient {
    public interface Context {
        Minecraft client();

        LocalPlayer player();

        void execute(Runnable task);
    }

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(Context ctx, T payload);
    }
}
