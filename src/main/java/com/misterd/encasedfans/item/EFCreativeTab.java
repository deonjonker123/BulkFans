package com.misterd.encasedfans.item;

import com.misterd.encasedfans.EncasedFans;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EncasedFans.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENCASED_FANS_TAB =
            CREATIVE_TABS.register("encasedfans", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.encasedfans"))
                    .icon(() -> new ItemStack(EFItems.ENCASED_FAN.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(EFItems.ENCASED_FAN.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
