package com.github.mahmudindev.mcmod.orenocommons.platform;

public enum EnvSide {
    CLIENT,
    DEDICATED_SERVER;

    public boolean isClient() {
        return this == CLIENT;
    }

    public boolean isDedicatedServer() {
        return this == DEDICATED_SERVER;
    }
}
