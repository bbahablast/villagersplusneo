package com.lion.villagersplus.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Server-side configuration for VillagersPlus. */
public final class VPConfig {

    /** Village types whose house pools we extend. */
    public static final List<String> VILLAGE_TYPES = List.of("plains", "desert", "savanna", "snowy", "taiga");

    /** Profession houses, with the weight finallion gave each one. */
    private static final Map<String, Integer> DEFAULT_WEIGHTS = Map.of(
            "alchemist", 10,
            "occultist", 10,
            "horticulturist", 7,
            "oceanographer", 15);

    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue EXP_AMOUNT;
    private static final ModConfigSpec.IntValue MAX_EXP_AMOUNT;
    private static final ModConfigSpec.IntValue ALCHEMIST_EXPLOSION_CHANCE;
    /** Keyed "<village type>_<profession>". */
    private static final Map<String, ModConfigSpec.IntValue> STRUCTURE_WEIGHTS = new LinkedHashMap<>();

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("occultist");
        EXP_AMOUNT = builder
                .comment("Experience points moved into or out of the enchanted basin per interaction.")
                .defineInRange("exp_amount", 100, 1, 100_000);
        MAX_EXP_AMOUNT = builder
                .comment("Maximum experience points the enchanted basin can store.")
                .defineInRange("max_exp_amount", 1000, 1, 1_000_000);
        builder.pop();

        builder.push("alchemist");
        ALCHEMIST_EXPLOSION_CHANCE = builder
                .comment("One-in-N chance that the alchemist table explodes when brewing. Set to 0 to disable.")
                .defineInRange("explosion_chance", 3, 0, 1000);
        builder.pop();

        builder.push("village_structures");
        builder.comment("How many entries each profession's house gets in the matching village's house pool.",
                "Higher means more common; 0 keeps the house out of that village type entirely.");
        for (String village : VILLAGE_TYPES) {
            for (String profession : List.of("alchemist", "occultist", "horticulturist", "oceanographer")) {
                String key = village + "_" + profession;
                STRUCTURE_WEIGHTS.put(key,
                        builder.defineInRange(key + "_weight", DEFAULT_WEIGHTS.get(profession), 0, 1000));
            }
        }
        builder.pop();

        SPEC = builder.build();
    }

    private VPConfig() {
    }

    public static int expAmount() {
        return EXP_AMOUNT.get();
    }

    public static int maxExpAmount() {
        return MAX_EXP_AMOUNT.get();
    }

    public static int alchemistExplosionChance() {
        return ALCHEMIST_EXPLOSION_CHANCE.get();
    }

    /** Weight for one profession's house in one village type, keyed "<village>_<profession>". */
    public static int structureWeight(String key) {
        ModConfigSpec.IntValue value = STRUCTURE_WEIGHTS.get(key);
        return value == null ? 0 : value.get();
    }

    public static Iterable<String> structureKeys() {
        return STRUCTURE_WEIGHTS.keySet();
    }
}
