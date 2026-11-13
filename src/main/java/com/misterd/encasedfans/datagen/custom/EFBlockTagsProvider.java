package com.misterd.encasedfans.datagen.custom;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class EFBlockTagsProvider extends BlockTagsProvider {
    public EFBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, EncasedFans.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(EFBlocks.ENCASED_FAN.get())
                .add(EFBlocks.TRAY.get())
                .add(EFBlocks.FIRE_CATALYST.get())
                .add(EFBlocks.SOUL_FIRE_CATALYST.get())
                .add(EFBlocks.LAVA_CATALYST.get())
                .add(EFBlocks.WATER_CATALYST.get());
    }
}
