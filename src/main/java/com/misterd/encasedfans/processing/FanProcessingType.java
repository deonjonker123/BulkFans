package com.misterd.encasedfans.processing;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public enum FanProcessingType {
    NONE,
    WASHING,
    SMOKING,
    BLASTING,
    HAUNTING;

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