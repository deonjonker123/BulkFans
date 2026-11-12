package com.misterd.encasedfans.util;

import com.misterd.encasedfans.EncasedFans;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EFTags {
    public static class Items {
        public static final TagKey<Item> BLASTING_EXCLUDE = tag("blasting_exclude");
        public static final TagKey<Item> BLASTING_INCLUDE = tag("blasting_include");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EncasedFans.MODID, name));
        }
    }
}