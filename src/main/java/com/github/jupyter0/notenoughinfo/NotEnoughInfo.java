package com.github.jupyter0.notenoughinfo;

import com.github.jupyter0.notenoughinfo.events.TooltipEvents;
import com.github.jupyter0.notenoughinfo.overlays.TooltipOverlay;
import com.github.jupyter0.notenoughinfo.util.AHManager;
import com.github.jupyter0.notenoughinfo.util.BazzarManager;
import com.github.jupyter0.notenoughinfo.util.TickScheduler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "notenoughinfo", useMetadata=true, version = "v0.1.0")
public class NotEnoughInfo {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TooltipOverlay());
        MinecraftForge.EVENT_BUS.register(new TooltipEvents());
        MinecraftForge.EVENT_BUS.register(new TickScheduler());

        AHManager.UpdateAH();
        BazzarManager.CallAPI();
    }
}
