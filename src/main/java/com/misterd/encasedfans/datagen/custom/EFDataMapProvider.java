package com.misterd.encasedfans.datagen.custom;

import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.datamap.BlockCatalyst;
import com.misterd.encasedfans.datamap.EFDataMaps;
import com.misterd.encasedfans.datamap.FluidCatalyst;
import com.misterd.encasedfans.processing.EFProcessingTypes;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EFDataMapProvider extends DataMapProvider {
    public EFDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        Optional<StatePropertiesPredicate> lit = StatePropertiesPredicate.Builder.properties()
                .hasProperty(CampfireBlock.LIT, true)
                .build();

        builder(EFDataMaps.BLOCK_CATALYSTS)
                .add(block(Blocks.FIRE), new BlockCatalyst(EFProcessingTypes.SMOKING, Optional.empty()), false)
                .add(block(Blocks.CAMPFIRE), new BlockCatalyst(EFProcessingTypes.SMOKING, lit), false)
                .add(block(Blocks.SOUL_FIRE), new BlockCatalyst(EFProcessingTypes.HAUNTING, Optional.empty()), false)
                .add(block(Blocks.SOUL_CAMPFIRE), new BlockCatalyst(EFProcessingTypes.HAUNTING, lit), false)
                .add(block(EFBlocks.FIRE_CATALYST.get()), new BlockCatalyst(EFProcessingTypes.SMOKING, Optional.empty()), false)
                .add(block(EFBlocks.SOUL_FIRE_CATALYST.get()), new BlockCatalyst(EFProcessingTypes.HAUNTING, Optional.empty()), false)
                .add(block(EFBlocks.LAVA_CATALYST.get()), new BlockCatalyst(EFProcessingTypes.BLASTING, Optional.empty()), false)
                .add(block(EFBlocks.WATER_CATALYST.get()), new BlockCatalyst(EFProcessingTypes.WASHING, Optional.empty()), false);;


        builder(EFDataMaps.FLUID_CATALYSTS)
                .add(fluid(Fluids.WATER), new FluidCatalyst(EFProcessingTypes.WASHING), false)
                .add(fluid(Fluids.LAVA), new FluidCatalyst(EFProcessingTypes.BLASTING), false);
    }

    private static ResourceKey<Block> block(Block block) {
        return block.builtInRegistryHolder().key();
    }

    private static ResourceKey<Fluid> fluid(Fluid fluid) {
        return fluid.builtInRegistryHolder().key();
    }
}