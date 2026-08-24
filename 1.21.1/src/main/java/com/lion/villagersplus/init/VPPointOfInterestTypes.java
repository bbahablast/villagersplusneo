package com.lion.villagersplus.init;

import com.google.common.collect.ImmutableSet;
import com.lion.villagersplus.VillagersPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

/**
 * Points of interest for the workstation blocks. Each POI is what lets a villager
 * claim the matching workstation and take up the profession.
 *
 * <p>NeoForge adds the block states of POI types registered here to vanilla's
 * blockstate lookup by itself, so no manual registration is needed. Doing it
 * manually makes vanilla throw "defined in more than one PoI type".
 */
public final class VPPointOfInterestTypes {

    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, VillagersPlus.MOD_ID);

    public static final List<DeferredHolder<PoiType, PoiType>> HORTICULTURIST_POIS = List.of(
            register("horticulturist_oak", VPBlocks.OAK_HORTICULTURIST_TABLE),
            register("horticulturist_dark_oak", VPBlocks.DARK_OAK_HORTICULTURIST_TABLE),
            register("horticulturist_acacia", VPBlocks.ACACIA_HORTICULTURIST_TABLE),
            register("horticulturist_jungle", VPBlocks.JUNGLE_HORTICULTURIST_TABLE),
            register("horticulturist_spruce", VPBlocks.SPRUCE_HORTICULTURIST_TABLE),
            register("horticulturist_birch", VPBlocks.BIRCH_HORTICULTURIST_TABLE),
            register("horticulturist_mangrove", VPBlocks.MANGROVE_HORTICULTURIST_TABLE),
            register("horticulturist_crimson", VPBlocks.CRIMSON_HORTICULTURIST_TABLE),
            register("horticulturist_warped", VPBlocks.WARPED_HORTICULTURIST_TABLE),
            register("horticulturist_cherry", VPBlocks.CHERRY_HORTICULTURIST_TABLE),
            register("horticulturist_bamboo", VPBlocks.BAMBOO_HORTICULTURIST_TABLE));

    public static final DeferredHolder<PoiType, PoiType> OCEANOGRAPHER =
            register("oceanographer", VPBlocks.OCEANOGRAPHER_TABLE);
    public static final DeferredHolder<PoiType, PoiType> OCCULTIST =
            register("occultist", VPBlocks.OCCULTIST_TABLE);
    public static final DeferredHolder<PoiType, PoiType> ALCHEMIST =
            register("alchemist", VPBlocks.ALCHEMIST_TABLE);

    private VPPointOfInterestTypes() {
    }

    private static DeferredHolder<PoiType, PoiType> register(String name, Supplier<? extends Block> block) {
        return POI_TYPES.register(name,
                () -> new PoiType(ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates()), 1, 1));
    }

    public static void init() {
    }
}
