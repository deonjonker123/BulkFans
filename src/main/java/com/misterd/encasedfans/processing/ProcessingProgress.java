package com.misterd.encasedfans.processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ProcessingProgress(FanProcessingType type, int ticks) {
    public static final ProcessingProgress EMPTY = new ProcessingProgress(FanProcessingType.NONE, 0);

    public static final MapCodec<ProcessingProgress> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FanProcessingType.CODEC.fieldOf("type").forGetter(ProcessingProgress::type),
            Codec.INT.fieldOf("ticks").forGetter(ProcessingProgress::ticks)
    ).apply(instance, ProcessingProgress::new));
}