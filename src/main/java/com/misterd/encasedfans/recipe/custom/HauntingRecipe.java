package com.misterd.encasedfans.recipe.custom;

import com.misterd.encasedfans.recipe.EFRecipes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class HauntingRecipe extends FanProcessingRecipe {
    public HauntingRecipe(Ingredient ingredient, List<ProcessingOutput> results) {
        super(ingredient, results);
    }

    @Override
    public RecipeSerializer<HauntingRecipe> getSerializer() {
        return EFRecipes.HAUNTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<HauntingRecipe> getType() {
        return EFRecipes.HAUNTING_TYPE.get();
    }
}