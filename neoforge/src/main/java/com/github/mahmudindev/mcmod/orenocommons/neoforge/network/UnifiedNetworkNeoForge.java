package com.github.mahmudindev.mcmod.orenocommons.neoforge.network;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.client.network.UnifiedNetworkNeoForgeClient;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.mixin.ServerPlayerAccessor;
import com.github.mahmudindev.mcmod.orenocommons.neoforge.platform.services.NeoForgePlatformHelper;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnifiedNetworkNeoForge {
    private static final Set<Identifier> PACKET_CODECS = new HashSet<>();
    public static final Map<Identifier, IPayloadHandler<?>> PACKET_HANDLERS = new HashMap<>();

    public static <T extends CustomPacketPayload> void registerPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        if (!PACKET_CODECS.add(type.id())) {
            return;
        }

        IEventBus eventBus = NeoForgePlatformHelper.getModEventBus(OrenoCommons.MOD_ID);
        eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            event.registrar(type.id().getNamespace()).optional().playBidirectional(
                    type,
                    codec,
                    (payload, context) -> {
                        switch (context.flow()) {
                            case CLIENTBOUND -> {
                                IPayloadHandler<T> handler = (IPayloadHandler<T>) UnifiedNetworkNeoForgeClient.PACKET_HANDLERS.get(type.id());
                                if (handler != null) {
                                    handler.handle(payload, context);
                                }
                            }
                            case SERVERBOUND -> {
                                IPayloadHandler<T> handler = (IPayloadHandler<T>) PACKET_HANDLERS.get(type.id());
                                if (handler != null) {
                                    handler.handle(payload, context);
                                }
                            }
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
                    return ((ServerPlayerAccessor) player).orenocommons$getServer();
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
