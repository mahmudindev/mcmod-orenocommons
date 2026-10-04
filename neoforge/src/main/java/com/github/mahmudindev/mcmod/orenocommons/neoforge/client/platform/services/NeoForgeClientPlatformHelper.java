package com.github.mahmudindev.mcmod.orenocommons.neoforge.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.IClientPlatformHelper;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.client.network.UnifiedNetworkNeoForgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class NeoForgeClientPlatformHelper implements IClientPlatformHelper {
    @Override
    public <T extends CustomPacketPayload> void registerClientNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacketClient.Handler<T> handler
    ) {
        UnifiedNetworkNeoForgeClient.registerClientNetworkPacketReceiver(type, handler);
    }

    @Override
    public void sendNetworkPacketToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    @Override
    public boolean canSendNetworkPacketToServer(CustomPacketPayload.Type<?> type) {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        return connection != null && connection.hasChannel(type);
    }
}
