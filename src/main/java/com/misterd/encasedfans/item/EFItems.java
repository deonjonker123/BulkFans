package com.misterd.encasedfans.item;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.item.custom.EncasedFanBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EncasedFans.MODID);

    public static final DeferredItem<EncasedFanBlockItem> ENCASED_FAN = ITEMS.registerItem("fan",
            props -> new EncasedFanBlockItem(EFBlocks.ENCASED_FAN.get(), props),
            props -> props.useBlockDescriptionPrefix());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
