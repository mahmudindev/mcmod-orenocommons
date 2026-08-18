package com.github.mahmudindev.mcmod.orenocommons.forge.client;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.forge.client.network.UnifiedNetworkForgeClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class OrenoCommonsClientExpectPlatformImpl {
    public static void registerClientNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacketClient.Handler handler
    ) {
        UnifiedNetworkForgeClient.registerClientPacketReceiver(channelName, handler);
    }

    public static void sendNetworkPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        UnifiedNetworkForgeClient.sendPacketToServer(channelName, buf);
    }

    public static boolean canSendNetworkPacketToServer(ResourceLocation channelName) {
        return UnifiedNetworkForgeClient.canSendPacketToServer(channelName);
    }
}
