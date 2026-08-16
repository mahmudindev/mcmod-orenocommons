package com.github.mahmudindev.mcmod.orenocommons.network;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommonsExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UnifiedNetwork {
    public static void registerServerPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacket.Handler handler
    ) {
        OrenoCommonsExpectPlatform.registerServerNetworkPacketReceiver(channelName, handler);
    }

    public static void sendPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        OrenoCommonsExpectPlatform.sendNetworkPacketToPlayer(player, channelName, buf);
    }
}
