package com.misterd.bulkfans.processing;

import com.misterd.bulkfans.BulkFans;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Optional;

public class BFProcessingTypes {
    public static final ResourceKey<Registry<ProcessingType>> REGISTRY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(BulkFans.MODID, "processing_type"));

    public static final ResourceKey<ProcessingType> WASHING = key("washing");
    public static final ResourceKey<ProcessingType> SMOKING = key("smoking");
    public static final ResourceKey<ProcessingType> BLASTING = key("blasting");
    public static final ResourceKey<ProcessingType> HAUNTING = key("haunting");

    private static ResourceKey<ProcessingType> key(String name) {
        return ResourceKey.create(REGISTRY, Identifier.fromNamespaceAndPath(BulkFans.MODID, name));
    }

    public static void registerRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, ProcessingType.CODEC, ProcessingType.CODEC);
    }

    public static void bootstrap(BootstrapContext<ProcessingType> context) {
        context.register(WASHING, new ProcessingType(ParticleTypes.SPLASH,
                Optional.empty(), Optional.of(CopperTransform.DEOXIDIZE), false, true, List.of(),
                icon(Items.WATER_BUCKET)));
        context.register(SMOKING, new ProcessingType(ParticleTypes.SMOKE,
                Optional.of(CookingFallback.SMOKING), Optional.empty(), true, false, List.of(),
                icon(Items.CAMPFIRE)));
        context.register(BLASTING, new ProcessingType(ParticleTypes.SMALL_FLAME,
                Optional.of(CookingFallback.BLASTING), Optional.empty(), true, false, List.of(),
                icon(Items.LAVA_BUCKET)));
        context.register(HAUNTING, new ProcessingType(ParticleTypes.SOUL_FIRE_FLAME,
                Optional.empty(), Optional.of(CopperTransform.OXIDIZE), false, false,
                List.of(new EntityEffect(MobEffects.DARKNESS, 0, Optional.empty())),
                icon(Items.SOUL_CAMPFIRE)));
    }

    private static Optional<Holder<Item>> icon(Item item) {
        return Optional.of(item.builtInRegistryHolder());
    }
}