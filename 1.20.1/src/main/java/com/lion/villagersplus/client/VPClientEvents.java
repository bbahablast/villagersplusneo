package com.lion.villagersplus.client;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = VillagersPlus.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class VPClientEvents {

    private VPClientEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VPBlockEntities.HORTICULTURIST_TABLE.get(),
                HorticulturistTableRenderer::new);
        event.registerBlockEntityRenderer(VPBlockEntities.OCEANOGRAPHER_TABLE.get(),
                OceanographerTableRenderer::new);
    }

    /** 1.20.1 has no RegisterMenuScreensEvent, so screens are bound during client setup. */
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(VPMenus.ALCHEMIST_TABLE.get(), AlchemistTableScreen::new));
    }
}
