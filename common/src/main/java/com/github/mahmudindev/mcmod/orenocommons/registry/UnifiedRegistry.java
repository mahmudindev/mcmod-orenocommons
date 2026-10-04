package com.github.mahmudindev.mcmod.orenocommons.registry;

import com.github.mahmudindev.mcmod.orenocommons.platform.services.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class UnifiedRegistry {
    public static <T, V extends T> Supplier<V> registerEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            Identifier identifier,
            Supplier<? extends V> supplier
    ) {
        return Services.PLATFORM.registerRegistryEntry(
                resourceKey,
                identifier,
                supplier
        );
    }
}
