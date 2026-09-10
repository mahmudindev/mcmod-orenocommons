package com.github.mahmudindev.mcmod.orenocommons.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(
                clazz,
                Services.class.getClassLoader()
        ).findFirst().orElseThrow(() -> {
            return new NullPointerException("Failed to load service for " + clazz.getName());
        });

        OrenoCommons.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
