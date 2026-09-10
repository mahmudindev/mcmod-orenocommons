package com.github.mahmudindev.mcmod.orenocommons.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import com.github.mahmudindev.mcmod.orenocommons.platform.EnvSide;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
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

    void registerServerNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacket.Handler handler
    );

    void sendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    );

    boolean canSendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName
    );
}
