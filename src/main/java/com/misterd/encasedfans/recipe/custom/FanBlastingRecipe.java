package com.misterd.encasedfans.recipe.custom;

import com.misterd.encasedfans.recipe.EFRecipes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class FanBlastingRecipe extends FanProcessingRecipe {
    public FanBlastingRecipe(Ingredient ingredient, List<ProcessingOutput> results) {
        super(ingredient, results);
    }

    @Override
    public RecipeSerializer<FanBlastingRecipe> getSerializer() {
        return EFRecipes.BLASTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<FanBlastingRecipe> getType() {
        return EFRecipes.BLASTING_TYPE.get();
    }
}