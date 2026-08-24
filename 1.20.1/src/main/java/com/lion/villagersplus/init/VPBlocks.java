package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blocks.AlchemistTableBlock;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import com.lion.villagersplus.blocks.OccultistTableBlock;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/** Registration of all VillagersPlus workstation blocks. */
public final class VPBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, VillagersPlus.MOD_ID);

    private static BlockBehaviour.Properties woodTable() {
        return BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().ignitedByLava();
    }

    // --- Horticulturist (flower tub), one per wood type ---
    public static final RegistryObject<HorticulturistTableBlock> OAK_HORTICULTURIST_TABLE =
            BLOCKS.register("oak_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> DARK_OAK_HORTICULTURIST_TABLE =
            BLOCKS.register("dark_oak_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> ACACIA_HORTICULTURIST_TABLE =
            BLOCKS.register("acacia_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> JUNGLE_HORTICULTURIST_TABLE =
            BLOCKS.register("jungle_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> SPRUCE_HORTICULTURIST_TABLE =
            BLOCKS.register("spruce_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> BIRCH_HORTICULTURIST_TABLE =
            BLOCKS.register("birch_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> MANGROVE_HORTICULTURIST_TABLE =
            BLOCKS.register("mangrove_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> CRIMSON_HORTICULTURIST_TABLE =
            BLOCKS.register("crimson_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> WARPED_HORTICULTURIST_TABLE =
            BLOCKS.register("warped_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> CHERRY_HORTICULTURIST_TABLE =
            BLOCKS.register("cherry_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));
    public static final RegistryObject<HorticulturistTableBlock> BAMBOO_HORTICULTURIST_TABLE =
            BLOCKS.register("bamboo_horticulturist_table", () -> new HorticulturistTableBlock(woodTable()));

    /** Every horticulturist table, in creative-tab order. */
    public static final List<RegistryObject<HorticulturistTableBlock>> HORTICULTURIST_TABLES = List.of(
            OAK_HORTICULTURIST_TABLE, BIRCH_HORTICULTURIST_TABLE, SPRUCE_HORTICULTURIST_TABLE,
            DARK_OAK_HORTICULTURIST_TABLE, JUNGLE_HORTICULTURIST_TABLE, ACACIA_HORTICULTURIST_TABLE,
            MANGROVE_HORTICULTURIST_TABLE, CRIMSON_HORTICULTURIST_TABLE, WARPED_HORTICULTURIST_TABLE,
            CHERRY_HORTICULTURIST_TABLE, BAMBOO_HORTICULTURIST_TABLE);

    // --- Oceanographer (aquarium) ---
    public static final RegistryObject<OceanographerTableBlock> OCEANOGRAPHER_TABLE =
            BLOCKS.register("oceanographer_table", () -> new OceanographerTableBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.5F)
                            .lightLevel(state -> 12)
                            .noOcclusion()
                            .isRedstoneConductor((state, level, pos) -> false)
                            .isSuffocating((state, level, pos) -> false)
                            .isViewBlocking((state, level, pos) -> false)));

    // --- Occultist (enchanted basin) ---
    public static final RegistryObject<OccultistTableBlock> OCCULTIST_TABLE =
            BLOCKS.register("occultist_table", () -> new OccultistTableBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.5F)
                            .lightLevel(state -> state.getValue(OccultistTableBlock.FILLING) * 2)
                            .noOcclusion()));

    // --- Alchemist ---
    public static final RegistryObject<AlchemistTableBlock> ALCHEMIST_TABLE =
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
    public static List<RegistryObject<? extends Block>> allWithItems() {
        List<RegistryObject<? extends Block>> list = new ArrayList<>(HORTICULTURIST_TABLES);
        list.add(OCEANOGRAPHER_TABLE);
        list.add(OCCULTIST_TABLE);
        list.add(ALCHEMIST_TABLE);
        return List.copyOf(list);
    }
}
