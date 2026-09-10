package com.github.mahmudindev.mcmod.orenocommons.forge.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.IClientPlatformHelper;
import com.github.mahmudindev.mcmod.orenocommons.forge.client.network.UnifiedNetworkForgeClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ForgeClientPlatformHelper implements IClientPlatformHelper {
    @Override
    public void registerClientNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacketClient.Handler handler
    ) {
        UnifiedNetworkForgeClient.registerClientPacketReceiver(channelName, handler);
    }

    @Override
    public void sendNetworkPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        UnifiedNetworkForgeClient.sendPacketToServer(channelName, buf);
    }

    @Override
    public boolean canSendNetworkPacketToServer(ResourceLocation channelName) {
        return UnifiedNetworkForgeClient.canSendPacketToServer(channelName);
    }
}
