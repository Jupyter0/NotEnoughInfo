package com.github.jupyter0.notenoughinfo.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class AHManager {
    public static JsonObject lBIN = new JsonObject();

    public static LinkedList<Integer> pagesToDownload = null;

    public static void UpdateAH() {

        if (pagesToDownload == null) {
            ParseAPIPage(0);
        }

        while (pagesToDownload != null && pagesToDownload.size() > 0) {
            try {
                int page = pagesToDownload.pop();
                ParseAPIPage(page);
            } catch (NoSuchElementException e) {
                return;
            }
        }
    }

    public static void ParseAPIPage(int page) {
        APIUtil.newAnonymousHypixelApiRequest("skyblock/auctions?page=" + page)
                .requestJson()
                .thenAccept(jsonObject -> {
                    if (jsonObject == null) return;
                    if (jsonObject.get("success").getAsBoolean()) {

                        if (pagesToDownload == null) {
                            int totalPages = jsonObject.get("totalPages").getAsInt();
                            pagesToDownload = new LinkedList<>();
                            for (int i = 0; i < totalPages; i++) {
                                pagesToDownload.add(i);
                            }
                        }

                        JsonArray auctions = jsonObject.getAsJsonArray("auctions");

                        if (auctions != null && auctions.size() > 0) {
                            for (JsonElement auction:auctions) {
                                JsonObject auctionObject = auction.getAsJsonObject();


                                String itemBytes = auctionObject.get("item_bytes").getAsString();

                                NBTTagCompound itemTag;

                                try {
                                    itemTag = CompressedStreamTools.readCompressed(
                                            new ByteArrayInputStream(Base64.getDecoder().decode(itemBytes))
                                    );
                                } catch (IOException e) {
                                    return;
                                }

                                NBTTagCompound itemNBT = itemTag.getTagList("i", 10).getCompoundTagAt(0);
                                String internalName = NBTUtil.ResolveInternalNameFromNBT(itemNBT);

                                HashSet<String> keys = new HashSet<>();

                                if (lBIN != null) {
                                    for (Map.Entry<String, JsonElement> entry : lBIN.entrySet()) {
                                        keys.add(entry.getKey());
                                    }
                                }
                                if (auctionObject.get("bin").getAsBoolean()) {
                                    long price = auctionObject.get("starting_bid").getAsLong();
                                    if (!keys.contains(internalName)) {
                                        lBIN.addProperty(internalName, price);
                                    } else {
                                        if (price < lBIN.get(internalName).getAsLong()) {
                                            lBIN.remove(internalName);
                                            lBIN.addProperty(internalName, price);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        pagesToDownload.addLast(page);
                    }
                })
                .handle((ignored, ex) -> {
                    if (ex != null) {
                        pagesToDownload.addLast(page);
                    }
                    return null;
                });
    }
}