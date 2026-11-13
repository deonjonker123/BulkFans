package com.misterd.encasedfans.processing;

import com.misterd.encasedfans.attachment.EFAttachments;
import com.misterd.encasedfans.config.EFConfig;
import com.misterd.encasedfans.recipe.EFRecipes;
import com.misterd.encasedfans.recipe.custom.FanProcessingRecipe;
import com.misterd.encasedfans.util.EFTags;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class FanProcessing {
    private static RecipeManager cachedRecipes;
    private static final Map<ResourceKey<ProcessingType>, List<FanProcessingRecipe>> RECIPES = new HashMap<>();
    private static final Map<ResourceKey<ProcessingType>, Map<Item, Boolean>> PROCESSABLE = new HashMap<>();
    private static final Set<Item> SMOKABLE = new HashSet<>();

    private FanProcessing() {
    }

    public static int requiredTicks(int count) {
        int batchSize = EFConfig.SERVER.batchSize.get();
        return EFConfig.SERVER.ticksPerBatch.get() * ((count + batchSize - 1) / batchSize);
    }

    public static void tick(ServerLevel level, ItemEntity entity, Holder<ProcessingType> type, int elapsed) {
        Optional<ResourceKey<ProcessingType>> key = type.unwrapKey();
        if (key.isEmpty()) {
            return;
        }
        ItemStack stack = entity.getItem();
        if (!canProcess(level, stack, type)) {
            entity.removeData(EFAttachments.PROCESSING_PROGRESS);
            return;
        }

        ProcessingProgress progress = entity.getData(EFAttachments.PROCESSING_PROGRESS);
        int ticks = progress.isFor(key.get()) ? progress.ticks() + elapsed : elapsed;
        if (ticks < requiredTicks(stack.getCount())) {
            entity.setData(EFAttachments.PROCESSING_PROGRESS, new ProcessingProgress(key, ticks));
            spawnProcessingParticles(level, entity.getX(), entity.getY() + 0.25, entity.getZ());
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

    public static boolean canProcess(ServerLevel level, ItemStack stack, Holder<ProcessingType> type) {
        Optional<ResourceKey<ProcessingType>> key = type.unwrapKey();
        if (key.isEmpty()) {
            return false;
        }
        refreshCache(level);
        return PROCESSABLE.computeIfAbsent(key.get(), k -> new HashMap<>())
                .computeIfAbsent(stack.getItem(), item -> hasResult(level, stack, key.get(), type.value()));
    }

    private static boolean hasResult(ServerLevel level, ItemStack stack, ResourceKey<ProcessingType> key, ProcessingType type) {
        return fanRecipe(level, key, stack).isPresent()
                || cookingRecipe(level, type, stack).isPresent()
                || !copperStage(stack, type).isEmpty();
    }

    public static List<ItemStack> process(ServerLevel level, ItemStack stack, Holder<ProcessingType> type) {
        Optional<FanProcessingRecipe> recipe = type.unwrapKey().flatMap(key -> fanRecipe(level, key, stack));
        if (recipe.isPresent()) {
            return recipe.get().rollResults(stack.getCount(), level.getRandom());
        }
        Optional<? extends AbstractCookingRecipe> cooking = cookingRecipe(level, type.value(), stack);
        if (cooking.isPresent()) {
            return cook(cooking.get(), stack);
        }
        ItemStack copper = copperStage(stack, type.value());
        return copper.isEmpty() ? List.of() : List.of(copper);
    }

    private static Optional<FanProcessingRecipe> fanRecipe(ServerLevel level, ResourceKey<ProcessingType> key, ItemStack stack) {
        SingleRecipeInput input = new SingleRecipeInput(stack);
        for (FanProcessingRecipe recipe : RECIPES.getOrDefault(key, List.of())) {
            if (recipe.matches(input, level)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    private static Optional<? extends AbstractCookingRecipe> cookingRecipe(ServerLevel level, ProcessingType type, ItemStack stack) {
        if (type.cookingFallback().isEmpty()) {
            return Optional.empty();
        }
        return switch (type.cookingFallback().get()) {
            case SMOKING -> vanillaRecipe(level, RecipeType.SMOKING, stack);
            case BLASTING -> blastingRecipe(level, stack);
        };
    }

    private static <T extends AbstractCookingRecipe> Optional<T> vanillaRecipe(ServerLevel level, RecipeType<T> type, ItemStack stack) {
        return level.recipeAccess().getRecipeFor(type, new SingleRecipeInput(stack), level).map(RecipeHolder::value);
    }

    private static Optional<? extends AbstractCookingRecipe> blastingRecipe(ServerLevel level, ItemStack stack) {
        if (isBlastingExcluded(stack)) {
            return Optional.empty();
        }
        Optional<? extends AbstractCookingRecipe> blasting = vanillaRecipe(level, RecipeType.BLASTING, stack);
        return blasting.isPresent() ? blasting : vanillaRecipe(level, RecipeType.SMELTING, stack);
    }

    private static boolean isBlastingExcluded(ItemStack stack) {
        if (stack.is(EFTags.Items.BLASTING_INCLUDE)) {
            return false;
        }
        return stack.is(EFTags.Items.BLASTING_EXCLUDE) || SMOKABLE.contains(stack.getItem());
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

    private static ItemStack copperStage(ItemStack stack, ProcessingType type) {
        if (type.copperTransform().isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
            return ItemStack.EMPTY;
        }
        Block next = type.copperTransform().get().apply(blockItem.getBlock());
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
        RECIPES.clear();
        PROCESSABLE.clear();
        SMOKABLE.clear();
        for (RecipeHolder<FanProcessingRecipe> holder : recipes.recipeMap().byType(EFRecipes.FAN_PROCESSING_TYPE.get())) {
            RECIPES.computeIfAbsent(holder.value().processingType(), key -> new ArrayList<>()).add(holder.value());
        }
        for (RecipeHolder<?> holder : recipes.recipeMap().byType(RecipeType.SMOKING)) {
            if (holder.value() instanceof AbstractCookingRecipe recipe) {
                recipe.input().items().forEach(item -> SMOKABLE.add(item.value()));
            }
        }
    }

    public static void spawnProcessingParticles(ServerLevel level, double x, double y, double z) {
        int particles = EFConfig.SERVER.processingParticles.get();
        if (particles > 0) {
            level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, particles, 0.1, 0.05, 0.1, 0.0);
        }
    }
}