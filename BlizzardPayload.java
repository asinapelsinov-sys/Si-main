package com.example.blizzardmod.network;

import com.example.blizzardmod.BlizzardMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Сервер -> клиент: метель включена / выключена. */
public record BlizzardPayload(boolean active) implements CustomPacketPayload {

    public static final Type<BlizzardPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlizzardMod.MODID, "blizzard"));

    public static final StreamCodec<ByteBuf, BlizzardPayload> CODEC =
            ByteBufCodecs.BOOL.map(BlizzardPayload::new, BlizzardPayload::active);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
