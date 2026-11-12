package com.misterd.encasedfans;

import com.misterd.encasedfans.attachment.EFAttachments;
import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.blockentity.EFBlockEntities;
import com.misterd.encasedfans.item.EFCreativeTab;
import com.misterd.encasedfans.item.EFItems;
import com.misterd.encasedfans.recipe.EFRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(EncasedFans.MODID)
public class EncasedFans {
    public static final String MODID = "encasedfans";

    public EncasedFans(IEventBus modEventBus, ModContainer modContainer) {
        EFBlocks.register(modEventBus);
        EFItems.register(modEventBus);
        EFBlockEntities.register(modEventBus);
        EFCreativeTab.register(modEventBus);
        EFRecipes.register(modEventBus);
        EFAttachments.register(modEventBus);
    }
}