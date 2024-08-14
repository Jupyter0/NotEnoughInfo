package com.github.jupyter0.notenoughinfo.events;

import com.github.jupyter0.notenoughinfo.overlays.TooltipOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipEvents {
    @SubscribeEvent
    public void TooltipEvent(ItemTooltipEvent event) {
        NBTTagCompound nbtData = event.itemStack.serializeNBT();

        TooltipOverlay.tooltipToggle = true;

        event.toolTip.add(nbtData.getString("id").replace("minecraft:", ""));
        TooltipOverlay.ItemID = nbtData.getString("id").replace("minecraft:", "");
    }
}
