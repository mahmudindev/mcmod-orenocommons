package com.github.mahmudindev.mcmod.orenocommons.client.platform.services;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;

import java.util.ServiceLoader;

public class ClientServices {
    public static final IClientPlatformHelper PLATFORM = load(IClientPlatformHelper.class);

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(
                clazz,
                ClientServices.class.getClassLoader()
        ).findFirst().orElseThrow(() -> {
            return new NullPointerException("Failed to load service for " + clazz.getName());
        });

        OrenoCommons.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
