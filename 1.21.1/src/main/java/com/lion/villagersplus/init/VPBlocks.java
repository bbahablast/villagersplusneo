package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.AlchemistTableBlock;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import com.lion.villagersplus.blocks.OccultistTableBlock;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

/** Registration of all VillagersPlus workstation blocks. */
public final class VPBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(VillagersPlus.MOD_ID);

    private static BlockBehaviour.Properties woodTable() {
        return BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().ignitedByLava();
    }

    // --- Horticulturist (flower tub), one per wood type ---
    public static final DeferredBlock<HorticulturistTableBlock> OAK_HORTICULTURIST_TABLE =
            BLOCKS.register("oak_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> DARK_OAK_HORTICULTURIST_TABLE =
            BLOCKS.register("dark_oak_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> ACACIA_HORTICULTURIST_TABLE =
            BLOCKS.register("acacia_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> JUNGLE_HORTICULTURIST_TABLE =
            BLOCKS.register("jungle_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> SPRUCE_HORTICULTURIST_TABLE =
            BLOCKS.register("spruce_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> BIRCH_HORTICULTURIST_TABLE =
            BLOCKS.register("birch_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> MANGROVE_HORTICULTURIST_TABLE =
            BLOCKS.register("mangrove_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> CRIMSON_HORTICULTURIST_TABLE =
            BLOCKS.register("crimson_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> WARPED_HORTICULTURIST_TABLE =
            BLOCKS.register("warped_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> CHERRY_HORTICULTURIST_TABLE =
            BLOCKS.register("cherry_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final DeferredBlock<HorticulturistTableBlock> BAMBOO_HORTICULTURIST_TABLE =
            BLOCKS.register("bamboo_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));

    /** Every horticulturist table, in creative-tab order. */
    public static final List<DeferredBlock<HorticulturistTableBlock>> HORTICULTURIST_TABLES = List.of(
            OAK_HORTICULTURIST_TABLE, BIRCH_HORTICULTURIST_TABLE, SPRUCE_HORTICULTURIST_TABLE,
            DARK_OAK_HORTICULTURIST_TABLE, JUNGLE_HORTICULTURIST_TABLE, ACACIA_HORTICULTURIST_TABLE,
            MANGROVE_HORTICULTURIST_TABLE, CRIMSON_HORTICULTURIST_TABLE, WARPED_HORTICULTURIST_TABLE,
            CHERRY_HORTICULTURIST_TABLE, BAMBOO_HORTICULTURIST_TABLE);

    // --- Oceanographer (aquarium) ---
    public static final DeferredBlock<OceanographerTableBlock> OCEANOGRAPHER_TABLE =
            BLOCKS.register("oceanographer_table", () -> new OceanographerTableBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.5F)
                            .lightLevel(state -> 12)
                            .noOcclusion()
                            .isRedstoneConductor((state, level, pos) -> false)
                            .isSuffocating((state, level, pos) -> false)
                            .isViewBlocking((state, level, pos) -> false)));

    // --- Occultist (enchanted basin) ---
    public static final DeferredBlock<OccultistTableBlock> OCCULTIST_TABLE =
            BLOCKS.register("occultist_table", () -> new OccultistTableBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.5F)
                            .lightLevel(state -> state.getValue(OccultistTableBlock.FILLING) * 2)
                            .noOcclusion()));

    // --- Alchemist ---
    public static final DeferredBlock<AlchemistTableBlock> ALCHEMIST_TABLE =
            BLOCKS.register("alchemist_table", () -> new AlchemistTableBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.5F)
                            .lightLevel(state -> 1)
                            .noOcclusion()));

    private VPBlocks() {
    }

    public static void init() {
    }

    /** All blocks that should get a simple BlockItem, in creative-tab order. */
    public static List<DeferredBlock<? extends Block>> allWithItems() {
        var list = new java.util.ArrayList<DeferredBlock<? extends Block>>(HORTICULTURIST_TABLES);
        list.add(OCEANOGRAPHER_TABLE);
        list.add(OCCULTIST_TABLE);
        list.add(ALCHEMIST_TABLE);
        return List.copyOf(list);
    }
}
