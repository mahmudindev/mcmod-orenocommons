package com.github.mahmudindev.mcmod.orenocommons.fabric.client;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class OrenoCommonsClientExpectPlatformImpl {
    public static void registerClientNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacketClient.Handler handler
    ) {
        ClientPlayNetworking.registerGlobalReceiver(
                channelName,
                (client, handlerX, buf, sender) -> {
                    handler.handle(client, buf);
                }
        );
    }

    public static void sendNetworkPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        ClientPlayNetworking.send(channelName, buf);
    }

    public static boolean canSendNetworkPacketToServer(ResourceLocation channelName) {
        return ClientPlayNetworking.canSend(channelName);
    }
}
