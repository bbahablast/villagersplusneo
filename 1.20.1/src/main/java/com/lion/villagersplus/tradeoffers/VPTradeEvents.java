package com.lion.villagersplus.tradeoffers;

import com.lion.villagersplus.VillagersPlus;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Injects the loaded trades when the game builds each profession's trade table. */
@Mod.EventBusSubscriber(modid = VillagersPlus.MOD_ID)
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
