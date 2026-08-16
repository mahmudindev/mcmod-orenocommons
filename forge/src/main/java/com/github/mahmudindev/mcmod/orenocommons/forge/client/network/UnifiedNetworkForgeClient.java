package com.github.mahmudindev.mcmod.orenocommons.forge.client.network;

import com.github.mahmudindev.mcmod.orenocommons.client.network.UnifiedPacketClient;
import com.github.mahmudindev.mcmod.orenocommons.forge.network.UnifiedNetworkForge;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.HashMap;
import java.util.Map;

public class UnifiedNetworkForgeClient {
    private static final Map<String, EventNetworkChannel> CHANNELS = new HashMap<>();
    private static final Map<ResourceLocation, UnifiedPacketClient.Handler> PACKET_HANDLERS = new HashMap<>();

    public static EventNetworkChannel getChannel(String namespace) {
        return CHANNELS.get(namespace);
    }

    public static boolean hasChannel(String namespace) {
        return CHANNELS.containsKey(namespace);
    }

    public static void registerClientPacketReceiver(
            ResourceLocation channelName,
            UnifiedPacketClient.Handler handler
    ) {
        CHANNELS.computeIfAbsent(channelName.getNamespace(), key -> {
            EventNetworkChannel channel;
            if (UnifiedNetworkForge.hasChannel(key)) {
                channel = UnifiedNetworkForge.getChannel(key);
            } else {
                channel = NetworkRegistry.newEventChannel(
                        UnifiedNetworkForge.getChannelName(key),
                        () -> UnifiedNetworkForge.CHANNEL_VERSION,
                        UnifiedNetworkForge.CHANNEL_VERSION::equals,
                        UnifiedNetworkForge.CHANNEL_VERSION::equals
                );
            }

            channel.addListener((NetworkEvent.ClientCustomPayloadEvent event) -> {
                FriendlyByteBuf buf = event.getPayload();

                ResourceLocation channelNameX = buf.readResourceLocation();
                UnifiedPacketClient.Handler handlerX = PACKET_HANDLERS.get(channelNameX);

                NetworkEvent.Context context = event.getSource().get();

                context.enqueueWork(() -> {
                    Minecraft client = Minecraft.getInstance();
                    handlerX.handle(client, buf);
                });
            });

            return channel;
        });

        PACKET_HANDLERS.put(channelName, handler);
    }

    public static void sendPacketToServer(
            ResourceLocation channelName,
            FriendlyByteBuf buf
    ) {
        FriendlyByteBuf bufX = new FriendlyByteBuf(Unpooled.buffer());
        bufX.writeResourceLocation(channelName);
        bufX.writeBytes(buf);

        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        if (connection == null) {
            throw new IllegalStateException("Cannot send packets when not in game!");
        }
        connection.send(new ServerboundCustomPayloadPacket(
                UnifiedNetworkForge.getChannelName(channelName.getNamespace()),
                bufX
        ));
    }
}
