package com.lion.villagersplus.worldgen;

import com.lion.villagersplus.VillagersPlus;
import com.lion.villagersplus.config.VPConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Adds the four profession houses to vanilla's village house pools.
 *
 * <p>There is no data-driven way to append to a template pool in 1.20.1 - a
 * datapack can only replace one outright, which would silently drop every other
 * mod's village buildings. So the pools are edited in place once the datapack
 * registries exist, which is what the original mod did through a mixin.
 */
@Mod.EventBusSubscriber(modid = VillagersPlus.MOD_ID)
public final class VPVillageStructures {

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSORS =
            ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("minecraft", "empty"));

    /**
     * {@code StructureTemplatePool#templates} - the flattened list a pool actually draws
     * from, where an element's weight is simply how many times it appears. It is looked up
     * by shape rather than by name so that the obfuscated field name does not matter, which
     * is what keeps this working without an access transformer.
     *
     * <p>The pool's other list, {@code rawTemplates}, is only the weighted form the pool was
     * deserialised from. Nothing in world generation reads it, and the codec hands the pool
     * an immutable list, so it is left alone.
     */
    private static final Field TEMPLATES_FIELD = findTemplatesField();

    private VPVillageStructures() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (TEMPLATES_FIELD == null) {
            VillagersPlus.LOGGER.error(
                    "Could not find the template list on StructureTemplatePool; village houses will not generate");
            return;
        }

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
            StructureTemplatePool pool = pools.get(new ResourceLocation("village/" + village + "/houses"));
            if (pool == null) {
                VillagersPlus.LOGGER.warn("No village/{}/houses pool, skipping {}", village, key);
                continue;
            }

            StructurePoolElement piece = StructurePoolElement
                    .single(VillagersPlus.MOD_ID + ":village/" + village + "/" + key, emptyProcessors)
                    .apply(StructureTemplatePool.Projection.RIGID);
            if (addToPool(pool, piece, weight)) {
                added++;
            }
        }

        VillagersPlus.LOGGER.info("Added {} village buildings to the vanilla house pools", added);
    }

    /** Appends the piece to the pool once per point of weight. */
    private static boolean addToPool(StructureTemplatePool pool, StructurePoolElement piece, int weight) {
        try {
            @SuppressWarnings("unchecked")
            List<StructurePoolElement> templates = (List<StructurePoolElement>) TEMPLATES_FIELD.get(pool);
            for (int i = 0; i < weight; i++) {
                templates.add(piece);
            }
            return true;
        } catch (ReflectiveOperationException | UnsupportedOperationException e) {
            VillagersPlus.LOGGER.error("Failed to add a village building to a house pool", e);
            return false;
        }
    }

    private static Field findTemplatesField() {
        Field byGenericType = null;
        Field byConcreteType = null;

        for (Field field : StructureTemplatePool.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || !List.class.isAssignableFrom(field.getType())) {
                continue;
            }
            // templates is List<StructurePoolElement>; rawTemplates is List<Pair<..., Integer>>.
            if (field.getGenericType() instanceof ParameterizedType parameterized
                    && parameterized.getActualTypeArguments().length == 1
                    && parameterized.getActualTypeArguments()[0] == StructurePoolElement.class) {
                byGenericType = field;
            }
            if (ObjectArrayList.class.isAssignableFrom(field.getType())) {
                byConcreteType = field;
            }
        }

        Field found = byGenericType != null ? byGenericType : byConcreteType;
        if (found != null) {
            found.setAccessible(true);
        }
        return found;
    }
}
