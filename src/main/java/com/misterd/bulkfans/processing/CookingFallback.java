package com.misterd.bulkfans.processing;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum CookingFallback implements StringRepresentable {
    SMOKING,
    BLASTING;

    public static final StringRepresentable.EnumCodec<CookingFallback> CODEC = StringRepresentable.fromEnum(CookingFallback::values);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}