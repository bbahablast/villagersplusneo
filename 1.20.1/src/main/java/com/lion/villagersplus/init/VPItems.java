package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/** BlockItems for every workstation block. */
public final class VPItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VillagersPlus.MOD_ID);

    /** Registered in the same order as {@link VPBlocks#allWithItems()}. */
    public static final List<RegistryObject<BlockItem>> BLOCK_ITEMS = registerBlockItems();

    private VPItems() {
    }

    private static List<RegistryObject<BlockItem>> registerBlockItems() {
        List<RegistryObject<BlockItem>> items = new ArrayList<>();
        for (RegistryObject<? extends Block> block : VPBlocks.allWithItems()) {
            items.add(ITEMS.register(block.getId().getPath(),
                    () -> new BlockItem(block.get(), new Item.Properties())));
        }
        return List.copyOf(items);
    }

    public static Item icon() {
        return BLOCK_ITEMS.get(0).get();
    }

    public static void init() {
    }
}
