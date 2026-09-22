package com.github.mahmudindev.mcmod.orenocommons.fabric.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.client.platform.services.IClientPlatformHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricClientPlatformHelper implements IClientPlatformHelper {
    @Override
    public <T extends CustomPacketPayload> void registerClientNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            UnifiedNetworkPacketClient.Handler<T> handler
    ) {
        PayloadTypeRegistry.playS2C().register(type, codec);

        ClientPlayNetworking.registerGlobalReceiver(
                type,
                (payload, context) -> {
                    handler.handle(new UnifiedNetworkPacketClient.Context() {
                        @Override
                        public Minecraft client() {
                            return context.client();
                        }

                        @Override
                        public LocalPlayer player() {
                            return context.player();
                        }

                        @Override
                        public void execute(Runnable task) {
                            Minecraft client = context.client();
                            if (client.isSameThread()) {
                                task.run();
                            } else {
                                client.execute(task);
                            }
                        }
                    }, payload);
                }
        );
    }

    @Override
    public void sendNetworkPacketToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public boolean canSendNetworkPacketToServer(CustomPacketPayload.Type<?> type) {
        return ClientPlayNetworking.canSend(type);
    }
}
