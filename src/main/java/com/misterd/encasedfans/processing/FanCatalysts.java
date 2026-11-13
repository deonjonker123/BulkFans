package com.misterd.encasedfans.processing;

import com.misterd.encasedfans.datamap.BlockCatalyst;
import com.misterd.encasedfans.datamap.EFDataMaps;
import com.misterd.encasedfans.datamap.FluidCatalyst;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public final class FanCatalysts {
    private FanCatalysts() {
    }

    public static Optional<Holder<ProcessingType>> find(Level level, BlockState state) {
        return catalystKey(state).flatMap(key -> level.registryAccess().lookupOrThrow(EFProcessingTypes.REGISTRY).get(key)).map(holder -> (Holder<ProcessingType>) holder);
    }

    private static Optional<ResourceKey<ProcessingType>> catalystKey(BlockState state) {
        BlockCatalyst block = state.getBlock().builtInRegistryHolder().getData(EFDataMaps.BLOCK_CATALYSTS);
        if (block != null && block.matches(state)) {
            return Optional.of(block.processingType());
        }
        if (!(state.getBlock() instanceof LiquidBlock)) {
            return Optional.empty();
        }
        Fluid fluid = state.getFluidState().getType();
        if (fluid instanceof FlowingFluid flowing) {
            fluid = flowing.getSource();
        }
        FluidCatalyst catalyst = fluid.builtInRegistryHolder().getData(EFDataMaps.FLUID_CATALYSTS);
        return catalyst == null ? Optional.empty() : Optional.of(catalyst.processingType());
    }
}