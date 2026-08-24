package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class VPTags {

    public static final TagKey<Item> TALL_PLANTABLE_ITEMS = itemTag("flower_tub_tall_plantable_items");
    public static final TagKey<Item> SMALL_PLANTABLE_ITEMS = itemTag("flower_tub_small_plantable_items");
    public static final TagKey<Item> AQUARIUM_PLANTABLE_ITEMS = itemTag("aquarium_plantable_items");

    private VPTags() {
    }

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(VillagersPlus.MOD_ID, path));
    }
}
