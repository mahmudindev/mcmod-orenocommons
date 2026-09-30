package com.github.mahmudindev.mcmod.orenocommons.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import com.github.mahmudindev.mcmod.orenocommons.platform.EnvSide;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface IPlatformHelper {
    String getPlatformName();

    Path getGameDir();

    Path getConfigDir();

    boolean isModLoaded(String id);

    EnvSide getEnvSide();

    boolean isDevelopmentEnvironment();

    <T, V extends T> Supplier<V> registerRegistryEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            ResourceLocation resourceLocation,
            Supplier<? extends V> supplier
    );

    <T extends CustomPacketPayload> void registerClientNetworkPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    );

    <T extends CustomPacketPayload> void registerServerNetworkPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    );

    <T extends CustomPacketPayload> void registerServerNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacket.Handler<T> handler
    );

    void sendNetworkPacketToPlayer(ServerPlayer player, CustomPacketPayload payload);

    boolean canSendNetworkPacketToPlayer(
            ServerPlayer player,
            CustomPacketPayload.Type<?> type
    );
}
