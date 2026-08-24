package com.lion.villagersplus.blocks;

import com.lion.villagersplus.blockentities.OccultistTableBlockEntity;
import com.lion.villagersplus.config.VPConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/** Enchanted basin: stores experience. Right-click withdraws, sneak right-click deposits. */
public class OccultistTableBlock extends WorkstationBlock {

    public static final IntegerProperty FILLING = IntegerProperty.create("filling", 0, 5);

    public OccultistTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FILLING, 0));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OccultistTableBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof OccultistTableBlockEntity basin) || player.isCreative()) {
            return InteractionResult.PASS;
        }

        basin.interact(level, player);

        if (level.isClientSide()) {
            double x = pos.getX() + 0.5D;
            double z = pos.getZ() + 0.5D;
            if (player.isShiftKeyDown()) {
                createParticleSpiral(level, x, pos.getY(), z, 250, ParticleTypes.SOUL, level.random);
                level.playLocalSound(x, pos.getY(), z, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 3.0F, 1.0F, false);
            } else {
                createParticleSpiral(level, x, pos.getY(), z, 250, ParticleTypes.ENCHANT, level.random);
                level.playLocalSound(x, pos.getY(), z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS,
                        3.0F, 1.0F, false);
            }
        } else {
            level.setBlock(pos, state.setValue(FILLING, fillingFor(basin.getLevels())), Block.UPDATE_CLIENTS);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    /** Maps stored experience onto the six visual fill levels. */
    private static int fillingFor(int levels) {
        int max = VPConfig.maxExpAmount();
        if (levels <= 0) {
            return 0;
        }
        if (levels >= 0.8D * max) {
            return 5;
        }
        if (levels >= 0.6D * max) {
            return 4;
        }
        if (levels >= 0.4D * max) {
            return 3;
        }
        if (levels >= 0.2D * max) {
            return 2;
        }
        return 1;
    }

    public static <T extends ParticleOptions> void createParticleSpiral(Level level, double x, double y, double z,
                                                                        int length, T type, RandomSource random) {
        double yCoord = y + 1.1D;
        for (int i = 0; i < length; i++) {
            float densityFactor = (float) i / 15.0F;
            double xCoord = x + Mth.sin(densityFactor) / 3.0D;
            yCoord += 0.0075D;
            double zCoord = z + Mth.cos(densityFactor) / 3.0D;
            if (random.nextInt(7) == 0) {
                level.addParticle(type, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(FILLING) > 0 && random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5D + random.nextDouble() - random.nextDouble(),
                    pos.getY() + 1.0D + random.nextDouble(),
                    pos.getZ() + 0.5D + random.nextDouble() - random.nextDouble(),
                    0.0D, 0.05D, 0.0D);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof OccultistTableBlockEntity basin
                    && level instanceof ServerLevel serverLevel && basin.getLevels() > 0) {
                this.popExperience(serverLevel, pos, basin.getLevels());
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILLING);
    }
}
