package com.misterd.bulkfans.processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public record ProcessingProgress(Optional<ResourceKey<ProcessingType>> type, int ticks) {
    public static final ProcessingProgress EMPTY = new ProcessingProgress(Optional.empty(), 0);

    public static final MapCodec<ProcessingProgress> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(BFProcessingTypes.REGISTRY).optionalFieldOf("type").forGetter(ProcessingProgress::type),
            Codec.INT.fieldOf("ticks").forGetter(ProcessingProgress::ticks)
    ).apply(instance, ProcessingProgress::new));

    public boolean isFor(ResourceKey<ProcessingType> key) {
        return type.isPresent() && type.get().equals(key);
    }
}