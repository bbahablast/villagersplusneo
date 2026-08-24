package com.lion.villagersplus.menu;

import com.lion.villagersplus.blockentities.AlchemistTableBlockEntity;
import com.lion.villagersplus.init.VPMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Brewing-stand style menu: three bottles, one ingredient, one fuel. */
public class AlchemistTableMenu extends AbstractContainerMenu {

    private static final int CONTAINER_SLOTS = AlchemistTableBlockEntity.SIZE;
    private static final int INVENTORY_START = CONTAINER_SLOTS;
    private static final int INVENTORY_END = INVENTORY_START + 36;

    private final Container container;
    private final ContainerData data;

    /** Client-side constructor: the contents arrive through the usual sync. */
    public AlchemistTableMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CONTAINER_SLOTS), new SimpleContainerData(2));
    }

    public AlchemistTableMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(VPMenus.ALCHEMIST_TABLE.get(), containerId);
        checkContainerSize(container, CONTAINER_SLOTS);
        checkContainerDataCount(data, 2);
        this.container = container;
        this.data = data;

        // Layout matches the vanilla brewing stand, which the GUI texture is based on.
        this.addSlot(new PotionSlot(container, 0, 56, 51));
        this.addSlot(new PotionSlot(container, 1, 79, 58));
        this.addSlot(new PotionSlot(container, 2, 102, 51));
        this.addSlot(new Slot(container, AlchemistTableBlockEntity.INGREDIENT_SLOT, 79, 17));
        this.addSlot(new FuelSlot(container, AlchemistTableBlockEntity.FUEL_SLOT, 17, 17));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    /** Brew progress as a 0-1 fraction, for the bubble and arrow overlays. */
    public float getBrewProgress() {
        int time = this.data.get(0);
        return time <= 0 ? 0.0F : time / 400.0F;
    }

    public int getFuel() {
        return this.data.get(1);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < CONTAINER_SLOTS) {
            // Table -> player inventory.
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.is(Items.BLAZE_POWDER)) {
            if (!this.moveItemStackTo(stack, AlchemistTableBlockEntity.FUEL_SLOT,
                    AlchemistTableBlockEntity.FUEL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (PotionSlot.isBottle(stack)) {
            if (!this.moveItemStackTo(stack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, AlchemistTableBlockEntity.INGREDIENT_SLOT,
                AlchemistTableBlockEntity.INGREDIENT_SLOT + 1, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    /** Only bottles and potions belong in the three brewing slots. */
    static class PotionSlot extends Slot {
        PotionSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        static boolean isBottle(ItemStack stack) {
            return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION)
                    || stack.is(Items.LINGERING_POTION) || stack.is(Items.GLASS_BOTTLE);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isBottle(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    static class FuelSlot extends Slot {
        FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.BLAZE_POWDER);
        }
    }
}
