package com.github.mahmudindev.mcmod.orenocommons;

import com.github.mahmudindev.mcmod.orenocommons.platform.EnvSide;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;
import java.util.function.Supplier;

public class OrenoCommonsExpectPlatform {
    @ExpectPlatform
    public static String getPlatformName() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getGameDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String id) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static EnvSide getEnvSide() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T, V extends T> Supplier<V> registerRegistryEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            ResourceLocation resourceLocation,
            Supplier<? extends V> supplier
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerServerNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacket.Handler handler
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canSendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName
    ) {
        throw new AssertionError();
    }
}
