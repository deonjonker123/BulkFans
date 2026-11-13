package com.misterd.encasedfans.compat.jei;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.item.EFItems;
import com.misterd.encasedfans.processing.ProcessingType;
import com.misterd.encasedfans.recipe.custom.ProcessingOutput;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FanProcessingCategory extends AbstractRecipeCategory<FanJeiRecipe> {
    public static final int WIDTH = 178;
    public static final int HEIGHT = 86;
    private static final int MAX_OUTPUTS = 6;
    private static final int COLUMNS = 3;
    private static final int SLOT_SIZE = 18;
    private static final int EDGE = 4;
    private static final int ARROW_GAP = 8;
    private static final int SLOT_ROW_Y = 56;

    private final FanSceneDrawable scene;

    public FanProcessingCategory(IRecipeType<FanJeiRecipe> recipeType, ResourceKey<ProcessingType> key, ItemStack decorator, ItemStack catalyst) {
        super(recipeType, title(key), new FanIconDrawable(new ItemStack(EFItems.ENCASED_FAN.get()), decorator), WIDTH, HEIGHT);
        this.scene = new FanSceneDrawable(new ItemStack(EFItems.ENCASED_FAN.get()), catalyst, 2.0F);
    }

    public static Component title(ResourceKey<ProcessingType> key) {
        Identifier id = key.identifier();
        return Component.translatableWithFallback(EncasedFans.MODID + ".processing_type." + id.getNamespace() + "." + id.getPath(), id.toString());
    }

    private static List<ProcessingOutput> shownOutputs(FanJeiRecipe recipe) {
        return recipe.outputs().subList(0, Math.min(MAX_OUTPUTS, recipe.outputs().size()));
    }

    private static int columns(FanJeiRecipe recipe) {
        return Math.min(COLUMNS, shownOutputs(recipe).size());
    }

    private static int outputsLeft(FanJeiRecipe recipe) {
        return WIDTH - EDGE - columns(recipe) * SLOT_SIZE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FanJeiRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(EDGE, SLOT_ROW_Y).setStandardSlotBackground().add(recipe.input());

        List<ProcessingOutput> outputs = shownOutputs(recipe);
        int columns = columns(recipe);
        int rows = (outputs.size() + COLUMNS - 1) / COLUMNS;
        int left = outputsLeft(recipe);
        int top = SLOT_ROW_Y - (rows - 1) * SLOT_SIZE / 2;
        for (int i = 0; i < outputs.size(); i++) {
            ProcessingOutput output = outputs.get(i);
            int x = left + (i % columns) * SLOT_SIZE;
            int y = top + (i / columns) * SLOT_SIZE;
            IRecipeSlotBuilder slot = builder.addOutputSlot(x, y).setStandardSlotBackground().add(output.item());
            if (output.chance() < 1) {
                int percent = Math.round(output.chance() * 100);
                slot.addRichTooltipCallback((view, tooltip) -> tooltip.add(
                        Component.translatable("encasedfans.jei.chance", percent).withStyle(ChatFormatting.GOLD)));
            }
        }
    }

    @Override
    public void draw(FanJeiRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        scene.draw(graphics, (WIDTH - scene.getWidth()) / 2, 4);
        int arrowStart = EDGE + SLOT_SIZE + ARROW_GAP;
        ThinArrowDrawable arrow = new ThinArrowDrawable(outputsLeft(recipe) - ARROW_GAP - arrowStart);
        arrow.draw(graphics, arrowStart, SLOT_ROW_Y + (SLOT_SIZE - arrow.getHeight()) / 2);
    }
}