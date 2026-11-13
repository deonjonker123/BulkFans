package com.misterd.encasedfans.processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

public record EntityEffect(Holder<MobEffect> effect, int amplifier, Optional<Integer> duration) {
    public static final Codec<EntityEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MobEffect.CODEC.fieldOf("id").forGetter(EntityEffect::effect),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(EntityEffect::amplifier),
            Codec.intRange(1, 72000).optionalFieldOf("duration").forGetter(EntityEffect::duration)
    ).apply(instance, EntityEffect::new));
}