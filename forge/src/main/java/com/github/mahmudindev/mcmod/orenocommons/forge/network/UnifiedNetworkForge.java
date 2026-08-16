package com.github.mahmudindev.mcmod.orenocommons.forge.network;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.forge.client.network.UnifiedNetworkForgeClient;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.HashMap;
import java.util.Map;

public class UnifiedNetworkForge {
    public static final String CHANNEL_VERSION = "1.0";
    private static final Map<String, EventNetworkChannel> CHANNELS = new HashMap<>();
    private static final Map<ResourceLocation, UnifiedNetworkPacket.Handler> PACKET_HANDLERS = new HashMap<>();

    public static EventNetworkChannel getChannel(String namespace) {
        return CHANNELS.get(namespace);
    }

    public static boolean hasChannel(String namespace) {
        return CHANNELS.containsKey(namespace);
    }

    public static ResourceLocation getChannelName(String namespace) {
        return ResourceLocation.fromNamespaceAndPath(
                namespace,
                OrenoCommons.MOD_ID + "-network"
        );
    }

    public static void registerServerPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacket.Handler handler
    ) {
        CHANNELS.computeIfAbsent(channelName.getNamespace(), key -> {
            EventNetworkChannel channel;
            if (UnifiedNetworkForgeClient.hasChannel(key)) {
                channel = UnifiedNetworkForgeClient.getChannel(key);
            } else {
                channel = NetworkRegistry.newEventChannel(
                        getChannelName(key),
                        () -> CHANNEL_VERSION,
                        CHANNEL_VERSION::equals,
                        CHANNEL_VERSION::equals
                );
            }

            channel.addListener((NetworkEvent.ServerCustomPayloadEvent event) -> {
                FriendlyByteBuf buf = event.getPayload();

                ResourceLocation channelNameX = buf.readResourceLocation();
                UnifiedNetworkPacket.Handler handlerX = PACKET_HANDLERS.get(channelNameX);

                NetworkEvent.Context context = event.getSource().get();

                ServerPlayer sender = context.getSender();
                context.enqueueWork(() -> {
                    MinecraftServer server = sender != null ? sender.getServer() : null;
                    handlerX.handle(server, sender, buf);
                });
            });

            return channel;
        });

        PACKET_HANDLERS.put(channelName, handler);
    }

    public static void sendPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        FriendlyByteBuf bufX = new FriendlyByteBuf(Unpooled.buffer());
        bufX.writeResourceLocation(channelName);
        bufX.writeBytes(buf);

        player.connection.send(new ClientboundCustomPayloadPacket(
                getChannelName(channelName.getNamespace()),
                bufX
        ));
    }
}
