package com.misterd.bulkfans.datagen.custom;

import com.misterd.bulkfans.block.BFBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.loot.BlockLootSubProvider;

import java.util.Set;

public class BFLootTableProvider extends BlockLootSubProvider {
    public BFLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(BFBlocks.INDUSTRIAL_FAN.get());
        dropSelf(BFBlocks.TRAY.get());
        dropSelf(BFBlocks.FIRE_CATALYST.get());
        dropSelf(BFBlocks.SOUL_FIRE_CATALYST.get());
        dropSelf(BFBlocks.LAVA_CATALYST.get());
        dropSelf(BFBlocks.WATER_CATALYST.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BFBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
