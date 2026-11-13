package com.misterd.encasedfans.processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;
import java.util.Optional;

public record ProcessingType(
        ParticleOptions particle,
        Optional<CookingFallback> cookingFallback,
        Optional<CopperTransform> copperTransform,
        boolean ignitesEntities,
        boolean extinguishesEntities,
        List<EntityEffect> effects) {

    public static final Codec<ProcessingType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ProcessingType::particle),
            CookingFallback.CODEC.optionalFieldOf("vanilla_recipes").forGetter(ProcessingType::cookingFallback),
            CopperTransform.CODEC.optionalFieldOf("copper_transform").forGetter(ProcessingType::copperTransform),
            Codec.BOOL.optionalFieldOf("ignite_entities", false).forGetter(ProcessingType::ignitesEntities),
            Codec.BOOL.optionalFieldOf("extinguish_entities", false).forGetter(ProcessingType::extinguishesEntities),
            EntityEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(ProcessingType::effects)
    ).apply(instance, ProcessingType::new));
}