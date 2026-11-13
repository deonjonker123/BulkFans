package com.misterd.bulkfans.compat.jei;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.item.BFItems;
import com.misterd.bulkfans.processing.ProcessingType;
import com.misterd.bulkfans.recipe.custom.ProcessingOutput;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;

public class FanProcessingCategory extends AbstractRecipeCategory<FanJeiRecipe> {
    public static final int WIDTH = 178;
    public static final int HEIGHT = 36;
    private static final int MAX_OUTPUTS = 6;
    private static final int COLUMNS = 3;
    private static final int SLOT_SIZE = 18;
    private static final int SPRITE_SIZE = 16;
    private static final int EDGE = 1;
    private static final int ARROW_GAP = 6;
    private static final int SLOT_ROW_Y = 9;
    private static final int FAN_X = EDGE;
    private static final int CATALYST_X = FAN_X + SLOT_SIZE;
    private static final int INPUT_X = CATALYST_X + SLOT_SIZE + 4;

    private final List<ItemStack> catalystBlocks;
    private final List<Fluid> catalystFluids;

    public FanProcessingCategory(IRecipeType<FanJeiRecipe> recipeType, ResourceKey<ProcessingType> key, ItemStack decorator, List<ItemStack> catalystBlocks, List<Fluid> catalystFluids) {
        super(recipeType, title(key), new FanIconDrawable(new ItemStack(BFItems.INDUSTRIAL_FAN.get()), decorator), WIDTH, HEIGHT);
        this.catalystBlocks = catalystBlocks;
        this.catalystFluids = catalystFluids;
    }

    public static Component title(ResourceKey<ProcessingType> key) {
        Identifier id = key.identifier();
        return Component.translatableWithFallback(BulkFans.MODID + ".processing_type." + id.getNamespace() + "." + id.getPath(), id.toString());
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
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, FAN_X, SLOT_ROW_Y).setStandardSlotBackground()
                .add(new ItemStack(BFItems.INDUSTRIAL_FAN.get()));

        IRecipeSlotBuilder catalyst = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, CATALYST_X, SLOT_ROW_Y)
                .setStandardSlotBackground()
                .addItemStacks(catalystBlocks);
        if (!catalystFluids.isEmpty()) {
            catalyst.setFluidRenderer(FluidType.BUCKET_VOLUME, false, SPRITE_SIZE, SPRITE_SIZE);
            for (Fluid fluid : catalystFluids) {
                catalyst.add(fluid, FluidType.BUCKET_VOLUME);
            }
        }

        builder.addInputSlot(INPUT_X, SLOT_ROW_Y).setStandardSlotBackground().add(recipe.input());

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
                        Component.translatable("bulkfans.jei.chance", percent).withStyle(ChatFormatting.GOLD)));
            }
        }
    }

    @Override
    public void draw(FanJeiRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        int arrowStart = INPUT_X + SLOT_SIZE + ARROW_GAP;
        ThinArrowDrawable arrow = new ThinArrowDrawable(outputsLeft(recipe) - ARROW_GAP - arrowStart);
        arrow.draw(graphics, arrowStart, SLOT_ROW_Y + (SLOT_SIZE - arrow.getHeight()) / 2);
    }
}