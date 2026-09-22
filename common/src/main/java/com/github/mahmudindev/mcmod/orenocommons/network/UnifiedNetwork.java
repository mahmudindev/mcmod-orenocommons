package com.github.mahmudindev.mcmod.orenocommons.network;

import com.github.mahmudindev.mcmod.orenocommons.platform.services.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UnifiedNetwork {
    public static void registerServerPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacket.Handler handler
    ) {
        Services.PLATFORM.registerServerNetworkPacketReceiver(channelName, handler);
    }

    public static void sendPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        Services.PLATFORM.sendNetworkPacketToPlayer(player, channelName, buf);
    }

    public static boolean canSendPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName
    ) {
        return Services.PLATFORM.canSendNetworkPacketToPlayer(player, channelName);
    }
}
