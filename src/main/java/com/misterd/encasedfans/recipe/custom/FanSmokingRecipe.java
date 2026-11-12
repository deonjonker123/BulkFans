package com.misterd.encasedfans.recipe.custom;

import com.misterd.encasedfans.recipe.EFRecipes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class FanSmokingRecipe extends FanProcessingRecipe {
    public FanSmokingRecipe(Ingredient ingredient, List<ProcessingOutput> results) {
        super(ingredient, results);
    }

    @Override
    public RecipeSerializer<FanSmokingRecipe> getSerializer() {
        return EFRecipes.SMOKING_SERIALIZER.get();
    }

    @Override
    public RecipeType<FanSmokingRecipe> getType() {
        return EFRecipes.SMOKING_TYPE.get();
    }
}