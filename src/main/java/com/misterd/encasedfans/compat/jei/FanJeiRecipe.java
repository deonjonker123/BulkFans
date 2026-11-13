package com.misterd.encasedfans.compat.jei;

import com.misterd.encasedfans.processing.ProcessingType;
import com.misterd.encasedfans.recipe.custom.ProcessingOutput;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record FanJeiRecipe(Holder<ProcessingType> type, Ingredient input, List<ProcessingOutput> outputs) {
}