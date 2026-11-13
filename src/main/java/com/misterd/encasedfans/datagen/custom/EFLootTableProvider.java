package com.misterd.encasedfans.datagen.custom;

import com.misterd.encasedfans.block.EFBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.loot.BlockLootSubProvider;

import java.util.Set;

public class EFLootTableProvider extends BlockLootSubProvider {
    public EFLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(EFBlocks.ENCASED_FAN.get());
        dropSelf(EFBlocks.TRAY.get());
        dropSelf(EFBlocks.FIRE_CATALYST.get());
        dropSelf(EFBlocks.SOUL_FIRE_CATALYST.get());
        dropSelf(EFBlocks.LAVA_CATALYST.get());
        dropSelf(EFBlocks.WATER_CATALYST.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EFBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
