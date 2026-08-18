package com.github.mahmudindev.mcmod.orenocommons.forge.client;

import com.github.mahmudindev.mcmod.orenocommons.forge.client.network.UnifiedNetworkForgeClient;

public class OrenoCommonsForgeClient {
    public OrenoCommonsForgeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        UnifiedNetworkForgeClient.init();
    }
}
