package com.github.mahmudindev.mcmod.orenocommons.fabric.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.platform.EnvSide;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import com.github.mahmudindev.mcmod.orenocommons.platform.services.IPlatformHelper;
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

public class FabricPlatformHelper implements IPlatformHelper {
    private static final FabricLoader LOADER = FabricLoader.getInstance();

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public Path getGameDir() {
        return LOADER.getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return LOADER.getConfigDir();
    }

    @Override
    public boolean isModLoaded(String id) {
        return LOADER.isModLoaded(id);
    }

    @Override
    public EnvSide getEnvSide() {
        switch (LOADER.getEnvironmentType()) {
            case CLIENT -> {
                return EnvSide.CLIENT;
            }
            case SERVER -> {
                return EnvSide.DEDICATED_SERVER;
            }
        }

        return null;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return LOADER.isDevelopmentEnvironment();
    }

    @Override
    public <T, V extends T> Supplier<V> registerRegistryEntry(
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

    @Override
    public void registerServerNetworkPacketReceiver(
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

    @Override
    public void sendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        ServerPlayNetworking.send(player, channelName, buf);
    }

    @Override
    public boolean canSendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName
    ) {
        return ServerPlayNetworking.canSend(player, channelName);
    }
}
