package com.misterd.bulkfans.item;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.block.BFBlocks;
import com.misterd.bulkfans.item.custom.IndustrialFanBlockItem;
import com.misterd.bulkfans.item.custom.SpannerItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BulkFans.MODID);

    public static final DeferredItem<IndustrialFanBlockItem> INDUSTRIAL_FAN = ITEMS.registerItem("industrial_fan",
            props -> new IndustrialFanBlockItem(BFBlocks.INDUSTRIAL_FAN.get(), props),
            props -> props.useBlockDescriptionPrefix());

    public static final DeferredItem<BlockItem> FIRE_CATALYST = ITEMS.registerSimpleBlockItem(BFBlocks.FIRE_CATALYST);
    public static final DeferredItem<BlockItem> SOUL_FIRE_CATALYST = ITEMS.registerSimpleBlockItem(BFBlocks.SOUL_FIRE_CATALYST);
    public static final DeferredItem<BlockItem> LAVA_CATALYST = ITEMS.registerSimpleBlockItem(BFBlocks.LAVA_CATALYST);
    public static final DeferredItem<BlockItem> WATER_CATALYST = ITEMS.registerSimpleBlockItem(BFBlocks.WATER_CATALYST);
    public static final DeferredItem<BlockItem> TRAY = ITEMS.registerSimpleBlockItem(BFBlocks.TRAY);

    public static final DeferredItem<SpannerItem> SPANNER = ITEMS.registerItem("spanner",
            SpannerItem::new,
            props -> props.stacksTo(1));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}