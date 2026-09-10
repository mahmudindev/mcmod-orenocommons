package com.github.mahmudindev.mcmod.orenocommons.network;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.platform.services.Services;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public static void writeCompressedBuffer(
            FriendlyByteBuf buf,
            Consumer<FriendlyByteBuf> payload
    ) {
        FriendlyByteBuf bufX = new FriendlyByteBuf(Unpooled.buffer());

        try {
            payload.accept(bufX);

            byte[] bytes = new byte[bufX.readableBytes()];
            bufX.readBytes(bytes);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
                gzip.write(bytes);
            } catch (IOException e) {
                OrenoCommons.LOGGER.error("Failed to write compressed packet", e);
            }
            byte[] bytesX = baos.toByteArray();

            buf.writeVarInt(bytesX.length);
            buf.writeBytes(bytesX);
        } finally {
            bufX.release();
        }
    }

    public static void readCompressedBuffer(
            FriendlyByteBuf buf,
            Consumer<FriendlyByteBuf> payload
    ) {
        FriendlyByteBuf bufX = new FriendlyByteBuf(Unpooled.buffer());

        try {
            int length = buf.readVarInt();
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try (GZIPInputStream gzip = new GZIPInputStream(bais)) {
                byte[] bytesX = gzip.readAllBytes();
                bufX.readBytes(bytesX);
            } catch (IOException e) {
                OrenoCommons.LOGGER.error("Failed to read compressed packet", e);
            }

            payload.accept(bufX);
        } finally {
            bufX.release();
        }
    }
}
