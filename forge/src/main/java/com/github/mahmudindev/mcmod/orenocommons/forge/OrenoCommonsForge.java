package com.github.mahmudindev.mcmod.orenocommons.forge;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import net.minecraftforge.fml.common.Mod;

@Mod(OrenoCommons.MOD_ID)
public final class OrenoCommonsForge {
    public OrenoCommonsForge() {
        // Run our common setup.
        OrenoCommons.init();
    }
}
