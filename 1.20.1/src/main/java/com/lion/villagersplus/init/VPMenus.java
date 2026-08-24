package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.menu.AlchemistTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class VPMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VillagersPlus.MOD_ID);

    public static final RegistryObject<MenuType<AlchemistTableMenu>> ALCHEMIST_TABLE =
            MENUS.register("alchemist_table",
                    () -> IForgeMenuType.create((id, inventory, buf) -> new AlchemistTableMenu(id, inventory)));

    private VPMenus() {
    }
}
