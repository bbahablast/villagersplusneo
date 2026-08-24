package com.lion.villagersplus.client;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.init.VPBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import com.lion.villagersplus.init.VPMenus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = VillagersPlus.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
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

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(VPMenus.ALCHEMIST_TABLE.get(), AlchemistTableScreen::new);
    }
}
