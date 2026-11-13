package com.misterd.encasedfans.datamap;

import com.misterd.encasedfans.EncasedFans;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class EFDataMaps {
    public static final DataMapType<Block, BlockCatalyst> BLOCK_CATALYSTS = DataMapType
            .builder(Identifier.fromNamespaceAndPath(EncasedFans.MODID, "fan_catalysts"), Registries.BLOCK, BlockCatalyst.CODEC)
            .synced(BlockCatalyst.CODEC, false)
            .build();

    public static final DataMapType<Fluid, FluidCatalyst> FLUID_CATALYSTS = DataMapType
            .builder(Identifier.fromNamespaceAndPath(EncasedFans.MODID, "fan_catalysts"), Registries.FLUID, FluidCatalyst.CODEC)
            .synced(FluidCatalyst.CODEC, false)
            .build();

    public static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(BLOCK_CATALYSTS);
        event.register(FLUID_CATALYSTS);
    }
}