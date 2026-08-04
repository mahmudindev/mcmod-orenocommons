package com.github.mahmudindev.mcmod.orenocommons.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

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
}
