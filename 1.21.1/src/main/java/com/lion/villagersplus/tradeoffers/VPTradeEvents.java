package com.lion.villagersplus.tradeoffers;

import com.lion.villagersplus.VillagersPlus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/** Injects the loaded trades when the game builds each profession's trade table. */
@EventBusSubscriber(modid = VillagersPlus.MOD_ID)
public final class VPTradeEvents {

    private VPTradeEvents() {
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        var trades = VPTrades.forProfession(event.getType().name());
        if (trades.isEmpty()) {
            return;
        }
        trades.forEach((level, listings) -> {
            var target = event.getTrades().get(level.intValue());
            if (target != null) {
                target.addAll(listings);
            }
        });
    }
}
