package com.misterd.encasedfans.datagen.custom;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

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
