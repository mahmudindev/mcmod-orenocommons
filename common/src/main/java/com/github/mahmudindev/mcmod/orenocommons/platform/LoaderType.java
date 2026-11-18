package com.github.mahmudindev.mcmod.orenocommons.platform;

public enum LoaderType {
    FABRIC,
    FORGE;

    public boolean isFabric() {
        return this == FABRIC;
    }

    public boolean isForge() {
        return this == FORGE;
    }
}
