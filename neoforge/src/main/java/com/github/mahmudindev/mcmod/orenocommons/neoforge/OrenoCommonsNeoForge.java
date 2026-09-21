package com.github.mahmudindev.mcmod.orenocommons.neoforge;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(OrenoCommons.MOD_ID)
public final class OrenoCommonsNeoForge {
    public static IEventBus EVENT_BUS;

    public OrenoCommonsNeoForge(IEventBus eventBus) {
        EVENT_BUS = eventBus;

        // Run our common setup.
        OrenoCommons.init();
    }
}
