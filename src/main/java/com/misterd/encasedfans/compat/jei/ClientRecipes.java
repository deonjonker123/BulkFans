package com.misterd.encasedfans.compat.jei;

import com.misterd.encasedfans.EncasedFans;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

@EventBusSubscriber(modid = EncasedFans.MODID, value = Dist.CLIENT)
public class ClientRecipes {
    private static RecipeMap recipes = RecipeMap.EMPTY;

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        recipes = event.getRecipeMap();
    }

    public static RecipeMap get() {
        return recipes;
    }
}