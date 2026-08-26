package com.github.mahmudindev.mcmod.orenocommons.client.network;

import com.github.mahmudindev.mcmod.orenocommons.client.OrenoCommonsClientExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class UnifiedNetworkClient {
    public static void registerClientPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacketClient.Handler handler
    ) {
        OrenoCommonsClientExpectPlatform.registerClientNetworkPacketReceiver(channelName, handler);
    }

    public static void sendPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        OrenoCommonsClientExpectPlatform.sendNetworkPacketToServer(channelName, buf);
    }

    public static boolean canSendPacketToServer(ResourceLocation channelName) {
        return OrenoCommonsClientExpectPlatform.canSendNetworkPacketToServer(channelName);
    }
}
