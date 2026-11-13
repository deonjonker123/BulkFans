package com.misterd.encasedfans.datagen.custom;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.processing.EFProcessingTypes;
import com.misterd.encasedfans.processing.ProcessingType;
import com.misterd.encasedfans.recipe.custom.FanProcessingRecipe;
import com.misterd.encasedfans.recipe.custom.ProcessingOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EFRecipeProvider extends RecipeProvider {
    protected EFRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.REDSTONE, EFBlocks.ENCASED_FAN.get())
                .pattern("IBI")
                .pattern("BRB")
                .pattern("IBI")
                .define('I', Items.COPPER_BLOCK)
                .define('B', Items.IRON_BARS)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(output);

        fan(EFProcessingTypes.WASHING, "gravel", Items.GRAVEL, output(Items.FLINT, 1F), output(Items.IRON_NUGGET, 0.1F));
        fan(EFProcessingTypes.WASHING, "sand", Items.SAND, output(Items.CLAY_BALL, 1F), output(Items.GOLD_NUGGET, 0.1F));
        fan(EFProcessingTypes.WASHING, "red_sand", Items.RED_SAND, output(Items.CLAY_BALL, 1F), output(Items.GOLD_NUGGET, 0.1F));
        fan(EFProcessingTypes.WASHING, "soul_sand", Items.SOUL_SAND, output(Items.SAND, 1F), output(Items.QUARTZ, 0.05F));
        fan(EFProcessingTypes.WASHING, "soul_soil", Items.SOUL_SOIL, output(Items.RED_SAND, 1F));

        fan(EFProcessingTypes.HAUNTING, "sand", Items.SAND, output(Items.SOUL_SAND, 1F));
        fan(EFProcessingTypes.HAUNTING, "red_sand", Items.RED_SAND, output(Items.SOUL_SOIL, 1F));
        fan(EFProcessingTypes.HAUNTING, "potato", Items.POTATO, output(Items.POISONOUS_POTATO, 1F));
        fan(EFProcessingTypes.HAUNTING, "torch", Items.TORCH, output(Items.SOUL_TORCH, 1F));
        fan(EFProcessingTypes.HAUNTING, "lantern", Items.LANTERN, output(Items.SOUL_LANTERN, 1F));
        fan(EFProcessingTypes.HAUNTING, "campfire", Items.CAMPFIRE, output(Items.SOUL_CAMPFIRE, 1F));
    }

    private void fan(ResourceKey<ProcessingType> type, String name, ItemLike input, ProcessingOutput... results) {
        String path = type.identifier().getPath() + "/" + name;
        output.accept(recipeKey(path), new FanProcessingRecipe(type, Ingredient.of(input), List.of(results)), null);
    }

    private static ProcessingOutput output(ItemLike item, float chance) {
        return new ProcessingOutput(new ItemStackTemplate(item.asItem()), chance);
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(EncasedFans.MODID, path));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new EFRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return EncasedFans.MODID + " Recipes";
        }
    }
}