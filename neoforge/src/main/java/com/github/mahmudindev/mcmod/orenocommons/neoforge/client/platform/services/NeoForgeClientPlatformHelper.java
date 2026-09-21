package com.github.mahmudindev.mcmod.orenocommons.neoforge.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.IClientPlatformHelper;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.OrenoCommonsNeoForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NeoForgeClientPlatformHelper implements IClientPlatformHelper {
    @Override
    public <T extends CustomPacketPayload> void registerClientNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            UnifiedNetworkPacketClient.Handler<T> handler
    ) {
        OrenoCommonsNeoForge.EVENT_BUS.addListener((RegisterPayloadHandlersEvent event) -> {
            event.registrar(type.id().getNamespace()).optional().playToClient(
                    type,
                    codec,
                    (payload, context) -> {
                        Minecraft client = Minecraft.getInstance();
                        handler.handle(payload, client);
                    }
            );
        });
    }

    @Override
    public void sendNetworkPacketToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    @Override
    public boolean canSendNetworkPacketToServer(CustomPacketPayload.Type<?> type) {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        return connection != null && connection.hasChannel(type);
    }
}
