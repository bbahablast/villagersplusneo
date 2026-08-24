package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Flower tub: holds up to four small plants, or a single tall plant. */
public class HorticulturistTableBlockEntity extends ContainerWorkstationBlockEntity {

    public static final int SIZE = 4;

    public HorticulturistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.HORTICULTURIST_TABLE.get(), pos, state, SIZE);
    }
}
