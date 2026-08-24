package com.lion.villagersplus.init;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class VPCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VillagersPlus.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("group",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + VillagersPlus.MOD_ID + ".group"))
                    .icon(() -> new ItemStack(VPItems.icon()))
                    .displayItems((params, output) -> VPItems.BLOCK_ITEMS.forEach(item -> output.accept(item.get())))
                    .build());

    private VPCreativeTabs() {
    }
}
