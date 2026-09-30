package com.github.mahmudindev.mcmod.orenocommons.neoforge.client.network;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.HashMap;
import java.util.Map;

public class UnifiedNetworkNeoForgeClient {
    public static final Map<ResourceLocation, IPayloadHandler<?>> PACKET_HANDLERS = new HashMap<>();

    public static  <T extends CustomPacketPayload> void registerClientNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacketClient.Handler<T> handler
    ) {
        PACKET_HANDLERS.put(type.id(), (payload, context) -> {
            handler.handle(new UnifiedNetworkPacketClient.Context() {
                @Override
                public Minecraft client() {
                    return Minecraft.getInstance();
                }

                @Override
                public LocalPlayer player() {
                    return (LocalPlayer) context.player();
                }

                @Override
                public void execute(Runnable task) {
                    context.enqueueWork(task);
                }
            }, (T) payload);
        });
    }
}
