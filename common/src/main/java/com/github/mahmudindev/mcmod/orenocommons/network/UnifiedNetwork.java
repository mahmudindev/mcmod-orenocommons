package com.github.mahmudindev.mcmod.orenocommons.network;

import com.github.mahmudindev.mcmod.orenocommons.OrenoCommons;
import com.github.mahmudindev.mcmod.orenocommons.platform.services.Services;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UnifiedNetwork {
    public static <T extends CustomPacketPayload> void registerServerPacketReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            UnifiedNetworkPacket.Handler<T> handler
    ) {
        Services.PLATFORM.registerServerNetworkPacketReceiver(type, codec, handler);
    }

    public static void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        Services.PLATFORM.sendNetworkPacketToPlayer(player, payload);
    }

    public static boolean canSendPacketToPlayer(
            ServerPlayer player,
            CustomPacketPayload.Type<?> type
    ) {
        return Services.PLATFORM.canSendNetworkPacketToPlayer(player, type);
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
                gzip.finish();
            } catch (IOException e) {
                OrenoCommons.LOGGER.error("Failed to write compressed packet", e);
                return;
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
                bufX.writeBytes(bytesX);
            } catch (IOException e) {
                OrenoCommons.LOGGER.error("Failed to read compressed packet", e);
                return;
            }

            payload.accept(bufX);
        } finally {
            bufX.release();
        }
    }
}
