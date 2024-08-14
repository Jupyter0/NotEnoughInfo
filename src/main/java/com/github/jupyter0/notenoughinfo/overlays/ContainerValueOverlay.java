package com.github.jupyter0.notenoughinfo.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.awt.*;

public class ContainerValueOverlay extends Gui {
    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event) {

        if (event.type == RenderGameOverlayEvent.ElementType.TEXT){
            FontRenderer fRenderer = Minecraft.getMinecraft().fontRendererObj;


            fRenderer.drawString("Hello World!", 10, 10, new Color(0, 0, 255, 255).getRGB());

            //Finalise Rendering
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
