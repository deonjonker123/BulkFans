package com.misterd.bulkfans.datamap;

import com.misterd.bulkfans.processing.BFProcessingTypes;
import com.misterd.bulkfans.processing.ProcessingType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;

public record FluidCatalyst(ResourceKey<ProcessingType> processingType) {
    public static final Codec<FluidCatalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(BFProcessingTypes.REGISTRY).fieldOf("processing_type").forGetter(FluidCatalyst::processingType)
    ).apply(instance, FluidCatalyst::new));
}