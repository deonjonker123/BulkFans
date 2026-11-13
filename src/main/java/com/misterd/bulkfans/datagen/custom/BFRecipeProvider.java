package com.misterd.bulkfans.datagen.custom;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.block.BFBlocks;
import com.misterd.bulkfans.item.BFItems;
import com.misterd.bulkfans.processing.BFProcessingTypes;
import com.misterd.bulkfans.processing.ProcessingType;
import com.misterd.bulkfans.recipe.custom.FanProcessingRecipe;
import com.misterd.bulkfans.recipe.custom.ProcessingOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BFRecipeProvider extends RecipeProvider {
    protected BFRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.REDSTONE, BFBlocks.INDUSTRIAL_FAN.get())
                .pattern("IBI")
                .pattern("BRB")
                .pattern("IBI")
                .define('I', Items.COPPER_BLOCK)
                .define('B', Items.IRON_BARS)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(output);

        shaped(RecipeCategory.TOOLS, BFItems.SPANNER.get())
                .pattern("I I")
                .pattern(" I ")
                .pattern(" I ")
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, BFBlocks.TRAY.get())
                .pattern("I I")
                .pattern("SSS")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.SMOOTH_STONE_SLAB)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, BFBlocks.SOUL_FIRE_CATALYST.get())
                .pattern("BBB")
                .pattern("BFB")
                .pattern("BSB")
                .define('B', Items.IRON_BARS)
                .define('F', Items.FLINT_AND_STEEL)
                .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
                .unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS))
                .save(output);

        catalyst(BFBlocks.FIRE_CATALYST.get(), Items.FLINT_AND_STEEL);
        catalyst(BFBlocks.LAVA_CATALYST.get(), Items.LAVA_BUCKET);
        catalyst(BFBlocks.WATER_CATALYST.get(), Items.WATER_BUCKET);

        fan(BFProcessingTypes.WASHING, "gravel", Items.GRAVEL, output(Items.FLINT, 0.5F), output(Items.IRON_NUGGET, 0.1F));
        fan(BFProcessingTypes.WASHING, "sand", Items.SAND, output(Items.CLAY_BALL, 1F));
        fan(BFProcessingTypes.WASHING, "red_sand", Items.RED_SAND, output(Items.GOLD_NUGGET, 0.25F), output(Items.DEAD_BUSH, 0.1F));
        fan(BFProcessingTypes.WASHING, "soul_sand", Items.SOUL_SAND, output(Items.QUARTZ, 0.25F), output(Items.GOLD_NUGGET, 0.05F));
        fan(BFProcessingTypes.WASHING, "magma_block", Items.MAGMA_BLOCK, output(Items.OBSIDIAN, 1F));
        fan(BFProcessingTypes.WASHING, "ice", Items.ICE, output(Items.PACKED_ICE, 1F));
        Stream<Item> dyedWools = Arrays.stream(DyeColor.values())
                .filter(color -> color != DyeColor.WHITE)
                .map(color -> vanillaItem(color, "_wool"));
        fan(BFProcessingTypes.WASHING, "dyed_wool", Ingredient.of(dyedWools), output(Items.WHITE_WOOL, 1F));
        for (DyeColor color : DyeColor.values()) {
            fan(BFProcessingTypes.WASHING, color.getSerializedName() + "_concrete_powder",
                    vanillaItem(color, "_concrete_powder"), output(vanillaItem(color, "_concrete"), 1F));
        }
        Stream<Item> stainedGlass = Arrays.stream(DyeColor.values())
                .map(color -> vanillaItem(color, "_stained_glass"));
        fan(BFProcessingTypes.WASHING, "stained_glass", Ingredient.of(stainedGlass), output(Items.GLASS, 1F));

        Stream<Item> stainedGlassPanes = Arrays.stream(DyeColor.values())
                .map(color -> vanillaItem(color, "_stained_glass_pane"));
        fan(BFProcessingTypes.WASHING, "stained_glass_pane", Ingredient.of(stainedGlassPanes), output(Items.GLASS_PANE, 1F));

        fan(BFProcessingTypes.HAUNTING, "sand", Items.SAND, output(Items.SOUL_SAND, 1F));
        fan(BFProcessingTypes.HAUNTING, "red_sand", Items.RED_SAND, output(Items.SOUL_SOIL, 1F));
        fan(BFProcessingTypes.HAUNTING, "potato", Items.POTATO, output(Items.POISONOUS_POTATO, 1F));
        fan(BFProcessingTypes.HAUNTING, "torch", Items.TORCH, output(Items.SOUL_TORCH, 1F));
        fan(BFProcessingTypes.HAUNTING, "lantern", Items.LANTERN, output(Items.SOUL_LANTERN, 1F));
        fan(BFProcessingTypes.HAUNTING, "campfire", Items.CAMPFIRE, output(Items.SOUL_CAMPFIRE, 1F));
        fan(BFProcessingTypes.HAUNTING, "brown_mushroom", Items.BROWN_MUSHROOM, output(Items.WARPED_FUNGUS, 1F));
        fan(BFProcessingTypes.HAUNTING, "red_mushroom", Items.RED_MUSHROOM, output(Items.CRIMSON_FUNGUS, 1F));
        fan(BFProcessingTypes.HAUNTING, "lapis_lazuli", Items.LAPIS_LAZULI, output(Items.PRISMARINE_SHARD, 0.75F), output(Items.PRISMARINE_CRYSTALS, 0.1F));
        fan(BFProcessingTypes.HAUNTING, "cobblestones", Tags.Items.COBBLESTONES, output(Items.BLACKSTONE, 1F));
        fan(BFProcessingTypes.HAUNTING, "sweet_berries", Items.SWEET_BERRIES, output(Items.GLOW_BERRIES, 1F));
        fan(BFProcessingTypes.HAUNTING, "ink_sac", Items.INK_SAC, output(Items.GLOW_INK_SAC, 1F));
        fan(BFProcessingTypes.HAUNTING, "brick", Items.BRICK, output(Items.NETHER_BRICK, 1F));

        fan(BFProcessingTypes.BLASTING, "raw_iron_block", Items.RAW_IRON_BLOCK, output(Items.IRON_BLOCK, 1F));
        fan(BFProcessingTypes.BLASTING, "raw_copper_block", Items.RAW_COPPER_BLOCK, output(Items.COPPER_BLOCK, 1F));
        fan(BFProcessingTypes.BLASTING, "raw_gold_block", Items.RAW_GOLD_BLOCK, output(Items.GOLD_BLOCK, 1F));
    }

    private void fan(ResourceKey<ProcessingType> type, String name, ItemLike input, ProcessingOutput... results) {
        fan(type, name, Ingredient.of(input), results);
    }

    private void fan(ResourceKey<ProcessingType> type, String name, TagKey<Item> input, ProcessingOutput... results) {
        fan(type, name, tag(input), results);
    }

    private void fan(ResourceKey<ProcessingType> type, String name, Ingredient input, ProcessingOutput... results) {
        String path = type.identifier().getPath() + "/" + name;
        output.accept(recipeKey(path), new FanProcessingRecipe(type, input, List.of(results)), null);
    }

    private static Item vanillaItem(DyeColor color, String suffix) {
        return BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color.getSerializedName() + suffix));
    }

    private static ProcessingOutput output(ItemLike item, float chance) {
        return new ProcessingOutput(new ItemStackTemplate(item.asItem()), chance);
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(BulkFans.MODID, path));
    }

    private void catalyst(ItemLike result, ItemLike source) {
        shaped(RecipeCategory.MISC, result)
                .pattern("BBB")
                .pattern("BXB")
                .pattern("BBB")
                .define('B', Items.IRON_BARS)
                .define('X', source)
                .unlockedBy(getHasName(Items.IRON_BARS), has(Items.IRON_BARS))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new BFRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return BulkFans.MODID + " Recipes";
        }
    }
}