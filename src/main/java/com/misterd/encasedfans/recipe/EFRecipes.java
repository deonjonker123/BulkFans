package com.misterd.encasedfans.recipe;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.recipe.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, EncasedFans.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, EncasedFans.MODID);
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
            DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, EncasedFans.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<WashingRecipe>> WASHING_TYPE =
            RECIPE_TYPES.register("washing", id -> RecipeType.<WashingRecipe>simple(id));
    public static final DeferredHolder<RecipeType<?>, RecipeType<HauntingRecipe>> HAUNTING_TYPE =
            RECIPE_TYPES.register("haunting", id -> RecipeType.<HauntingRecipe>simple(id));
    public static final DeferredHolder<RecipeType<?>, RecipeType<FanSmokingRecipe>> SMOKING_TYPE =
            RECIPE_TYPES.register("smoking", id -> RecipeType.<FanSmokingRecipe>simple(id));
    public static final DeferredHolder<RecipeType<?>, RecipeType<FanBlastingRecipe>> BLASTING_TYPE =
            RECIPE_TYPES.register("blasting", id -> RecipeType.<FanBlastingRecipe>simple(id));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WashingRecipe>> WASHING_SERIALIZER =
            RECIPE_SERIALIZERS.register("washing", () -> FanProcessingRecipe.serializer(WashingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HauntingRecipe>> HAUNTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("haunting", () -> FanProcessingRecipe.serializer(HauntingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FanSmokingRecipe>> SMOKING_SERIALIZER =
            RECIPE_SERIALIZERS.register("smoking", () -> FanProcessingRecipe.serializer(FanSmokingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FanBlastingRecipe>> BLASTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("blasting", () -> FanProcessingRecipe.serializer(FanBlastingRecipe::new));

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> FAN_PROCESSING_CATEGORY =
            RECIPE_BOOK_CATEGORIES.register("fan_processing", RecipeBookCategory::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_BOOK_CATEGORIES.register(eventBus);
    }
}