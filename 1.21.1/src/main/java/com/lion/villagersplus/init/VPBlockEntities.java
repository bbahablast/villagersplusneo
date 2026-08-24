package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class VPBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VillagersPlus.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HorticulturistTableBlockEntity>>
            HORTICULTURIST_TABLE = BLOCK_ENTITIES.register("horticulturist_table",
            () -> BlockEntityType.Builder.of(HorticulturistTableBlockEntity::new, horticulturistBlocks()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OceanographerTableBlockEntity>>
            OCEANOGRAPHER_TABLE = BLOCK_ENTITIES.register("oceanographer_table",
            () -> BlockEntityType.Builder.of(OceanographerTableBlockEntity::new,
                    VPBlocks.OCEANOGRAPHER_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OccultistTableBlockEntity>>
            OCCULTIST_TABLE = BLOCK_ENTITIES.register("occultist_table",
            () -> BlockEntityType.Builder.of(OccultistTableBlockEntity::new,
                    VPBlocks.OCCULTIST_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemistTableBlockEntity>>
            ALCHEMIST_TABLE = BLOCK_ENTITIES.register("alchemist_table",
            () -> BlockEntityType.Builder.of(AlchemistTableBlockEntity::new,
                    VPBlocks.ALCHEMIST_TABLE.get()).build(null));

    private VPBlockEntities() {
    }

    private static Block[] horticulturistBlocks() {
        return VPBlocks.HORTICULTURIST_TABLES.stream()
                .map(holder -> (Block) holder.get())
                .toArray(Block[]::new);
    }

    public static void init() {
    }
}
