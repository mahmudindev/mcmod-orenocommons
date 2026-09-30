package com.github.mahmudindev.mcmod.orenocommons.client.network;

import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.ClientServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class UnifiedNetworkClient {
    public static <T extends CustomPacketPayload> void registerClientPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacketClient.Handler<T> handler
    ) {
        ClientServices.PLATFORM.registerClientNetworkPacketReceiver(type, handler);
    }

    public static void sendPacketToServer(CustomPacketPayload payload) {
        ClientServices.PLATFORM.sendNetworkPacketToServer(payload);
    }

    public static boolean canSendPacketToServer(CustomPacketPayload.Type<?> type) {
        return ClientServices.PLATFORM.canSendNetworkPacketToServer(type);
    }
}
