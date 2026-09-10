package com.github.mahmudindev.mcmod.orenocommons.fabric.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.IClientPlatformHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class FabricClientPlatformHelper implements IClientPlatformHelper {
    @Override
    public void registerClientNetworkPacketReceiver(
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

    @Override
    public void sendNetworkPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        ClientPlayNetworking.send(channelName, buf);
    }

    @Override
    public boolean canSendNetworkPacketToServer(ResourceLocation channelName) {
        return ClientPlayNetworking.canSend(channelName);
    }
}
