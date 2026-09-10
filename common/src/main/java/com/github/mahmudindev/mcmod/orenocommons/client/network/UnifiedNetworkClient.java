package com.github.mahmudindev.mcmod.orenocommons.client.network;

import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.ClientServices;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class UnifiedNetworkClient {
    public static void registerClientPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacketClient.Handler handler
    ) {
        ClientServices.PLATFORM.registerClientNetworkPacketReceiver(channelName, handler);
    }

    public static void sendPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        ClientServices.PLATFORM.sendNetworkPacketToServer(channelName, buf);
    }

    public static boolean canSendPacketToServer(ResourceLocation channelName) {
        return ClientServices.PLATFORM.canSendNetworkPacketToServer(channelName);
    }
}
