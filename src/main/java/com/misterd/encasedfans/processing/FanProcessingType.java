package com.misterd.encasedfans.processing;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum FanProcessingType implements StringRepresentable {
    NONE(ParticleTypes.CLOUD),
    WASHING(ParticleTypes.SPLASH),
    SMOKING(ParticleTypes.SMOKE),
    BLASTING(ParticleTypes.SMALL_FLAME),
    HAUNTING(ParticleTypes.SOUL_FIRE_FLAME);

    public static final StringRepresentable.EnumCodec<FanProcessingType> CODEC = StringRepresentable.fromEnum(FanProcessingType::values);

    private final ParticleOptions particle;

    FanProcessingType(ParticleOptions particle) {
        this.particle = particle;
    }

    public ParticleOptions particle() {
        return particle;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static @Nullable FanProcessingType fromCatalyst(BlockState state) {
        if (state.is(Blocks.WATER)) {
            return WASHING;
        }
        if (state.is(Blocks.LAVA)) {
            return BLASTING;
        }
        if (state.is(Blocks.FIRE)) {
            return SMOKING;
        }
        if (state.is(Blocks.SOUL_FIRE)) {
            return HAUNTING;
        }
        if (state.is(Blocks.CAMPFIRE) && state.getValue(CampfireBlock.LIT)) {
            return SMOKING;
        }
        if (state.is(Blocks.SOUL_CAMPFIRE) && state.getValue(CampfireBlock.LIT)) {
            return HAUNTING;
        }
        return null;
    }

    public boolean ignitesEntities() {
        return this == SMOKING || this == BLASTING;
    }

    public boolean darkensEntities() {
        return this == HAUNTING;
    }

    public boolean extinguishesEntities() {
        return this == WASHING;
    }
}