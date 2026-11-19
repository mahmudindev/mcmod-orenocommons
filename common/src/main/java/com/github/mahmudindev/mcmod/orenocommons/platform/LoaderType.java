package com.github.mahmudindev.mcmod.orenocommons.platform;

public enum LoaderType {
    FABRIC,
    NEOFORGE;

    public boolean isFabric() {
        return this == FABRIC;
    }

    public boolean isNeoForge() {
        return this == NEOFORGE;
    }
}
