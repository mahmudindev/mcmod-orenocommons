package com.github.mahmudindev.mcmod.orenocommons.registry;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommonsExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class GeneralRegistry {
    public static <T, V extends T> Supplier<V> registerEntry(
            ResourceKey<? extends Registry<T>> resourceKey,
            ResourceLocation resourceLocation,
            Supplier<? extends V> supplier
    ) {
        return OrenoCommonsExpectPlatform.registerRegistryEntry(
                resourceKey,
                resourceLocation,
                supplier
        );
    }
}
