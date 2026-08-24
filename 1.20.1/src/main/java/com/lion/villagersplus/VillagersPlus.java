/*
 * Villagers Plus Neo - an unofficial 1.20.1 NeoForge port of
 * VillagersPlus, (c) finallion.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * finallion released the original under GPL-3.0 without granting the
 * "or any later version" option, so this port cannot grant it either.
 */
package com.lion.villagersplus;

import com.lion.villagersplus.config.VPConfig;
import com.lion.villagersplus.init.VPBlockEntities;
import com.lion.villagersplus.init.VPBlocks;
import com.lion.villagersplus.init.VPCreativeTabs;
import com.lion.villagersplus.init.VPItems;
import com.lion.villagersplus.init.VPMenus;
import com.lion.villagersplus.init.VPPointOfInterestTypes;
import com.lion.villagersplus.init.VPVillagerProfessions;
import com.lion.villagersplus.tradeoffers.VPTrades;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(VillagersPlus.MOD_ID)
public class VillagersPlus {

    public static final String MOD_ID = "villagersplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public VillagersPlus() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        VPBlocks.BLOCKS.register(modBus);
        VPItems.ITEMS.register(modBus);
        VPBlockEntities.BLOCK_ENTITIES.register(modBus);
        VPCreativeTabs.TABS.register(modBus);
        VPPointOfInterestTypes.POI_TYPES.register(modBus);
        VPVillagerProfessions.PROFESSIONS.register(modBus);
        VPMenus.MENUS.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, VPConfig.SPEC);

        modBus.addListener(this::commonSetup);

        LOGGER.info("Villagers Plus Neo 4.4.2 (1.20.1 NeoForge port) initialised");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        VPTrades.load();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
