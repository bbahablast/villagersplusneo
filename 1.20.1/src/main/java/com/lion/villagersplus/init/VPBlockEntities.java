package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class VPBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VillagersPlus.MOD_ID);

    public static final RegistryObject<BlockEntityType<HorticulturistTableBlockEntity>> HORTICULTURIST_TABLE =
            BLOCK_ENTITIES.register("horticulturist_table",
                    () -> BlockEntityType.Builder.of(HorticulturistTableBlockEntity::new, horticulturistBlocks())
                            .build(null));

    public static final RegistryObject<BlockEntityType<OceanographerTableBlockEntity>> OCEANOGRAPHER_TABLE =
            BLOCK_ENTITIES.register("oceanographer_table",
                    () -> BlockEntityType.Builder.of(OceanographerTableBlockEntity::new,
                            VPBlocks.OCEANOGRAPHER_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<OccultistTableBlockEntity>> OCCULTIST_TABLE =
            BLOCK_ENTITIES.register("occultist_table",
                    () -> BlockEntityType.Builder.of(OccultistTableBlockEntity::new,
                            VPBlocks.OCCULTIST_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AlchemistTableBlockEntity>> ALCHEMIST_TABLE =
            BLOCK_ENTITIES.register("alchemist_table",
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
