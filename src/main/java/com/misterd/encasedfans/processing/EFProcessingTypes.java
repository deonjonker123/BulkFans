package com.misterd.encasedfans.processing;

import com.misterd.encasedfans.EncasedFans;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.List;
import java.util.Optional;

public class EFProcessingTypes {
    public static final ResourceKey<Registry<ProcessingType>> REGISTRY =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(EncasedFans.MODID, "processing_type"));

    public static final ResourceKey<ProcessingType> WASHING = key("washing");
    public static final ResourceKey<ProcessingType> SMOKING = key("smoking");
    public static final ResourceKey<ProcessingType> BLASTING = key("blasting");
    public static final ResourceKey<ProcessingType> HAUNTING = key("haunting");

    private static ResourceKey<ProcessingType> key(String name) {
        return ResourceKey.create(REGISTRY, Identifier.fromNamespaceAndPath(EncasedFans.MODID, name));
    }

    public static void registerRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, ProcessingType.CODEC, ProcessingType.CODEC);
    }

    public static void bootstrap(BootstrapContext<ProcessingType> context) {
        context.register(WASHING, new ProcessingType(ParticleTypes.SPLASH,
                Optional.empty(), Optional.of(CopperTransform.DEOXIDIZE), false, true, List.of(),
                icon(Items.WATER_BUCKET), display("water")));
        context.register(SMOKING, new ProcessingType(ParticleTypes.SMOKE,
                Optional.of(CookingFallback.SMOKING), Optional.empty(), true, false, List.of(),
                icon(Items.CAMPFIRE), display("fire")));
        context.register(BLASTING, new ProcessingType(ParticleTypes.SMALL_FLAME,
                Optional.of(CookingFallback.BLASTING), Optional.empty(), true, false, List.of(),
                icon(Items.LAVA_BUCKET), display("lava")));
        context.register(HAUNTING, new ProcessingType(ParticleTypes.SOUL_FIRE_FLAME,
                Optional.empty(), Optional.of(CopperTransform.OXIDIZE), false, false,
                List.of(new EntityEffect(MobEffects.DARKNESS, 0, Optional.empty())),
                icon(Items.SOUL_CAMPFIRE), display("soul_fire")));
    }

    private static Optional<Holder<Item>> icon(Item item) {
        return Optional.of(item.builtInRegistryHolder());
    }

    private static Optional<ItemStackTemplate> display(String model) {
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(EncasedFans.MODID, "jei/" + model))
                .build();
        return Optional.of(new ItemStackTemplate(Items.BARRIER, patch));
    }
}