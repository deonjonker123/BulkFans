package com.misterd.encasedfans.processing;

import com.misterd.encasedfans.attachment.EFAttachments;
import com.misterd.encasedfans.recipe.EFRecipes;
import com.misterd.encasedfans.recipe.custom.FanProcessingRecipe;
import com.misterd.encasedfans.util.EFTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.DataMapHooks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class FanProcessing {
    private static final int TICKS_PER_BATCH = 100;
    private static final int BATCH_SIZE = 16;

    private static RecipeManager cachedRecipes;
    private static final Map<FanProcessingType, Map<Item, Boolean>> PROCESSABLE = new EnumMap<>(FanProcessingType.class);
    private static final Set<Item> SMOKABLE = new HashSet<>();

    private FanProcessing() {
    }

    public static int requiredTicks(int count) {
        return TICKS_PER_BATCH * ((count + BATCH_SIZE - 1) / BATCH_SIZE);
    }

    public static void tick(ServerLevel level, ItemEntity entity, FanProcessingType type, int elapsed) {
        if (type == FanProcessingType.NONE) {
            return;
        }
        ItemStack stack = entity.getItem();
        if (!canProcess(level, stack, type)) {
            entity.removeData(EFAttachments.PROCESSING_PROGRESS);
            return;
        }

        ProcessingProgress progress = entity.getData(EFAttachments.PROCESSING_PROGRESS);
        int ticks = progress.type() == type ? progress.ticks() + elapsed : elapsed;
        if (ticks < requiredTicks(stack.getCount())) {
            entity.setData(EFAttachments.PROCESSING_PROGRESS, new ProcessingProgress(type, ticks));
            level.sendParticles(ParticleTypes.LARGE_SMOKE, entity.getX(), entity.getY() + 0.25, entity.getZ(), 3, 0.1, 0.05, 0.1, 0.0);
            return;
        }

        entity.removeData(EFAttachments.PROCESSING_PROGRESS);
        List<ItemStack> results = process(level, stack, type);
        if (results.isEmpty()) {
            entity.discard();
            return;
        }
        entity.setItem(results.getFirst());
        for (int i = 1; i < results.size(); i++) {
            ItemEntity extra = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), results.get(i));
            extra.setDeltaMovement(entity.getDeltaMovement());
            level.addFreshEntity(extra);
        }
    }

    public static boolean canProcess(ServerLevel level, ItemStack stack, FanProcessingType type) {
        refreshCache(level);
        return PROCESSABLE.computeIfAbsent(type, key -> new HashMap<>())
                .computeIfAbsent(stack.getItem(), item -> hasResult(level, stack, type));
    }

    private static boolean hasResult(ServerLevel level, ItemStack stack, FanProcessingType type) {
        return switch (type) {
            case NONE -> false;
            case SMOKING -> fanRecipe(level, EFRecipes.SMOKING_TYPE.get(), stack).isPresent() || cookingRecipe(level, RecipeType.SMOKING, stack).isPresent();
            case BLASTING -> fanRecipe(level, EFRecipes.BLASTING_TYPE.get(), stack).isPresent() || blastingRecipe(level, stack).isPresent();
            case WASHING -> fanRecipe(level, EFRecipes.WASHING_TYPE.get(), stack).isPresent() || !copperStage(stack, false).isEmpty();
            case HAUNTING -> fanRecipe(level, EFRecipes.HAUNTING_TYPE.get(), stack).isPresent() || !copperStage(stack, true).isEmpty();
        };
    }

    private static List<ItemStack> process(ServerLevel level, ItemStack stack, FanProcessingType type) {
        return switch (type) {
            case NONE -> List.of();
            case SMOKING -> fanRecipe(level, EFRecipes.SMOKING_TYPE.get(), stack)
                    .map(recipe -> recipe.rollResults(stack.getCount(), level.getRandom()))
                    .orElseGet(() -> cookingRecipe(level, RecipeType.SMOKING, stack).map(recipe -> cook(recipe, stack)).orElse(List.of()));
            case BLASTING -> fanRecipe(level, EFRecipes.BLASTING_TYPE.get(), stack)
                    .map(recipe -> recipe.rollResults(stack.getCount(), level.getRandom()))
                    .orElseGet(() -> blastingRecipe(level, stack).map(recipe -> cook(recipe, stack)).orElse(List.of()));
            case WASHING -> fanRecipe(level, EFRecipes.WASHING_TYPE.get(), stack)
                    .map(recipe -> recipe.rollResults(stack.getCount(), level.getRandom()))
                    .orElseGet(() -> List.of(copperStage(stack, false)));
            case HAUNTING -> fanRecipe(level, EFRecipes.HAUNTING_TYPE.get(), stack)
                    .map(recipe -> recipe.rollResults(stack.getCount(), level.getRandom()))
                    .orElseGet(() -> List.of(copperStage(stack, true)));
        };
    }

    private static <T extends AbstractCookingRecipe> Optional<T> cookingRecipe(ServerLevel level, RecipeType<T> type, ItemStack stack) {
        return level.recipeAccess().getRecipeFor(type, new SingleRecipeInput(stack), level).map(RecipeHolder::value);
    }

    private static Optional<? extends AbstractCookingRecipe> blastingRecipe(ServerLevel level, ItemStack stack) {
        if (isBlastingExcluded(stack)) {
            return Optional.empty();
        }
        Optional<? extends AbstractCookingRecipe> blasting = cookingRecipe(level, RecipeType.BLASTING, stack);
        return blasting.isPresent() ? blasting : cookingRecipe(level, RecipeType.SMELTING, stack);
    }

    private static boolean isBlastingExcluded(ItemStack stack) {
        if (stack.is(EFTags.Items.BLASTING_INCLUDE)) {
            return false;
        }
        return stack.is(EFTags.Items.BLASTING_EXCLUDE) || SMOKABLE.contains(stack.getItem());
    }

    private static <T extends FanProcessingRecipe> Optional<T> fanRecipe(ServerLevel level, RecipeType<T> type, ItemStack stack) {
        return level.recipeAccess().getRecipeFor(type, new SingleRecipeInput(stack), level).map(RecipeHolder::value);
    }

    private static List<ItemStack> cook(AbstractCookingRecipe recipe, ItemStack stack) {
        ItemStack result = recipe.assemble(new SingleRecipeInput(stack.copyWithCount(1)));
        int total = result.getCount() * stack.getCount();
        List<ItemStack> results = new ArrayList<>();
        while (total > 0) {
            int size = Math.min(total, result.getMaxStackSize());
            results.add(result.copyWithCount(size));
            total -= size;
        }
        return results;
    }

    private static ItemStack copperStage(ItemStack stack, boolean oxidize) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return ItemStack.EMPTY;
        }
        Block block = blockItem.getBlock();
        Block next = oxidize ? DataMapHooks.getNextOxidizedStage(block) : DataMapHooks.getPreviousOxidizedStage(block);
        if (next == null || next.asItem() == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return stack.transmuteCopy(next, stack.getCount());
    }

    private static void refreshCache(ServerLevel level) {
        RecipeManager recipes = level.recipeAccess();
        if (recipes == cachedRecipes) {
            return;
        }
        cachedRecipes = recipes;
        PROCESSABLE.clear();
        SMOKABLE.clear();
        for (RecipeHolder<?> holder : recipes.recipeMap().byType(RecipeType.SMOKING)) {
            if (holder.value() instanceof AbstractCookingRecipe recipe) {
                recipe.input().items().forEach(item -> SMOKABLE.add(item.value()));
            }
        }
    }
}