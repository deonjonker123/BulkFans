package com.misterd.bulkfans.recipe.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;

public record ProcessingOutput(ItemStackTemplate item, float chance) {
    public static final Codec<ProcessingOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.MAP_CODEC.forGetter(ProcessingOutput::item),
            Codec.floatRange(0, 1).optionalFieldOf("chance", 1F).forGetter(ProcessingOutput::chance)
    ).apply(instance, ProcessingOutput::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessingOutput> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, ProcessingOutput::item,
            ByteBufCodecs.FLOAT, ProcessingOutput::chance,
            ProcessingOutput::new);
}