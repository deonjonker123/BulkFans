package com.misterd.bulkfans;

import com.misterd.bulkfans.attachment.BFAttachments;
import com.misterd.bulkfans.block.BFBlocks;
import com.misterd.bulkfans.blockentity.BFBlockEntities;
import com.misterd.bulkfans.config.BFConfig;
import com.misterd.bulkfans.datamap.BFDataMaps;
import com.misterd.bulkfans.item.BFCreativeTab;
import com.misterd.bulkfans.item.BFItems;
import com.misterd.bulkfans.processing.BFProcessingTypes;
import com.misterd.bulkfans.recipe.BFRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(BulkFans.MODID)
public class BulkFans {
    public static final String MODID = "bulkfans";

    public BulkFans(IEventBus modEventBus, ModContainer modContainer) {
        BFBlocks.register(modEventBus);
        BFItems.register(modEventBus);
        BFBlockEntities.register(modEventBus);
        BFCreativeTab.register(modEventBus);
        BFRecipes.register(modEventBus);
        BFAttachments.register(modEventBus);
        modEventBus.addListener(BFProcessingTypes::registerRegistry);
        modEventBus.addListener(BFDataMaps::registerDataMaps);

        modContainer.registerConfig(ModConfig.Type.SERVER, BFConfig.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, BFConfig.CLIENT_SPEC);
    }
}