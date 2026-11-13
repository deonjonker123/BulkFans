package com.misterd.bulkfans.datagen.custom;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.util.BFTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class BFItemTagsProvider extends ItemTagsProvider {
    public BFItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BulkFans.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BFTags.Items.BLASTING_INCLUDE);
        tag(BFTags.Items.BLASTING_EXCLUDE);
    }
}