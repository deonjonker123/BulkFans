package com.misterd.bulkfans.item;

import com.misterd.bulkfans.BulkFans;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BFCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BulkFans.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BULK_FANS_TAB =
            CREATIVE_TABS.register("bulkfans", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bulkfans"))
                    .icon(() -> new ItemStack(BFItems.INDUSTRIAL_FAN.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(BFItems.INDUSTRIAL_FAN.get());
                        output.accept(BFItems.TRAY.get());
                        output.accept(BFItems.FIRE_CATALYST.get());
                        output.accept(BFItems.SOUL_FIRE_CATALYST.get());
                        output.accept(BFItems.LAVA_CATALYST.get());
                        output.accept(BFItems.WATER_CATALYST.get());
                        output.accept(BFItems.SPANNER.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}