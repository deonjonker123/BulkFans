package com.misterd.encasedfans.datamap;

import com.misterd.encasedfans.processing.EFProcessingTypes;
import com.misterd.encasedfans.processing.ProcessingType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record BlockCatalyst(ResourceKey<ProcessingType> processingType, Optional<StatePropertiesPredicate> state) {
    public static final Codec<BlockCatalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(EFProcessingTypes.REGISTRY).fieldOf("processing_type").forGetter(BlockCatalyst::processingType),
            StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(BlockCatalyst::state)
    ).apply(instance, BlockCatalyst::new));

    public boolean matches(BlockState blockState) {
        return state.isEmpty() || state.get().matches(blockState);
    }
}