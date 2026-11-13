package com.misterd.bulkfans.datagen.custom;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.block.BFBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class BFBlockTagsProvider extends BlockTagsProvider {
    public BFBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BulkFans.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BFBlocks.INDUSTRIAL_FAN.get())
                .add(BFBlocks.TRAY.get())
                .add(BFBlocks.FIRE_CATALYST.get())
                .add(BFBlocks.SOUL_FIRE_CATALYST.get())
                .add(BFBlocks.LAVA_CATALYST.get())
                .add(BFBlocks.WATER_CATALYST.get());
    }
}
