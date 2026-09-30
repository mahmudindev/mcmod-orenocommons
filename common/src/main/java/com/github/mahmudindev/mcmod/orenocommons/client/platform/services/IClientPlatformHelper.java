package com.github.mahmudindev.mcmod.orenocommons.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IClientPlatformHelper {
    <T extends CustomPacketPayload> void registerClientNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacketClient.Handler<T> handler
    );

    void sendNetworkPacketToServer(CustomPacketPayload payload);

    boolean canSendNetworkPacketToServer(CustomPacketPayload.Type<?> type);
}
