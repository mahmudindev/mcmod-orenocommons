package com.github.mahmudindev.mcmod.orenocommons.neoforge.network;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.client.network.UnifiedNetworkNeoForgeClient;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.platform.services.NeoForgePlatformHelper;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.HashMap;
import java.util.Map;

public class UnifiedNetworkNeoForge {
    public static final Map<ResourceLocation, IPayloadHandler<?>> PACKET_HANDLERS = new HashMap<>();

    public static <T extends CustomPacketPayload> void registerClientPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        IEventBus eventBus = NeoForgePlatformHelper.getModEventBus(OrenoCommons.MOD_ID);
        eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            event.registrar(type.id().getNamespace()).optional().playToClient(
                    type,
                    codec,
                    (payload, context) -> {
                        IPayloadHandler<T> handler = (IPayloadHandler<T>) UnifiedNetworkNeoForgeClient.PACKET_HANDLERS.get(type.id());
                        if (handler != null) {
                            handler.handle(payload, context);
                        }
                    }
            );
        });
    }

    public static <T extends CustomPacketPayload> void registerServerPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        IEventBus eventBus = NeoForgePlatformHelper.getModEventBus(OrenoCommons.MOD_ID);
        eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            event.registrar(type.id().getNamespace()).optional().playToServer(
                    type,
                    codec,
                    (payload, context) -> {
                        IPayloadHandler<T> handler = (IPayloadHandler<T>) PACKET_HANDLERS.get(type.id());
                        if (handler != null) {
                            handler.handle(payload, context);
                        }
                    }
            );
        });
    }

    public static <T extends CustomPacketPayload> void registerServerNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacket.Handler<T> handler
    ) {
        PACKET_HANDLERS.put(type.id(), (payload, context) -> {
            handler.handle(new UnifiedNetworkPacket.Context() {
                @Override
                public MinecraftServer server() {
                    Player player = context.player();
                    return player.getServer();
                }

                @Override
                public ServerPlayer player() {
                    return (ServerPlayer) context.player();
                }

                @Override
                public void execute(Runnable task) {
                    context.enqueueWork(task);
                }
            }, (T) payload);
        });
    }
}
