package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.menu.AlchemistTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class VPMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, VillagersPlus.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AlchemistTableMenu>> ALCHEMIST_TABLE =
            MENUS.register("alchemist_table",
                    () -> IMenuTypeExtension.create((id, inventory, buf) -> new AlchemistTableMenu(id, inventory)));

    private VPMenus() {
    }
}
