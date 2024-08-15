package com.github.jupyter0.notenoughinfo.events;

import com.github.jupyter0.notenoughinfo.overlays.TooltipOverlay;
import com.github.jupyter0.notenoughinfo.util.AHManager;
import com.github.jupyter0.notenoughinfo.util.NBTUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipEvents {
    @SubscribeEvent
    public void TooltipEvent(ItemTooltipEvent event) {
        NBTTagCompound nbtData = event.itemStack.serializeNBT();

        String internalID = NBTUtil.ResolveInternalNameFromNBT(nbtData);

        if (internalID.equals("")) return;

        if (AHManager.lBIN.get(internalID) != null) {

            long itemLBIN = AHManager.lBIN.get(internalID).getAsLong();

            if (itemLBIN > 0) {
                String suffix = "";
                float adjustedValue = itemLBIN;
                if (itemLBIN > 1_000_000_000_000L) {
                    //Trillion
                    suffix = "T";
                    adjustedValue /= 1_000_000_000_000L;
                } else if (itemLBIN > 1_000_000_000L) {
                    //Billion
                    suffix = "B";
                    adjustedValue /= 1_000_000_000L;
                } else if (itemLBIN > 1_000_000L) {
                    //Million
                    suffix = "M";
                    adjustedValue /= 1_000_000L;
                } else if (itemLBIN > 10_000L) {
                    //Thousand
                    suffix = "K";
                    adjustedValue /= 1_000L;
                }


                event.toolTip.add("§2LBIN §e" + Math.round(adjustedValue*10)/10 + suffix);
            }
        }
        TooltipOverlay.tooltipToggle = true;
        TooltipOverlay.ItemID = internalID;
        event.toolTip.add(internalID);
    }
}
