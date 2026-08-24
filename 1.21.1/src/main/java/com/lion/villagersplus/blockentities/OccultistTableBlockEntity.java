package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.config.VPConfig;
import com.lion.villagersplus.init.VPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Enchanted basin: stores experience points that players can deposit and withdraw. */
public class OccultistTableBlockEntity extends BlockEntity {

    private int levels = 0;

    public OccultistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.OCCULTIST_TABLE.get(), pos, state);
    }

    public int getLevels() {
        return this.levels;
    }

    /**
     * Sneaking deposits experience into the basin, otherwise experience is withdrawn.
     * Only ever mutates state on the server.
     */
    public void interact(Level level, Player player) {
        if (level.isClientSide()) {
            return;
        }

        final int amount = VPConfig.expAmount();
        final int max = VPConfig.maxExpAmount();

        if (player.isShiftKeyDown()) {
            int space = max - this.levels;
            if (space <= 0) {
                return;
            }
            int deposit = Math.min(Math.min(amount, space), player.totalExperience);
            if (deposit > 0) {
                player.giveExperiencePoints(-deposit);
                this.levels += deposit;
                this.setChanged();
            }
        } else {
            int withdraw = Math.min(amount, this.levels);
            if (withdraw > 0) {
                player.giveExperiencePoints(withdraw);
                this.levels -= withdraw;
                this.setChanged();
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.levels = tag.getInt("Levels");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Levels", this.levels);
    }
}
