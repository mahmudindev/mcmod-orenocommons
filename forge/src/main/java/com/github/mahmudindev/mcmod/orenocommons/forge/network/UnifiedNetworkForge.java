package com.github.mahmudindev.mcmod.orenocommons.forge.network;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.forge.client.network.UnifiedNetworkForgeClient;
import com.github.mahmudindev.mcmod.orenocommons.network.UnifiedNetworkPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class UnifiedNetworkForge {
    public static final String CHANNEL_VERSION = "1.0";
    public static final ResourceLocation CHANNEL_NAME = ResourceLocation.fromNamespaceAndPath(
            OrenoCommons.MOD_ID, "default"
    );
    public static final EventNetworkChannel CHANNEL = NetworkRegistry.newEventChannel(
            CHANNEL_NAME,
            () -> CHANNEL_VERSION,
            getChannelVersionChecker(),
            getChannelVersionChecker()
    );
    private static final Map<String, EventNetworkChannel> CHANNELS = new HashMap<>();
    private static final Map<ResourceLocation, UnifiedNetworkPacket.Handler> PACKET_HANDLERS = new HashMap<>();
    private static final Map<ServerPlayer, List<ResourceLocation>> PLAYER_PACKETS = new HashMap<>();

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                (PlayerEvent.PlayerLoggedInEvent event) -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

                    buf.writeVarInt(PACKET_HANDLERS.size());
                    PACKET_HANDLERS.forEach((id, handler) -> {
                        buf.writeResourceLocation(id);
                    });

                    ServerPlayer sender = (ServerPlayer) event.getEntity();
                    sender.connection.send(new ClientboundCustomPayloadPacket(
                            CHANNEL_NAME,
                            buf
                    ));
                }
        );

        CHANNEL.addListener((NetworkEvent.ServerCustomPayloadEvent event) -> {
            NetworkEvent.Context context = event.getSource().get();
            if (context.getPacketHandled()) {
                return;
            }

            FriendlyByteBuf buf = event.getPayload();

            ServerPlayer sender = context.getSender();
            int count = buf.readVarInt();
            for (int i = 0; i < count; i++) {
                ResourceLocation id = buf.readResourceLocation();
                List<ResourceLocation> playerPackets = PLAYER_PACKETS.computeIfAbsent(
                        sender,
                        Key -> new ArrayList<>()
                );
                playerPackets.add(id);
            }

            context.setPacketHandled(true);
        });

        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            PLAYER_PACKETS.remove((ServerPlayer) event.getEntity());
        });
    }

    public static Predicate<String> getChannelVersionChecker() {
        return NetworkRegistry.acceptMissingOr(CHANNEL_VERSION::equals);
    }

    public static EventNetworkChannel getChannel(String namespace) {
        return CHANNELS.get(namespace);
    }

    public static boolean hasChannel(String namespace) {
        return CHANNELS.containsKey(namespace);
    }

    public static ResourceLocation getChannelName(String namespace) {
        return ResourceLocation.fromNamespaceAndPath(
                namespace,
                OrenoCommons.MOD_ID + "_network"
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
                        getChannelVersionChecker(),
                        getChannelVersionChecker()
                );
            }

            channel.addListener((NetworkEvent.ServerCustomPayloadEvent event) -> {
                NetworkEvent.Context context = event.getSource().get();
                if (context.getPacketHandled()) {
                    return;
                }

                FriendlyByteBuf buf = event.getPayload();

                ResourceLocation channelNameX = buf.readResourceLocation();
                UnifiedNetworkPacket.Handler handlerX = PACKET_HANDLERS.get(channelNameX);
                if (handlerX == null) {
                    buf.skipBytes(buf.readableBytes());
                    context.setPacketHandled(true);
                    return;
                }

                ServerPlayer sender = context.getSender();
                context.enqueueWork(() -> {
                    MinecraftServer server = sender != null ? sender.getServer() : null;
                    handlerX.handle(server, sender, buf);
                });

                context.setPacketHandled(true);
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
        List<ResourceLocation> playerPackets = PLAYER_PACKETS.get(player);
        if (playerPackets != null && !playerPackets.contains(channelName)) {
            player.connection.send(new ClientboundCustomPayloadPacket(channelName, buf));
            return;
        }

        FriendlyByteBuf bufX = new FriendlyByteBuf(Unpooled.buffer());
        bufX.writeResourceLocation(channelName);
        bufX.writeBytes(buf);

        player.connection.send(new ClientboundCustomPayloadPacket(
                getChannelName(channelName.getNamespace()),
                bufX
        ));
    }

    public static boolean canSendPacketToPlayer(
            ServerPlayer player,
            ResourceLocation channelName
    ) {
        List<ResourceLocation> playerPackets = PLAYER_PACKETS.get(player);
        if (playerPackets != null && playerPackets.contains(channelName)) {
            return true;
        }

        Connection connection = player.connection.connection;
        ConnectionData connectionData = NetworkHooks.getConnectionData(connection);
        if (connectionData != null) {
            return connectionData.getChannels().containsKey(channelName);
        }

        return false;
    }
}
