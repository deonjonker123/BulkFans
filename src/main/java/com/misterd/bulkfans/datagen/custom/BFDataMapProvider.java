package com.misterd.bulkfans.datagen.custom;

import com.misterd.bulkfans.block.BFBlocks;
import com.misterd.bulkfans.datamap.BlockCatalyst;
import com.misterd.bulkfans.datamap.BFDataMaps;
import com.misterd.bulkfans.datamap.FluidCatalyst;
import com.misterd.bulkfans.processing.BFProcessingTypes;
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

public class BFDataMapProvider extends DataMapProvider {
    public BFDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        Optional<StatePropertiesPredicate> lit = StatePropertiesPredicate.Builder.properties()
                .hasProperty(CampfireBlock.LIT, true)
                .build();

        builder(BFDataMaps.BLOCK_CATALYSTS)
                .add(block(Blocks.FIRE), new BlockCatalyst(BFProcessingTypes.SMOKING, Optional.empty()), false)
                .add(block(Blocks.CAMPFIRE), new BlockCatalyst(BFProcessingTypes.SMOKING, lit), false)
                .add(block(Blocks.SOUL_FIRE), new BlockCatalyst(BFProcessingTypes.HAUNTING, Optional.empty()), false)
                .add(block(Blocks.SOUL_CAMPFIRE), new BlockCatalyst(BFProcessingTypes.HAUNTING, lit), false)
                .add(block(BFBlocks.FIRE_CATALYST.get()), new BlockCatalyst(BFProcessingTypes.SMOKING, Optional.empty()), false)
                .add(block(BFBlocks.SOUL_FIRE_CATALYST.get()), new BlockCatalyst(BFProcessingTypes.HAUNTING, Optional.empty()), false)
                .add(block(BFBlocks.LAVA_CATALYST.get()), new BlockCatalyst(BFProcessingTypes.BLASTING, Optional.empty()), false)
                .add(block(BFBlocks.WATER_CATALYST.get()), new BlockCatalyst(BFProcessingTypes.WASHING, Optional.empty()), false);;


        builder(BFDataMaps.FLUID_CATALYSTS)
                .add(fluid(Fluids.WATER), new FluidCatalyst(BFProcessingTypes.WASHING), false)
                .add(fluid(Fluids.LAVA), new FluidCatalyst(BFProcessingTypes.BLASTING), false);
    }

    private static ResourceKey<Block> block(Block block) {
        return block.builtInRegistryHolder().key();
    }

    private static ResourceKey<Fluid> fluid(Fluid fluid) {
        return fluid.builtInRegistryHolder().key();
    }
}