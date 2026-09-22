package com.github.mahmudindev.mcmod.orenocommons.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UnifiedNetworkUtility {
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
                throw new RuntimeException("Write compressed packet payload failed", e);
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
            Consumer<FriendlyByteBuf> payload,
            int maxUncompressedSize
    ) {
        FriendlyByteBuf bufX = new FriendlyByteBuf(Unpooled.buffer());

        try {
            int length = buf.readVarInt();
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try (GZIPInputStream gzip = new GZIPInputStream(bais)) {
                byte[] bytesX = gzip.readNBytes(maxUncompressedSize + 1);

                if (bytesX.length > maxUncompressedSize) {
                    throw new IOException("Compressed packet payload too large");
                }

                bufX.writeBytes(bytesX);
            } catch (IOException e) {
                throw new RuntimeException("Read compressed packet payload failed", e);
            }

            payload.accept(bufX);
        } finally {
            bufX.release();
        }
    }
}
