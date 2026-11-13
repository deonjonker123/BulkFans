package com.misterd.bulkfans.datagen.custom;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.item.BFItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class BFModelProvider extends ModelProvider {
    public BFModelProvider(PackOutput output) {
        super(output, BulkFans.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(BFItems.SPANNER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.of(BFItems.SPANNER);
    }
}