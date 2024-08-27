package com.github.jupyter0.notenoughinfo.overlays;

import com.github.jupyter0.notenoughinfo.util.AHManager;
import com.github.jupyter0.notenoughinfo.util.BazzarManager;
import com.github.jupyter0.notenoughinfo.util.NEIUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.threadpool.Arrays;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TooltipOverlay {

    public static String ItemID;
    public static String displayName;
    public static boolean tooltipToggle = false;
    FontRenderer fRenderer = Minecraft.getMinecraft().fontRendererObj;
    public static NBTTagCompound nbtData = new NBTTagCompound();
    @SubscribeEvent
    public void TooltipOverlayEvent(GuiScreenEvent.BackgroundDrawnEvent event) {
        NumberFormat jf = java.text.DecimalFormat.getInstance();
        jf.setGroupingUsed(true);
        int gameWidth = event.gui.width;
        int gameHeight = event.gui.height;

        if (tooltipToggle) {
            long totalPrice = 0;
            fRenderer.drawString("§2" + displayName, 10, 10, 0);

            try {
                totalPrice += AHManager.lBIN.get(ItemID).getAsLong();
            } catch (NullPointerException ignored) {}
            int i = 0;

            //Resolve Enchants
            List<String> enchantList = new ArrayList<>();
            NBTTagCompound enchants = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getCompoundTag("enchantments");
            for (String enchant:enchants.getKeySet()) {
                int level = enchants.getInteger(enchant);
                String enchantInternalName = "ENCHANTMENT_" + enchant.toUpperCase() + "_" + level;

                enchantList.add(enchantInternalName);
            }
            if (!enchantList.isEmpty()) {
                i++;
                fRenderer.drawString("§3" + "Enchantments", 10, 10+(9*i), 0);
            }
            for (String enchant:enchantList) {
                try {
                    i++;
                    long enchantPrice = NEIUtil.ResolveEnchantPrice(enchant);

                    String readableEnchant = NEIUtil.ConvertToRomanNumerals(NEIUtil.capitalizeWords(enchant.replaceAll("ENCHANTMENT_", "").toLowerCase().replaceAll("_", " ")));

                    fRenderer.drawString("§3" + readableEnchant + ": §6" + jf.format(enchantPrice), 20, 10+(9*i), 0);
                    totalPrice += enchantPrice;
                } catch (NullPointerException e) {
                    continue;
                }
            }

            //Resolve Reforge
            String reforge = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getString("modifier");
            if (!reforge.isEmpty()) {
                String source = NEIUtil.ResolveReforgePrice(reforge).getSource();
                long price = NEIUtil.ResolveReforgePrice(reforge).getPrice();
                if (source.equals("BLACKSMITH")) {
                    i += 2;
                    fRenderer.drawString("§3" + NEIUtil.capitalizeWords(reforge) + " Reforge" + ": §6" + price + " §7(Blacksmith Reforge)", 10, 10 + (9 * i), 0);
                }
                if (source.equals("WIP")) {
                    i += 2;
                    fRenderer.drawString("§3" + "Reforge Prices are a WIP", 10, 10 + (9 * i), 0);
                }
            }

            //Resolve Gemstones


            //Resolve Scrolls
            NBTBase scrolls = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getTag("ability_scroll");

            List<String> scrollList = NEIUtil.NBTBaseToList(scrolls);

            if (!scrollList.isEmpty()) {
                i += 2;
                fRenderer.drawString("§3" + "Scrolls", 10, 10 + (9 * i), 0);
                for (String scroll:scrollList) {
                    i++;
                    long price = BazzarManager.qbPrice.get(scroll).getAsLong();
                    totalPrice += price;
                    String scrollName = NEIUtil.capitalizeWords(scroll.replaceAll("_SCROLL", "").replaceAll("_", " ").toLowerCase(Locale.ROOT));
                    fRenderer.drawString("§3" + scrollName + ": §6" + jf.format(price), 20, 10 + (9 * i), 0);
                }
            }

            //Art of War/Peace
            boolean artOfWar = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getInteger("art_of_war_count") == 1;
            boolean artOfPeace = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getInteger("artOfPeaceApplied") == 1;
            if (artOfPeace || artOfWar) {
                i++;
            }
            if (artOfWar) {
                i++;
                long price = BazzarManager.qbPrice.get("THE_ART_OF_WAR").getAsLong();
                totalPrice += price;
                fRenderer.drawString("§3" + "Art of War: §6" + jf.format(price), 10, 10 + (9 * i), 0);
            }
            if (artOfPeace) {
                i++;
                long price = BazzarManager.qbPrice.get("THE_ART_OF_PEACE").getAsLong();
                totalPrice += price;
                fRenderer.drawString("§3" + "Art of Peace: §6" + jf.format(price), 10, 10 + (9 * i), 0);
            }

            //Resolve Potato Books
            int potatoBookCount = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getInteger("hot_potato_count");
            if (potatoBookCount > 0) {
                int hotBookCount;
                int fumingBookCount;
                if (potatoBookCount > 10) {
                    fumingBookCount = potatoBookCount-10;
                    totalPrice += BazzarManager.qbPrice.get("FUMING_POTATO_BOOK").getAsLong() * fumingBookCount;
                    hotBookCount = potatoBookCount-fumingBookCount;
                } else {
                    hotBookCount = potatoBookCount;
                    fumingBookCount = 0;
                }
                totalPrice += BazzarManager.qbPrice.get("HOT_POTATO_BOOK").getAsLong() * hotBookCount;
                i += 2;
                fRenderer.drawString("§3" + "Hot Potato Book §e(" + hotBookCount + "x)" + "§3: §6" + jf.format(BazzarManager.qbPrice.get("HOT_POTATO_BOOK").getAsLong() * hotBookCount), 10, 10+(9*i), 0);
                i++;
                fRenderer.drawString("§3" + "Fuming Potato Book §e(" + fumingBookCount + "x)" + "§3: §6" + jf.format(BazzarManager.qbPrice.get("FUMING_POTATO_BOOK").getAsLong() * fumingBookCount), 10, 10+(9*i), 0);
            }

            //Resolve Transmission Tuner/Etherwarp
            int tuners = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getInteger("tuned_transmission");
            boolean etherwarp = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getByte("ethermerge") == 1;
            if (tuners > 0 || etherwarp) {
                i++;
            }
            if (tuners > 0) {
                long price = BazzarManager.qbPrice.get("TRANSMISSION_TUNER").getAsLong();

                totalPrice += price * tuners;

                i++;
                fRenderer.drawString("§3" + "Transmission Tuner §e(" + tuners + "x)" + "§3: §6" + jf.format(price * tuners), 10, 10+(9*i), 0);
            }
            if (etherwarp) {
                try {
                    long mergerPrice = AHManager.lBIN.get("ETHERWARP_MERGER").getAsLong();
                    long conduitPrice = AHManager.lBIN.get("ETHERWARP_CONDUIT").getAsLong();
                    long combinedPrice = mergerPrice + conduitPrice;

                    totalPrice += combinedPrice;

                    i++;
                    fRenderer.drawString("§3" + "Etherwarp: §6" + jf.format(combinedPrice), 10, 10 + (9 * i), 0);
                } catch (NullPointerException e) {
                    i++;
                    fRenderer.drawString("§3Couldent resolve the price of Etherwarp", 10, 10 + (9 * i), 0);
                }
            }

            //Resolve Stars
            int stars = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getInteger("upgrade_level");
            if (stars > 5) {
                int masterModeStars = stars - 5;

                long masterStarPrice = 0L;
                for (int j = 1; j <= masterModeStars; j++) {
                    if (j == 1) {
                        masterStarPrice += BazzarManager.qbPrice.get("FIRST_MASTER_STAR").getAsLong();
                    } else if (j == 2) {
                        masterStarPrice += BazzarManager.qbPrice.get("SECOND_MASTER_STAR").getAsLong();
                    } else if (j == 3) {
                        masterStarPrice += BazzarManager.qbPrice.get("THIRD_MASTER_STAR").getAsLong();
                    } else if (j == 4) {
                        masterStarPrice += BazzarManager.qbPrice.get("FOURTH_MASTER_STAR").getAsLong();
                    } else if (j == 5) {
                        masterStarPrice += BazzarManager.qbPrice.get("FIFTH_MASTER_STAR").getAsLong();
                    }
                }

                totalPrice += masterStarPrice;
                i += 2;
                fRenderer.drawString("§3" + "Master Stars §e(" + masterModeStars + "x)" + "§3: §6" + jf.format(masterStarPrice), 10, 10+(9*i), 0);
            }

            //Resolve Recom
            boolean recomed = nbtData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getInteger("rarity_upgrades") == 1;
            if (recomed) {
                long recomPrice = BazzarManager.qbPrice.get("RECOMBOBULATOR_3000").getAsLong();
                totalPrice += recomPrice;
                i += 2;
                fRenderer.drawString("§3" + "Recombobulated" + ": §6" + jf.format(recomPrice), 10, 10+(9*i), 0);
            }

            fRenderer.drawString("§2Total Price: " + jf.format(totalPrice), 10, 10+(9*(i+1)), 0);

            //Finalise Rendering
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }

        tooltipToggle = false;
    }
}
