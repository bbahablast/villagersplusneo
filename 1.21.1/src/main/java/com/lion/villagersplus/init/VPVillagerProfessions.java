package com.lion.villagersplus.init;

import com.google.common.collect.ImmutableSet;
import com.lion.villagersplus.VillagersPlus;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Predicate;

public final class VPVillagerProfessions {

    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, VillagersPlus.MOD_ID);

    /** Any of the eleven wood variants counts as a horticulturist job site. */
    private static final Predicate<Holder<PoiType>> HORTICULTURIST_SITE = holder ->
            VPPointOfInterestTypes.HORTICULTURIST_POIS.stream().anyMatch(poi -> holder.value() == poi.get());

    public static final DeferredHolder<VillagerProfession, VillagerProfession> HORTICULTURIST =
            register("horticulturist", HORTICULTURIST_SITE, SoundEvents.VILLAGER_WORK_FARMER);

    public static final DeferredHolder<VillagerProfession, VillagerProfession> OCEANOGRAPHER =
            register("oceanographer", matches(VPPointOfInterestTypes.OCEANOGRAPHER), SoundEvents.BUCKET_FILL);

    public static final DeferredHolder<VillagerProfession, VillagerProfession> OCCULTIST =
            register("occultist", matches(VPPointOfInterestTypes.OCCULTIST), SoundEvents.VILLAGER_WORK_CLERIC);

    public static final DeferredHolder<VillagerProfession, VillagerProfession> ALCHEMIST =
            register("alchemist", matches(VPPointOfInterestTypes.ALCHEMIST), SoundEvents.VILLAGER_WORK_CLERIC);

    private VPVillagerProfessions() {
    }

    private static Predicate<Holder<PoiType>> matches(DeferredHolder<PoiType, PoiType> poi) {
        return holder -> holder.value() == poi.get();
    }

    private static DeferredHolder<VillagerProfession, VillagerProfession> register(
            String name, Predicate<Holder<PoiType>> jobSite, SoundEvent workSound) {
        return PROFESSIONS.register(name, () -> new VillagerProfession(
                VillagersPlus.MOD_ID + ":" + name,
                jobSite,
                jobSite,
                ImmutableSet.of(),
                ImmutableSet.of(),
                workSound));
    }

    public static void init() {
    }
}
