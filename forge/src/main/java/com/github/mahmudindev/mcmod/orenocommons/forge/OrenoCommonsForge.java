package com.github.mahmudindev.mcmod.orenocommons.forge;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.forge.client.OrenoCommonsForgeClient;
import com.github.mahmudindev.mcmod.orenocommons.forge.network.UnifiedNetworkForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(OrenoCommons.MOD_ID)
public final class OrenoCommonsForge {
    public OrenoCommonsForge() {
        // Run our common setup.
        OrenoCommons.init();

        UnifiedNetworkForge.init();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> OrenoCommonsForgeClient::new);
    }
}
