package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Aquarium: four coral/seaplant slots plus one slot for a bucketed fish. */
public class OceanographerTableBlockEntity extends ContainerWorkstationBlockEntity {

    public static final int SIZE = 5;
    /** Slot index that holds the bucketed entity. */
    public static final int FISH_SLOT = 4;

    public OceanographerTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.OCEANOGRAPHER_TABLE.get(), pos, state, SIZE);
    }
}
