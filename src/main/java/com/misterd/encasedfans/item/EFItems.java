package com.misterd.encasedfans.item;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.item.custom.EncasedFanBlockItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EncasedFans.MODID);

    public static final DeferredItem<EncasedFanBlockItem> ENCASED_FAN = ITEMS.registerItem("fan",
            props -> new EncasedFanBlockItem(EFBlocks.ENCASED_FAN.get(), props),
            props -> props.useBlockDescriptionPrefix());

    public static final DeferredItem<BlockItem> FIRE_CATALYST = ITEMS.registerSimpleBlockItem(EFBlocks.FIRE_CATALYST);
    public static final DeferredItem<BlockItem> SOUL_FIRE_CATALYST = ITEMS.registerSimpleBlockItem(EFBlocks.SOUL_FIRE_CATALYST);
    public static final DeferredItem<BlockItem> LAVA_CATALYST = ITEMS.registerSimpleBlockItem(EFBlocks.LAVA_CATALYST);
    public static final DeferredItem<BlockItem> WATER_CATALYST = ITEMS.registerSimpleBlockItem(EFBlocks.WATER_CATALYST);
    public static final DeferredItem<BlockItem> TRAY = ITEMS.registerSimpleBlockItem(EFBlocks.TRAY);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}