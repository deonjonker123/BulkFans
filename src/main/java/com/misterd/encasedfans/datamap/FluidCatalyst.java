package com.misterd.encasedfans.datamap;

import com.misterd.encasedfans.processing.EFProcessingTypes;
import com.misterd.encasedfans.processing.ProcessingType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;

public record FluidCatalyst(ResourceKey<ProcessingType> processingType) {
    public static final Codec<FluidCatalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(EFProcessingTypes.REGISTRY).fieldOf("processing_type").forGetter(FluidCatalyst::processingType)
    ).apply(instance, FluidCatalyst::new));
}