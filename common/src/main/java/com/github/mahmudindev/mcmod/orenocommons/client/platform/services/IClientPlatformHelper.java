package com.github.mahmudindev.mcmod.orenocommons.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IClientPlatformHelper {
    void registerClientNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacketClient.Handler handler
    );

    void sendNetworkPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    );

    boolean canSendNetworkPacketToServer(ResourceLocation channelName);
}
