package com.misterd.encasedfans.recipe.custom;

import com.misterd.encasedfans.processing.EFProcessingTypes;
import com.misterd.encasedfans.processing.ProcessingType;
import com.misterd.encasedfans.recipe.EFRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FanProcessingRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<FanProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(EFProcessingTypes.REGISTRY).fieldOf("processing_type").forGetter(FanProcessingRecipe::processingType),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(FanProcessingRecipe::ingredient),
            ExtraCodecs.nonEmptyList(ProcessingOutput.CODEC.listOf()).fieldOf("results").forGetter(FanProcessingRecipe::results)
    ).apply(instance, FanProcessingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FanProcessingRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(EFProcessingTypes.REGISTRY), FanProcessingRecipe::processingType,
            Ingredient.CONTENTS_STREAM_CODEC, FanProcessingRecipe::ingredient,
            ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), FanProcessingRecipe::results,
            FanProcessingRecipe::new);

    private final ResourceKey<ProcessingType> processingType;
    private final Ingredient ingredient;
    private final List<ProcessingOutput> results;

    public FanProcessingRecipe(ResourceKey<ProcessingType> processingType, Ingredient ingredient, List<ProcessingOutput> results) {
        this.processingType = processingType;
        this.ingredient = ingredient;
        this.results = results;
    }

    public ResourceKey<ProcessingType> processingType() {
        return processingType;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public List<ProcessingOutput> results() {
        return results;
    }

    public List<ItemStack> rollResults(int count, RandomSource random) {
        List<ItemStack> rolled = new ArrayList<>();
        for (ProcessingOutput output : results) {
            int total = 0;
            for (int i = 0; i < count; i++) {
                if (output.chance() >= 1 || random.nextFloat() < output.chance()) {
                    total += output.item().count();
                }
            }
            ItemStack template = output.item().create();
            while (total > 0) {
                int size = Math.min(total, template.getMaxStackSize());
                rolled.add(template.copyWithCount(size));
                total -= size;
            }
        }
        return rolled;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return results.getFirst().item().create();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<FanProcessingRecipe> getSerializer() {
        return EFRecipes.FAN_PROCESSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<FanProcessingRecipe> getType() {
        return EFRecipes.FAN_PROCESSING_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return EFRecipes.FAN_PROCESSING_CATEGORY.get();
    }
}