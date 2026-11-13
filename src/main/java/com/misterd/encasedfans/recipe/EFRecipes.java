package com.misterd.encasedfans.recipe;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.recipe.custom.FanProcessingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, EncasedFans.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, EncasedFans.MODID);
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
            DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, EncasedFans.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FanProcessingRecipe>> FAN_PROCESSING_TYPE =
            RECIPE_TYPES.register("fan_processing", id -> RecipeType.<FanProcessingRecipe>simple(id));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FanProcessingRecipe>> FAN_PROCESSING_SERIALIZER =
            RECIPE_SERIALIZERS.register("fan_processing", () -> new RecipeSerializer<>(FanProcessingRecipe.CODEC, FanProcessingRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> FAN_PROCESSING_CATEGORY =
            RECIPE_BOOK_CATEGORIES.register("fan_processing", RecipeBookCategory::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_BOOK_CATEGORIES.register(eventBus);
        NeoForge.EVENT_BUS.addListener(EFRecipes::syncRecipes);
    }

    private static void syncRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(FAN_PROCESSING_TYPE.get(), RecipeType.SMOKING, RecipeType.BLASTING, RecipeType.SMELTING);
    }
}