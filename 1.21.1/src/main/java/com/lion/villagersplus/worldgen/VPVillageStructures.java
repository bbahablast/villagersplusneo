package com.lion.villagersplus.worldgen;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.config.VPConfig;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds the four profession houses to vanilla's village house pools.
 *
 * <p>There is no data-driven way to append to a template pool in 1.21.1 - a
 * datapack can only replace one outright, which would silently drop every other
 * mod's village buildings. So the pools are edited in place once the datapack
 * registries exist, which is what the original mod did through a mixin.
 */
@EventBusSubscriber(modid = VillagersPlus.MOD_ID)
public final class VPVillageStructures {

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSORS =
            ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.withDefaultNamespace("empty"));

    private VPVillageStructures() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        RegistryAccess registries = event.getServer().registryAccess();
        Registry<StructureTemplatePool> pools = registries.registryOrThrow(Registries.TEMPLATE_POOL);
        Holder<StructureProcessorList> emptyProcessors = registries
                .registryOrThrow(Registries.PROCESSOR_LIST)
                .getHolderOrThrow(EMPTY_PROCESSORS);

        int added = 0;
        for (String key : VPConfig.structureKeys()) {
            int weight = VPConfig.structureWeight(key);
            if (weight <= 0) {
                continue;
            }

            String village = key.substring(0, key.indexOf('_'));
            StructureTemplatePool pool = pools.get(
                    ResourceLocation.withDefaultNamespace("village/" + village + "/houses"));
            if (pool == null) {
                VillagersPlus.LOGGER.warn("No village/{}/houses pool, skipping {}", village, key);
                continue;
            }

            StructurePoolElement piece = StructurePoolElement
                    .single(VillagersPlus.MOD_ID + ":village/" + village + "/" + key, emptyProcessors)
                    .apply(StructureTemplatePool.Projection.RIGID);
            addToPool(pool, piece, weight);
            added++;
        }

        VillagersPlus.LOGGER.info("Added {} village buildings to the vanilla house pools", added);
    }

    /**
     * A pool draws from {@code templates}, where an element's weight is simply how
     * many times it appears. {@code rawTemplates} is the weighted form the pool was
     * loaded from; it is kept in sync so anything else reading the pool sees us.
     */
    private static void addToPool(StructureTemplatePool pool, StructurePoolElement piece, int weight) {
        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>(pool.rawTemplates);
        rawTemplates.add(Pair.of(piece, weight));
        pool.rawTemplates = rawTemplates;
    }
}
