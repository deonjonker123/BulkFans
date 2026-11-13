package com.misterd.encasedfans.compat.jei;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.datamap.BlockCatalyst;
import com.misterd.encasedfans.datamap.EFDataMaps;
import com.misterd.encasedfans.datamap.FluidCatalyst;
import com.misterd.encasedfans.item.EFItems;
import com.misterd.encasedfans.processing.CookingFallback;
import com.misterd.encasedfans.processing.EFProcessingTypes;
import com.misterd.encasedfans.processing.ProcessingType;
import com.misterd.encasedfans.recipe.EFRecipes;
import com.misterd.encasedfans.recipe.custom.FanProcessingRecipe;
import com.misterd.encasedfans.recipe.custom.ProcessingOutput;
import com.misterd.encasedfans.util.EFTags;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@JeiPlugin
public class EFJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(EncasedFans.MODID, "jei_plugin");

    private final Map<ResourceKey<ProcessingType>, IRecipeType<FanJeiRecipe>> recipeTypes = new LinkedHashMap<>();

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    private static Optional<Registry<ProcessingType>> processingTypes() {
        if (Minecraft.getInstance().getConnection() == null) {
            return Optional.empty();
        }
        RegistryAccess access = Minecraft.getInstance().getConnection().registryAccess();
        return access.lookup(EFProcessingTypes.REGISTRY);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        recipeTypes.clear();
        processingTypes().ifPresent(registry -> registry.listElements().forEach(holder -> {
            ResourceKey<ProcessingType> key = holder.key();
            Identifier id = key.identifier();
            IRecipeType<FanJeiRecipe> type = IRecipeType.create(
                    Identifier.fromNamespaceAndPath(EncasedFans.MODID, id.getNamespace() + "_" + id.getPath()), FanJeiRecipe.class);
            recipeTypes.put(key, type);
            ItemStack fallback = displayCatalyst(key);
            ItemStack decorator = holder.value().icon().map(ItemStack::new).orElse(fallback);
            ItemStack scene = holder.value().display().map(ItemStackTemplate::create).orElse(fallback);
            registration.addRecipeCategories(new FanProcessingCategory(type, key, decorator, scene));
        }));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (Map.Entry<ResourceKey<ProcessingType>, IRecipeType<FanJeiRecipe>> entry : recipeTypes.entrySet()) {
            registration.addCraftingStation(entry.getValue(), EFItems.ENCASED_FAN.get(), EFItems.TRAY.get());
            for (ItemStack catalyst : catalystItems(entry.getKey())) {
                registration.addCraftingStation(entry.getValue(), catalyst);
            }
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeMap recipes = ClientRecipes.get();
        Set<Item> smokable = new HashSet<>();
        for (RecipeHolder<?> holder : recipes.byType(RecipeType.SMOKING)) {
            if (holder.value() instanceof AbstractCookingRecipe recipe) {
                recipe.input().items().forEach(item -> smokable.add(item.value()));
            }
        }

        processingTypes().ifPresent(registry -> registry.listElements().forEach(holder -> {
            IRecipeType<FanJeiRecipe> type = recipeTypes.get(holder.key());
            if (type == null) {
                return;
            }
            List<FanJeiRecipe> list = new ArrayList<>();
            Set<Item> covered = new HashSet<>();

            for (RecipeHolder<FanProcessingRecipe> recipeHolder : recipes.byType(EFRecipes.FAN_PROCESSING_TYPE.get())) {
                FanProcessingRecipe recipe = recipeHolder.value();
                if (recipe.processingType().equals(holder.key())) {
                    list.add(new FanJeiRecipe(holder, recipe.ingredient(), recipe.results()));
                    recipe.ingredient().items().forEach(item -> covered.add(item.value()));
                }
            }

            holder.value().cookingFallback().ifPresent(fallback -> {
                List<RecipeType<? extends AbstractCookingRecipe>> types = fallback == CookingFallback.SMOKING
                        ? List.of(RecipeType.SMOKING)
                        : List.of(RecipeType.BLASTING, RecipeType.SMELTING);
                for (RecipeType<? extends AbstractCookingRecipe> cookingType : types) {
                    for (RecipeHolder<? extends AbstractCookingRecipe> cookingHolder : recipes.byType(cookingType)) {
                        AbstractCookingRecipe recipe = cookingHolder.value();
                        List<Item> inputs = recipe.input().items().map(Holder::value)
                                .filter(item -> !covered.contains(item))
                                .filter(item -> fallback != CookingFallback.BLASTING || !isBlastingExcluded(item, smokable))
                                .toList();
                        if (inputs.isEmpty()) {
                            continue;
                        }
                        ItemStack result = recipe.assemble(new SingleRecipeInput(new ItemStack(inputs.getFirst())));
                        if (result.isEmpty()) {
                            continue;
                        }
                        list.add(new FanJeiRecipe(holder, Ingredient.of(inputs.stream()),
                                List.of(new ProcessingOutput(new ItemStackTemplate(result.getItem(), result.getCount()), 1F))));
                        covered.addAll(inputs);
                    }
                }
            });

            holder.value().copperTransform().ifPresent(transform -> {
                for (Block block : BuiltInRegistries.BLOCK) {
                    Item input = block.asItem();
                    Block next = transform.apply(block);
                    if (next == null || input == Items.AIR || next.asItem() == Items.AIR || covered.contains(input)) {
                        continue;
                    }
                    list.add(new FanJeiRecipe(holder, Ingredient.of(input),
                            List.of(new ProcessingOutput(new ItemStackTemplate(next.asItem()), 1F))));
                }
            });

            registration.addRecipes(type, list);
        }));
    }

    private static boolean isBlastingExcluded(Item item, Set<Item> smokable) {
        ItemStack stack = new ItemStack(item);
        if (stack.is(EFTags.Items.BLASTING_INCLUDE)) {
            return false;
        }
        return stack.is(EFTags.Items.BLASTING_EXCLUDE) || smokable.contains(item);
    }

    private static List<ItemStack> catalystItems(ResourceKey<ProcessingType> key) {
        List<ItemStack> ours = new ArrayList<>();
        List<ItemStack> others = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            BlockCatalyst catalyst = block.builtInRegistryHolder().getData(EFDataMaps.BLOCK_CATALYSTS);
            if (catalyst != null && catalyst.processingType().equals(key) && block.asItem() != Items.AIR) {
                ItemStack stack = new ItemStack(block);
                if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(EncasedFans.MODID)) {
                    ours.add(stack);
                } else {
                    others.add(stack);
                }
            }
        }
        for (Fluid fluid : BuiltInRegistries.FLUID) {
            FluidCatalyst catalyst = fluid.builtInRegistryHolder().getData(EFDataMaps.FLUID_CATALYSTS);
            if (catalyst != null && catalyst.processingType().equals(key) && fluid.getBucket() != Items.AIR) {
                others.add(new ItemStack(fluid.getBucket()));
            }
        }
        ours.addAll(others);
        return ours;
    }

    private static ItemStack displayCatalyst(ResourceKey<ProcessingType> key) {
        List<ItemStack> catalysts = catalystItems(key);
        return catalysts.isEmpty() ? new ItemStack(Items.BARRIER) : catalysts.getFirst();
    }
}