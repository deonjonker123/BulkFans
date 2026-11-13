package com.misterd.bulkfans.compat.jei;

import com.misterd.bulkfans.processing.ProcessingType;
import com.misterd.bulkfans.recipe.custom.ProcessingOutput;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record FanJeiRecipe(Holder<ProcessingType> type, Ingredient input, List<ProcessingOutput> outputs) {
}