package com.lion.villagersplus.blocks;

import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.init.VPTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/** Aquarium: holds up to four corals/seaplants and a single bucketed fish. */
public class OceanographerTableBlock extends WorkstationBlock {

    public static final IntegerProperty CORALS = IntegerProperty.create("corals", 0, 4);
    public static final IntegerProperty FISH = IntegerProperty.create("fish", 0, 1);
    public static final BooleanProperty IS_FILLED = BooleanProperty.create("is_filled");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public OceanographerTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CORALS, 0)
                .setValue(FISH, 0)
                .setValue(IS_FILLED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OceanographerTableBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextBoolean()) {
            double x = pos.getX() + 0.1D + 0.8D * random.nextDouble();
            double y = pos.getY() + 0.1D + 0.3D * random.nextDouble();
            double z = pos.getZ() + 0.1D + 0.8D * random.nextDouble();
            level.addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE, x, y, z, 0.0D, 0.000001D, 0.0D);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof OceanographerTableBlockEntity aquarium)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        int corals = state.getValue(CORALS);

        if (stack.is(VPTags.AQUARIUM_PLANTABLE_ITEMS) && corals < 4) {
            if (!level.isClientSide()) {
                aquarium.insert(stack, corals);
                level.setBlock(pos, state.setValue(CORALS, corals + 1), Block.UPDATE_ALL);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                level.playSound(null, pos, SoundEvents.CORAL_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (stack.getItem() instanceof MobBucketItem && state.getValue(FISH) < 1) {
            if (!level.isClientSide()) {
                aquarium.insert(stack, OceanographerTableBlockEntity.FISH_SLOT);
                level.setBlock(pos, state.setValue(FISH, 1), Block.UPDATE_ALL);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof OceanographerTableBlockEntity aquarium) {
                Containers.dropContents(level, pos, aquarium);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(FISH) + state.getValue(CORALS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CORALS, FISH, IS_FILLED, FACING);
    }
}
