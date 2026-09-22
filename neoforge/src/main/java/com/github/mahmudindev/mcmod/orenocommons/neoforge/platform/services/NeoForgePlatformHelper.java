package com.github.mahmudindev.mcmod.orenocommons.neoforge.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.platform.EnvSide;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import com.github.mahmudindev.mcmod.orenocommons.platform.services.IPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.nio.file.Path;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {
    private static final ModList MODLIST = ModList.get();

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String id) {
        return MODLIST.isLoaded(id);
    }

    @Override
    public EnvSide getEnvSide() {
        switch (FMLEnvironment.dist) {
            case CLIENT -> {
                return EnvSide.CLIENT;
            }
            case DEDICATED_SERVER -> {
                return EnvSide.DEDICATED_SERVER;
            }
        }

        return null;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    public static IEventBus getModEventBus(String id) {
        return MODLIST.getModContainerById(id)
                .map(ModContainer::getEventBus)
                .orElseThrow(() -> new IllegalStateException("No mod found with id " + id));
    }

    @Override
    public <T, V extends T> Supplier<V> registerRegistryEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            ResourceLocation resourceLocation,
            Supplier<? extends V> supplier
    ) {
        DeferredRegister<T> deferredRegister = DeferredRegister.create(
                resourceKey,
                resourceLocation.getNamespace()
        );

        IEventBus eventBus = getModEventBus(OrenoCommons.MOD_ID);
        deferredRegister.register(eventBus);

        return deferredRegister.register(resourceLocation.getPath(), supplier);
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerNetworkPacketReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            UnifiedNetworkPacket.Handler<T> handler
    ) {
        IEventBus eventBus = getModEventBus(OrenoCommons.MOD_ID);
        eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            event.registrar(type.id().getNamespace()).optional().playToServer(
                    type,
                    codec,
                    (payload, context) -> {
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
                        }, payload);
                    }
            );
        });
    }

    @Override
    public void sendNetworkPacketToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public boolean canSendNetworkPacketToPlayer(
            ServerPlayer player,
            CustomPacketPayload.Type<?> type
    ) {
        return player.connection.hasChannel(type);
    }
}
