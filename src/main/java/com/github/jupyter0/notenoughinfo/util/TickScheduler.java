package com.github.jupyter0.notenoughinfo.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickScheduler {
    boolean ahTrigger = true;
    @SubscribeEvent
    public void Ticker(TickEvent event) {
        if (System.currentTimeMillis() % 15000 < 50) {
            if (ahTrigger) {
                ahTrigger = false;
                AHManager.UpdateAH();
                BazzarManager.CallAPI();
            }
        } else {
            ahTrigger = true;
        }
    }
}
