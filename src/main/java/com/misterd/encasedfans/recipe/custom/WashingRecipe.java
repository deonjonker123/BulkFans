package com.misterd.encasedfans.recipe.custom;

import com.misterd.encasedfans.recipe.EFRecipes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class WashingRecipe extends FanProcessingRecipe {
    public WashingRecipe(Ingredient ingredient, List<ProcessingOutput> results) {
        super(ingredient, results);
    }

    @Override
    public RecipeSerializer<WashingRecipe> getSerializer() {
        return EFRecipes.WASHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<WashingRecipe> getType() {
        return EFRecipes.WASHING_TYPE.get();
    }
}