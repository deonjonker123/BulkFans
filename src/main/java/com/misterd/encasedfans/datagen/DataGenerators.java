package com.misterd.encasedfans.datagen;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.datagen.custom.*;
import com.misterd.encasedfans.processing.EFProcessingTypes;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = EncasedFans.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider((output, lookupProvider) -> new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(EFLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider));
        event.createProvider(EFBlockTagsProvider::new);
        event.createProvider(EFItemTagsProvider::new);
        event.createProvider(EFRecipeProvider.Runner::new);
        event.createProvider(EFDataMapProvider::new);
        event.createDatapackRegistryObjects(new RegistrySetBuilder().add(EFProcessingTypes.REGISTRY, EFProcessingTypes::bootstrap));
    }
}
