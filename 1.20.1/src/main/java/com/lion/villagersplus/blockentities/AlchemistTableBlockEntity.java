package com.lion.villagersplus.blockentities;

import com.lion.villagersplus.blocks.AlchemistTableBlock;
import com.lion.villagersplus.config.VPConfig;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.menu.AlchemistTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Alchemist table: brews the bottles in its three potion slots into random
 * potions, consuming one ingredient and some fuel, with a chance of exploding.
 */
public class AlchemistTableBlockEntity extends BaseContainerBlockEntity {

    public static final int SIZE = 5;
    public static final int[] BOTTLE_SLOTS = {0, 1, 2};
    public static final int INGREDIENT_SLOT = 3;
    public static final int FUEL_SLOT = 4;

    /** Ticks a single brew takes, matching the vanilla brewing stand. */
    private static final int BREW_TIME = 400;
    /** Brews one unit of fuel provides. */
    private static final int FUEL_PER_POWDER = 20;

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int brewTime;
    private int fuel;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AlchemistTableBlockEntity.this.brewTime;
                case 1 -> AlchemistTableBlockEntity.this.fuel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AlchemistTableBlockEntity.this.brewTime = value;
                case 1 -> AlchemistTableBlockEntity.this.fuel = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public AlchemistTableBlockEntity(BlockPos pos, BlockState state) {
        super(VPBlockEntities.ALCHEMIST_TABLE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AlchemistTableBlockEntity table) {
        ItemStack fuelStack = table.items.get(FUEL_SLOT);
        if (table.fuel <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
            table.fuel = FUEL_PER_POWDER;
            fuelStack.shrink(1);
            setChanged(level, pos, state);
        }

        boolean canBrew = table.canBrew();
        boolean brewing = table.brewTime > 0;

        if (brewing) {
            table.brewTime--;
            if (table.brewTime == 0 && canBrew) {
                table.brew(level, pos);
                setChanged(level, pos, state);
            } else if (!canBrew) {
                table.brewTime = 0;
                setChanged(level, pos, state);
            }
        } else if (canBrew && table.fuel > 0) {
            table.fuel--;
            table.brewTime = BREW_TIME;
            setChanged(level, pos, state);
        }

        BlockState updated = table.withVisualState(state);
        if (updated != state) {
            level.setBlock(pos, updated, 2);
        }
    }

    /** Mirrors the filled bottles and brewing state onto the block for its model. */
    private BlockState withVisualState(BlockState state) {
        BlockState result = state;
        for (int i = 0; i < BOTTLE_SLOTS.length; i++) {
            result = result.setValue(AlchemistTableBlock.BOTTLE_PROPERTIES[i], !this.items.get(i).isEmpty());
        }
        return result
                .setValue(AlchemistTableBlock.EXTENDED, !this.items.get(INGREDIENT_SLOT).isEmpty())
                .setValue(AlchemistTableBlock.HAS_FUEL, this.fuel > 0)
                .setValue(AlchemistTableBlock.IS_BREWING, this.brewTime > 0);
    }

    private boolean canBrew() {
        if (this.items.get(INGREDIENT_SLOT).isEmpty()) {
            return false;
        }
        for (int slot : BOTTLE_SLOTS) {
            if (!this.items.get(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** Turns every filled bottle into a random potion, or blows the table up. */
    private void brew(Level level, BlockPos pos) {
        RandomSource random = level.getRandom();
        int chance = VPConfig.alchemistExplosionChance();

        if (chance > 0 && random.nextInt(chance) == 0) {
            level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    3.0F, Level.ExplosionInteraction.NONE);
            this.items.clear();
            return;
        }

        for (int slot : BOTTLE_SLOTS) {
            ItemStack bottle = this.items.get(slot);
            if (!bottle.isEmpty()) {
                this.items.set(slot, randomPotion(random));
            }
        }
        this.items.get(INGREDIENT_SLOT).shrink(1);
        level.levelEvent(1035, pos, 0);
    }

    private static ItemStack randomPotion(RandomSource random) {
        Potion potion = BuiltInRegistries.POTION.getRandom(random)
                .map(Holder::value)
                .orElse(null);
        if (potion == null) {
            return ItemStack.EMPTY;
        }
        return PotionUtils.setPotion(
                new ItemStack(random.nextBoolean() ? Items.SPLASH_POTION : Items.POTION), potion);
    }

    public ContainerData getData() {
        return this.data;
    }

    // --- BaseContainerBlockEntity ------------------------------------------

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.villagersplus.alchemist_table");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AlchemistTableMenu(containerId, inventory, this, this.data);
    }

    // 1.20.1's BaseContainerBlockEntity is only the naming and menu half of a
    // container; the storage half is left to the subclass.

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < this.items.size() ? this.items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);
        if (!removed.isEmpty()) {
            this.setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.items.size()) {
            return;
        }
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.brewTime = tag.getInt("BrewTime");
        this.fuel = tag.getInt("Fuel");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putInt("BrewTime", this.brewTime);
        tag.putInt("Fuel", this.fuel);
    }
}
