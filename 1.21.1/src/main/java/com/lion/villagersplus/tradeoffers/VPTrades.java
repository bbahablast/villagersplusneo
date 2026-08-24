package com.lion.villagersplus.tradeoffers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lion.villagersplus.VillagersPlus;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Loads the bundled villager trade definitions.
 *
 * <p>The original mod shipped these as JSON and parsed them with its own registry of
 * trade adapters. The three types it uses for these professions ({@code sell_item},
 * {@code buy_item} and {@code process_item}) all reduce to the same shape — up to two
 * input stacks and one output stack — so a single listing implementation covers them.
 */
public final class VPTrades {

    /** Villager career levels, matching the keys used in the JSON files. */
    private static final Map<String, Integer> LEVELS = Map.of(
            "novice", 1, "apprentice", 2, "journeyman", 3, "expert", 4, "master", 5);

    private static final String[] FILES = {"horticulturist", "oceanographer", "occultist", "alchemist"};

    /** profession name (e.g. {@code villagersplus:occultist}) -> level -> listings. */
    private static final Map<String, Map<Integer, List<VillagerTrades.ItemListing>>> TRADES = new HashMap<>();

    private VPTrades() {
    }

    public static Map<Integer, List<VillagerTrades.ItemListing>> forProfession(String professionName) {
        return TRADES.getOrDefault(professionName, Map.of());
    }

    /** Parses the bundled trade files. Safe to call once during mod setup. */
    public static void load() {
        TRADES.clear();
        for (String name : FILES) {
            String path = "/data/" + VillagersPlus.MOD_ID + "/default_villager_trades/" + name + ".json";
            try (InputStream in = VPTrades.class.getResourceAsStream(path)) {
                if (in == null) {
                    VillagersPlus.LOGGER.error("Missing bundled trade file {}", path);
                    continue;
                }
                JsonObject root = JsonParser
                        .parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .getAsJsonObject();
                parseProfession(root);
            } catch (Exception e) {
                VillagersPlus.LOGGER.error("Failed to read trades from {}", path, e);
            }
        }
        int count = TRADES.values().stream()
                .flatMap(m -> m.values().stream())
                .mapToInt(List::size)
                .sum();
        VillagersPlus.LOGGER.info("Loaded {} villager trades for {} professions", count, TRADES.size());
    }

    private static void parseProfession(JsonObject root) {
        String profession = root.get("profession").getAsString();
        Map<Integer, List<VillagerTrades.ItemListing>> byLevel = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("trades").entrySet()) {
            Integer level = LEVELS.get(entry.getKey());
            if (level == null) {
                VillagersPlus.LOGGER.warn("Unknown trade level '{}' in {}", entry.getKey(), profession);
                continue;
            }

            JsonArray array = entry.getValue().getAsJsonArray();
            List<VillagerTrades.ItemListing> listings = new ArrayList<>();
            for (JsonElement element : array) {
                VillagerTrades.ItemListing listing = parseTrade(element.getAsJsonObject(), profession);
                if (listing != null) {
                    listings.add(listing);
                }
            }
            if (!listings.isEmpty()) {
                byLevel.put(level, listings);
            }
        }
        TRADES.put(profession, byLevel);
    }

    private static VillagerTrades.ItemListing parseTrade(JsonObject trade, String profession) {
        String type = trade.get("type").getAsString();
        int maxUses = trade.has("max_uses") ? trade.get("max_uses").getAsInt() : 12;
        int xp = trade.has("villager_experience") ? trade.get("villager_experience").getAsInt() : 1;
        float multiplier = trade.has("price_multiplier") ? trade.get("price_multiplier").getAsFloat() : 0.05F;

        ItemCost costA;
        Optional<ItemCost> costB = Optional.empty();
        ItemStack result;

        switch (type) {
            case "villagersplus:sell_item" -> {
                costA = cost(trade.getAsJsonObject("priceIn"));
                result = stack(trade.getAsJsonObject("sell"));
            }
            case "villagersplus:buy_item" -> {
                costA = cost(trade.getAsJsonObject("buy"));
                result = stack(trade.getAsJsonObject("reward"));
            }
            case "villagersplus:process_item" -> {
                costA = cost(trade.getAsJsonObject("priceIn"));
                costB = Optional.of(cost(trade.getAsJsonObject("convertible")));
                result = stack(trade.getAsJsonObject("sell"));
            }
            case "villagersplus:sell_potion" -> {
                costA = cost(trade.getAsJsonObject("priceIn"));
                costB = Optional.of(cost(trade.getAsJsonObject("convertible")));
                result = stack(trade.getAsJsonObject("sell"));
                if (costA == null || result.isEmpty()) {
                    return null;
                }
                // The potion itself is rolled per offer, so each villager sells something different.
                final ItemCost potionCostA = costA;
                final Optional<ItemCost> potionCostB = costB;
                final ItemStack bottle = result;
                return (trader, random) -> new MerchantOffer(
                        potionCostA, potionCostB, randomPotion(bottle, random), maxUses, xp, multiplier);
            }
            default -> {
                VillagersPlus.LOGGER.warn("Unsupported trade type '{}' in {}, skipping", type, profession);
                return null;
            }
        }

        if (costA == null || result == null || result.isEmpty()) {
            VillagersPlus.LOGGER.warn("Skipping trade with unknown items in {}: {}", profession, trade);
            return null;
        }

        final ItemCost finalCostA = costA;
        final Optional<ItemCost> finalCostB = costB;
        final ItemStack finalResult = result;
        return (trader, random) ->
                new MerchantOffer(finalCostA, finalCostB, finalResult.copy(), maxUses, xp, multiplier);
    }

    /** Copies the bottle type from the trade file and gives it a random potion. */
    private static ItemStack randomPotion(ItemStack bottle, net.minecraft.util.RandomSource random) {
        ItemStack stack = bottle.copy();
        Holder<Potion> potion = BuiltInRegistries.POTION.getRandom(random)
                .map(holder -> (Holder<Potion>) holder)
                .orElse(null);
        if (potion != null) {
            stack.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new PotionContents(potion));
        }
        return stack;
    }

    private static ItemCost cost(JsonObject json) {
        Item item = item(json);
        return item == null ? null : new ItemCost(item, count(json));
    }

    private static ItemStack stack(JsonObject json) {
        Item item = item(json);
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count(json));
    }

    private static int count(JsonObject json) {
        return json.has("count") ? json.get("count").getAsInt() : 1;
    }

    /** Item ids in the trade files may omit the {@code minecraft:} namespace. */
    private static Item item(JsonObject json) {
        ResourceLocation id = ResourceLocation.tryParse(json.get("item").getAsString());
        if (id == null) {
            return null;
        }
        return BuiltInRegistries.ITEM.getOptional(id).orElse(null);
    }
}
