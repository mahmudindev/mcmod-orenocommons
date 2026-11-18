package com.github.mahmudindev.mcmod.orenocommons.platform;

import com.github.mahmudindev.mcmod.orenocommons.platform.services.Services;

import java.nio.file.Path;

public class UnifiedPlatform {
    public static String getName() {
        return Services.PLATFORM.getPlatformName();
    }

    public static LoaderType getLoaderType() {
        return Services.PLATFORM.getLoaderType();
    }

    public static Path getGameDir() {
        return Services.PLATFORM.getGameDir();
    }

    public static Path getConfigDir() {
        return Services.PLATFORM.getConfigDir();
    }

    public static boolean isModLoaded(String id) {
        return Services.PLATFORM.isModLoaded(id);
    }

    public static String getModVersion(String id) {
        return  Services.PLATFORM.getModVersion(id);
    }

    public static EnvSide getEnvSide() {
        return Services.PLATFORM.getEnvSide();
    }

    public static boolean isDevelopmentEnvironment() {
        return Services.PLATFORM.isDevelopmentEnvironment();
    }
}
