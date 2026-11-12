package com.misterd.encasedfans.datagen.custom;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.recipe.custom.HauntingRecipe;
import com.misterd.encasedfans.recipe.custom.ProcessingOutput;
import com.misterd.encasedfans.recipe.custom.WashingRecipe;
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
                .define('I', Items.IRON_INGOT)
                .define('B', Items.IRON_BARS)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(output);

        washing("gravel", Items.GRAVEL, output(Items.FLINT, 1F), output(Items.IRON_NUGGET, 0.1F));
        washing("sand", Items.SAND, output(Items.CLAY_BALL, 1F), output(Items.GOLD_NUGGET, 0.1F));
        washing("soul_sand", Items.SOUL_SAND, output(Items.SAND, 1F), output(Items.QUARTZ, 0.1F));

        haunting("sand", Items.SAND, output(Items.SOUL_SAND, 1F));
        haunting("potato", Items.POTATO, output(Items.POISONOUS_POTATO, 1F));
        haunting("torch", Items.TORCH, output(Items.SOUL_TORCH, 1F));
        haunting("lantern", Items.LANTERN, output(Items.SOUL_LANTERN, 1F));
        haunting("campfire", Items.CAMPFIRE, output(Items.SOUL_CAMPFIRE, 1F));
    }

    private void washing(String name, ItemLike input, ProcessingOutput... results) {
        output.accept(recipeKey("washing/" + name), new WashingRecipe(Ingredient.of(input), List.of(results)), null);
    }

    private void haunting(String name, ItemLike input, ProcessingOutput... results) {
        output.accept(recipeKey("haunting/" + name), new HauntingRecipe(Ingredient.of(input), List.of(results)), null);
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