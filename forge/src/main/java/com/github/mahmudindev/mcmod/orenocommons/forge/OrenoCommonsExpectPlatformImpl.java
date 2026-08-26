package com.github.mahmudindev.mcmod.orenocommons.forge;

import com.github.mahmudindev.mcmod.orenocommons.forge.network.UnifiedNetworkForge;
import com.github.mahmudindev.mcmod.orenocommons.platform.EnvSide;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;

import java.nio.file.Path;
import java.util.function.Supplier;

public class OrenoCommonsExpectPlatformImpl {
    private static final ModList MODLIST = ModList.get();

    public static String getPlatformName() {
        return "Forge";
    }

    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static boolean isModLoaded(String id) {
        return MODLIST.isLoaded(id);
    }

    public static EnvSide getEnvSide() {
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

    public static boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    public static <T, V extends T> Supplier<V> registerRegistryEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            ResourceLocation resourceLocation,
            Supplier<? extends V> supplier
    ) {
        DeferredRegister<T> deferredRegister = DeferredRegister.create(
                resourceKey,
                resourceLocation.getNamespace()
        );

        //noinspection removal
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        deferredRegister.register(modEventBus);

        return deferredRegister.register(resourceLocation.getPath(), supplier);
    }

    public static void registerServerNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacket.Handler handler
    ) {
        UnifiedNetworkForge.registerServerPacketReceiver(channelName, handler);
    }

    public static void sendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        UnifiedNetworkForge.sendPacketToPlayer(player, channelName, buf);
    }

    public static boolean canSendNetworkPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName
    ) {
        return UnifiedNetworkForge.canSendPacketToPlayer(player, channelName);
    }
}
