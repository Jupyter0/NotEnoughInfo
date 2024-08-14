package com.github.jupyter0.notenoughinfo.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class TooltipOverlay {

    public static String ItemID;
    public static boolean tooltipToggle = false;
    @SubscribeEvent
    public void TooltipOverlayEvent(RenderGameOverlayEvent event) {

        if (event.type == RenderGameOverlayEvent.ElementType.TEXT){
            if (tooltipToggle) {
                FontRenderer fRenderer = Minecraft.getMinecraft().fontRendererObj;

                fRenderer.drawString(ItemID, 10, 10, new Color(0, 0, 255, 255).getRGB());

                //Finalise Rendering
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }

            tooltipToggle = false;
        }
    }
}
