package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/** BlockItems for every workstation block. */
public final class VPItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VillagersPlus.MOD_ID);

    /** Registered in the same order as {@link VPBlocks#allWithItems()}. */
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = registerBlockItems();

    private VPItems() {
    }

    private static List<DeferredItem<BlockItem>> registerBlockItems() {
        List<DeferredItem<BlockItem>> items = new ArrayList<>();
        for (var block : VPBlocks.allWithItems()) {
            items.add(ITEMS.registerSimpleBlockItem(block));
        }
        return List.copyOf(items);
    }

    public static Item icon() {
        return BLOCK_ITEMS.get(0).get();
    }

    public static void init() {
    }
}
