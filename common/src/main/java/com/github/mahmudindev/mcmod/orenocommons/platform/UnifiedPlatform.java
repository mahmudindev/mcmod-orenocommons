package com.github.mahmudindev.mcmod.orenocommons.platform;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommonsExpectPlatform;

import java.nio.file.Path;

public class UnifiedPlatform {
    public static String getName() {
        return OrenoCommonsExpectPlatform.getPlatformName();
    }

    public static Path getGameDir() {
        return OrenoCommonsExpectPlatform.getGameDir();
    }

    public static Path getConfigDir() {
        return OrenoCommonsExpectPlatform.getConfigDir();
    }

    public static boolean isModLoaded(String id) {
        return OrenoCommonsExpectPlatform.isModLoaded(id);
    }

    public static EnvSide getEnvSide() {
        return OrenoCommonsExpectPlatform.getEnvSide();
    }

    public static boolean isDevelopmentEnvironment() {
        return OrenoCommonsExpectPlatform.isDevelopmentEnvironment();
    }
}
