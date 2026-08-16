package com.github.mahmudindev.mcmod.orenocommons.client;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedPacketClient;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class OrenoCommonsClientExpectPlatform {
    @ExpectPlatform
    public static void registerClientNetworkPacketReceiver(
            ResourceLocation channelName,
            UnifiedPacketClient.Handler handler
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendNetworkPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        throw new AssertionError();
    }
}
