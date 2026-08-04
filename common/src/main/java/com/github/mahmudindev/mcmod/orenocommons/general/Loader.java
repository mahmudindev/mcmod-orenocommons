package com.github.mahmudindev.mcmod.orenocommons.general;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommonsExpectPlatform;

import java.nio.file.Path;

public class Loader {
    public static String getPlatformName() {
        return OrenoCommonsExpectPlatform.getPlatformName();
    }

    public static Path getGameDirectory() {
        return OrenoCommonsExpectPlatform.getGameDirectory();
    }

    public static Path getConfigDirectory() {
        return OrenoCommonsExpectPlatform.getConfigDirectory();
    }

    public static boolean isModLoaded(String id) {
        return OrenoCommonsExpectPlatform.isModLoaded(id);
    }

    public static boolean isDevelopmentEnvironment() {
        return OrenoCommonsExpectPlatform.isDevelopmentEnvironment();
    }
}
