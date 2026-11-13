package com.misterd.encasedfans.datagen.custom;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.util.EFTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class EFItemTagsProvider extends ItemTagsProvider {
    public EFItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, EncasedFans.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EFTags.Items.BLASTING_INCLUDE);
        tag(EFTags.Items.BLASTING_EXCLUDE);
    }
}