package com.lion.villagersplus.blocks;

import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.init.VPTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/** Flower tub: accepts four small plants, or one tall plant that fills the whole tub. */
public class HorticulturistTableBlock extends WorkstationBlock {

    public static final MapCodec<HorticulturistTableBlock> CODEC = simpleCodec(HorticulturistTableBlock::new);

    public static final IntegerProperty FLOWERS = IntegerProperty.create("flowers", 0, 4);
    public static final BooleanProperty IS_TALL_FLOWER = BooleanProperty.create("is_tall_flower");

    public HorticulturistTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FLOWERS, 0)
                .setValue(IS_TALL_FLOWER, false));
    }

    @Override
    protected MapCodec<? extends WorkstationBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HorticulturistTableBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof HorticulturistTableBlockEntity tub)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int flowers = state.getValue(FLOWERS);
        if (flowers >= 4) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        boolean tall = stack.is(VPTags.TALL_PLANTABLE_ITEMS) && flowers == 0;
        boolean small = stack.is(VPTags.SMALL_PLANTABLE_ITEMS);

        if (!tall && !small) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide()) {
            // A tall plant occupies the first slot but visually fills the tub.
            tub.insert(stack, tall ? 0 : flowers);
            BlockState placed = tall
                    ? state.setValue(FLOWERS, 4).setValue(IS_TALL_FLOWER, true)
                    : state.setValue(FLOWERS, flowers + 1).setValue(IS_TALL_FLOWER, false);
            level.setBlock(pos, placed, Block.UPDATE_ALL);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof HorticulturistTableBlockEntity tub) {
                Containers.dropContents(level, pos, tub);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(FLOWERS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLOWERS, IS_TALL_FLOWER);
    }
}
