package com.github.jupyter0.notenoughinfo.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;

public class BazzarManager {

    public static JsonObject qbPrice = new JsonObject();

    public static void CallAPI() {
        APIUtil.newAnonymousHypixelApiRequest("skyblock/bazaar")
                .requestJson()
                .thenAccept(jsonObject -> {
                    if (jsonObject == null) return;
                    if (jsonObject.get("success").getAsBoolean()) {
                        JsonObject products = jsonObject.getAsJsonObject("products");

                        Set<Map.Entry<String, JsonElement>> tableKeys = products.entrySet();
                        for (Map.Entry<String, JsonElement> entry : products.entrySet()) {
                            float price = products.get(entry.getKey()).getAsJsonObject().get("quick_status").getAsJsonObject().get("buyPrice").getAsFloat();
                            qbPrice.addProperty(entry.getKey(), Math.round(price));
                        }
                    }
                });
    }
}
