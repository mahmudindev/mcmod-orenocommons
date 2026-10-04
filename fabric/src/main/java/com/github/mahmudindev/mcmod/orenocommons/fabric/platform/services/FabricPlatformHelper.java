package com.github.mahmudindev.mcmod.orenocommons.fabric.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.platform.EnvSide;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import com.github.mahmudindev.mcmod.orenocommons.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
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
            Identifier identifier,
            Supplier<? extends V> supplier
    ) {
        Registry<?> registry = BuiltInRegistries.REGISTRY.getValue(resourceKey.identifier());
        if (registry == null) {
            throw new IllegalStateException("Unable to find the registry.");
        }

        //noinspection unchecked
        Registry<T> registryX = (Registry<T>) registry;
        V registered = Registry.register(registryX, identifier, supplier.get());

        return () -> registered;
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientNetworkPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerNetworkPacketCodec(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            UnifiedNetworkPacket.Handler<T> handler
    ) {
        ServerPlayNetworking.registerGlobalReceiver(
                type,
                (payload, context) -> {
                    handler.handle(new UnifiedNetworkPacket.Context() {
                        @Override
                        public MinecraftServer server() {
                            return context.server();
                        }

                        @Override
                        public ServerPlayer player() {
                            return context.player();
                        }

                        @Override
                        public void execute(Runnable task) {
                            MinecraftServer server = context.server();
                            if (server.isSameThread()) {
                                task.run();
                            } else {
                                server.execute(task);
                            }
                        }
                    }, payload);
                }
        );
    }

    @Override
    public void sendNetworkPacketToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public boolean canSendNetworkPacketToPlayer(
            ServerPlayer player,
            CustomPacketPayload.Type<?> type
    ) {
        return ServerPlayNetworking.canSend(player, type);
    }
}
