package com.github.mahmudindev.mcmod.orenocommons.forge.client.network;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedNetworkPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.forge.network.UnifiedNetworkForge;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
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

public class UnifiedNetworkForgeClient {
    private static final Map<String, EventNetworkChannel> CHANNELS = new HashMap<>();
    private static final Map<ResourceLocation, UnifiedNetworkPacketClient.Handler> PACKET_HANDLERS = new HashMap<>();
    private static final List<ResourceLocation> SERVER_PACKETS = new ArrayList<>();

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.HIGHEST,
                (ClientPlayerNetworkEvent.LoggingIn event) -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

                    buf.writeVarInt(PACKET_HANDLERS.size());
                    PACKET_HANDLERS.forEach((id, handler) -> {
                        buf.writeResourceLocation(id);
                    });

                    Minecraft client = Minecraft.getInstance();
                    ClientPacketListener connection = client.getConnection();
                    if (connection == null) {
                        throw new IllegalStateException("Connection is not available");
                    }
                    connection.send(new ServerboundCustomPayloadPacket(
                            UnifiedNetworkForge.CHANNEL_NAME,
                            buf
                    ));
                }
        );

        UnifiedNetworkForge.CHANNEL.addListener((NetworkEvent.ClientCustomPayloadEvent event) -> {
            NetworkEvent.Context context = event.getSource().get();
            if (context.getPacketHandled()) {
                return;
            }

            FriendlyByteBuf buf = event.getPayload();

            int count = buf.readVarInt();
            for (int i = 0; i < count; i++) {
                ResourceLocation id = buf.readResourceLocation();
                SERVER_PACKETS.add(id);
            }

            context.setPacketHandled(true);
        });

        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
            SERVER_PACKETS.clear();
        });
    }

    public static EventNetworkChannel getChannel(String namespace) {
        return CHANNELS.get(namespace);
    }

    public static boolean hasChannel(String namespace) {
        return CHANNELS.containsKey(namespace);
    }

    public static void registerClientPacketReceiver(
            ResourceLocation channelName,
            UnifiedNetworkPacketClient.Handler handler
    ) {
        CHANNELS.computeIfAbsent(channelName.getNamespace(), key -> {
            EventNetworkChannel channel;
            if (UnifiedNetworkForge.hasChannel(key)) {
                channel = UnifiedNetworkForge.getChannel(key);
            } else {
                channel = NetworkRegistry.newEventChannel(
                        UnifiedNetworkForge.getChannelName(key),
                        () -> UnifiedNetworkForge.CHANNEL_VERSION,
                        UnifiedNetworkForge.getChannelVersionChecker(),
                        UnifiedNetworkForge.getChannelVersionChecker()
                );
            }

            channel.addListener((NetworkEvent.ClientCustomPayloadEvent event) -> {
                NetworkEvent.Context context = event.getSource().get();
                if (context.getPacketHandled()) {
                    return;
                }

                FriendlyByteBuf buf = event.getPayload();

                ResourceLocation channelNameX = buf.readResourceLocation();
                UnifiedNetworkPacketClient.Handler handlerX = PACKET_HANDLERS.get(channelNameX);
                if (handlerX == null) {
                    buf.skipBytes(buf.readableBytes());
                    context.setPacketHandled(true);
                    return;
                }

                context.enqueueWork(() -> {
                    Minecraft client = Minecraft.getInstance();
                    handlerX.handle(client, buf);
                });

                context.setPacketHandled(true);
            });

            return channel;
        });

        PACKET_HANDLERS.put(channelName, handler);
    }

    public static void sendPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        if (connection == null) {
            throw new IllegalStateException("Connection is not available yet");
        }

        if (!SERVER_PACKETS.contains(channelName)) {
            connection.send(new ServerboundCustomPayloadPacket(channelName, buf));
        }

        FriendlyByteBuf bufX = new FriendlyByteBuf(Unpooled.buffer());
        bufX.writeResourceLocation(channelName);
        bufX.writeBytes(buf);

        connection.send(new ServerboundCustomPayloadPacket(
                UnifiedNetworkForge.getChannelName(channelName.getNamespace()),
                bufX
        ));
    }

    public static boolean canSendPacketToServer(ResourceLocation channelName) {
        if (SERVER_PACKETS.contains(channelName)) {
            return true;
        }

        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        if (connection == null) {
            throw new IllegalStateException("Connection is not available yet");
        }
        ConnectionData connectionData = NetworkHooks.getConnectionData(connection.getConnection());
        if (connectionData != null) {
            return connectionData.getChannels().containsKey(channelName);
        }

        return false;
    }
}
