package com.github.mahmudindev.mcmod.orenocommons.network;

import com.github.mahmudindev.mcmod.orenocommons.platform.services.Services;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class UnifiedNetwork {
    public static <T extends CustomPacketPayload> void registerClientPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        Services.PLATFORM.registerClientNetworkPacketCodec(type, codec);
    }

    public static <T extends CustomPacketPayload> void registerServerPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        Services.PLATFORM.registerServerNetworkPacketCodec(type, codec);
    }

    public static <T extends CustomPacketPayload> void registerServerPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacket.Handler<T> handler
    ) {
        Services.PLATFORM.registerServerNetworkPacketReceiver(type, handler);
    }

    public static void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        Services.PLATFORM.sendNetworkPacketToPlayer(player, payload);
    }

    public static boolean canSendPacketToPlayer(
            ServerPlayer player,
            CustomPacketPayload.Type<?> type
    ) {
        return Services.PLATFORM.canSendNetworkPacketToPlayer(player, type);
    }
}
