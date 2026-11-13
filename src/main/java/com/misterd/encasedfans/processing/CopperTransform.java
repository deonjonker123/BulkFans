package com.misterd.encasedfans.processing;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.DataMapHooks;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public enum CopperTransform implements StringRepresentable {
    OXIDIZE,
    DEOXIDIZE,
    WAX,
    UNWAX;

    public static final StringRepresentable.EnumCodec<CopperTransform> CODEC = StringRepresentable.fromEnum(CopperTransform::values);

    public @Nullable Block apply(Block block) {
        return switch (this) {
            case OXIDIZE -> DataMapHooks.getNextOxidizedStage(block);
            case DEOXIDIZE -> DataMapHooks.getPreviousOxidizedStage(block);
            case WAX -> DataMapHooks.getBlockWaxed(block);
            case UNWAX -> DataMapHooks.getBlockUnwaxed(block);
        };
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}