package com.github.mahmudindev.mcmod.orenocommons.neoforge.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.IClientPlatformHelper;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.platform.services.NeoForgePlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NeoForgeClientPlatformHelper implements IClientPlatformHelper {
    @Override
    public <T extends CustomPacketPayload> void registerClientNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            UnifiedNetworkPacketClient.Handler<T> handler
    ) {
        IEventBus eventBus = NeoForgePlatformHelper.getModEventBus(OrenoCommons.MOD_ID);
        eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            event.registrar(type.id().getNamespace()).optional().playToClient(
                    type,
                    codec,
                    (payload, context) -> {
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
                        }, payload);
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
