package com.github.mahmudindev.mcmod.orenocommons.fabric;

import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;
import java.util.function.Supplier;

public class OrenoCommonsExpectPlatformImpl {
    private static final FabricLoader LOADER = FabricLoader.getInstance();

    public static String getPlatformName() {
        return "Fabric";
    }

    public static Path getGameDirectory() {
        return LOADER.getGameDir();
    }

    public static Path getConfigDirectory() {
        return LOADER.getConfigDir();
    }

    public static boolean isModLoaded(String id) {
        return LOADER.isModLoaded(id);
    }

    public static boolean isDevelopmentEnvironment() {
        return LOADER.isDevelopmentEnvironment();
    }

    public static <T, V extends T> Supplier<V> registerRegistryEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            ResourceLocation resourceLocation,
            Supplier<? extends V> supplier
    ) {
        Registry<?> registry = BuiltInRegistries.REGISTRY.get(resourceKey.location());
        if (registry == null) {
            throw new IllegalStateException("Unable to find the registry.");
        }

        //noinspection unchecked
        Registry<T> registryX = (Registry<T>) registry;
        V registered = Registry.register(registryX, resourceLocation, supplier.get());

        return () -> registered;
    }

    public static void registerServerNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacket.Handler handler
    ) {
        ServerPlayNetworking.registerGlobalReceiver(
                channelName,
                (server, player, handlerX, buf, responseSender) -> {
                    handler.handle(server, player, buf);
                }
        );
    }

    public static void sendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        ServerPlayNetworking.send(player, channelName, buf);
    }
}
