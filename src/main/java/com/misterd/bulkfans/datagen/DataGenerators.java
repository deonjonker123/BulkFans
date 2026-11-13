package com.misterd.bulkfans.datagen;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.datagen.custom.*;
import com.misterd.bulkfans.processing.BFProcessingTypes;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = BulkFans.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider((output, lookupProvider) -> new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(BFLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider));
        event.createProvider(BFBlockTagsProvider::new);
        event.createProvider(BFItemTagsProvider::new);
        event.createProvider(BFRecipeProvider.Runner::new);
        event.createProvider(BFDataMapProvider::new);
        event.createProvider(BFModelProvider::new);
        event.createDatapackRegistryObjects(new RegistrySetBuilder().add(BFProcessingTypes.REGISTRY, BFProcessingTypes::bootstrap));
    }
}
