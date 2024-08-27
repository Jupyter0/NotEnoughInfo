package com.github.jupyter0.notenoughinfo.util;

import com.github.jupyter0.notenoughinfo.overlays.TooltipOverlay;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import scala.Int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class NEIUtil {

    public static String[] stackingEnchants = {"ENCHANTMENT_EXPERTISE_1", "ENCHANTMENT_COMPACT_1", "ENCHANTMENT_CULTIVATING_1", "ENCHANTMENT_CHAMPION_1", "ENCHANTMENT_HECATOMB_1", "ENCHANTMENT_TOXOPHILITE_1"};
    public static String[] blacksmithReforges = {"epic", "fair", "fast", "gentle", "heroic", "legendary", "odd_sword", "sharp", "spicy",
            "awkward", "deadly", "fine", "grand", "hasty", "neat", "rapid", "rich_bow", "unreal",
            "clean", "fierce", "heavy", "light", "mythic", "pure", "titanic", "smart", "wise",
            "stained", "menacing", "hefty", "soft", "honored", "blended", "astute", "colossal", "brilliant",
            "unyielding", "prospector", "excellent", "sturdy", "fortunate",
            "great", "rugged", "lush", "lumberjack", "double_bit",
            "robust", "zooming", "peasant", "green_thumb"};

    public static class ReforgePriceSourcePair {
        private final long price;
        private final String source;

        public ReforgePriceSourcePair(long price, String source) {
            this.price = price;
            this.source = source;
        }

        public long getPrice() {
            return price;
        }
        public String getSource() {
            return source;
        }
    }

    public static String capitalizeWords(String input) {
        String[] words = input.split("\\s");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(Character.toTitleCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return result.toString().trim();
    }

    public static String ConvertToRomanNumerals(String input) {
        return input
                .replaceAll("10", "X")
                .replaceAll("9", "IX")
                .replaceAll("8", "VIII")
                .replaceAll("7", "VII")
                .replaceAll("6", "VI")
                .replaceAll("5", "V")
                .replaceAll("4", "IV")
                .replaceAll("3", "III")
                .replaceAll("2", "II")
                .replaceAll("1", "I");
    }

    public static long CalculateValue(NBTTagCompound itemData) {
        String internalID = NBTUtil.ResolveInternalNameFromNBT(itemData);
        List<String> enchants = new ArrayList<>();

        NBTTagCompound enchantTag = itemData.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getCompoundTag("enchantments");
        for (String enchant:enchantTag.getKeySet()) {
            int level = enchantTag.getInteger(enchant);
            String enchantInternalName = "ENCHANTMENT_" + enchant.toUpperCase() + "_" + level;
            enchants.add(enchantInternalName);
        }

        //Adding everything together
        long price = 0;

        price += AHManager.lBIN.get(internalID).getAsLong();

        for (String internalEnchantName:enchants) {
            price += ResolveEnchantPrice(internalEnchantName);
        }

        return price;
    }

    public static long ResolveEnchantPrice(String internalEnchantID) {
        long price = BazzarManager.qbPrice.get(internalEnchantID).getAsLong();

        if (price == 0) {
            String level = internalEnchantID.substring(internalEnchantID.length()-2).replaceAll("_", "");
            String lvl1EnchantName = internalEnchantID.replaceAll("_\\d\\d", "_1").replaceAll("_\\d", "_1");

            if (Arrays.asList(stackingEnchants).contains(lvl1EnchantName)) {
                return BazzarManager.qbPrice.get(lvl1EnchantName).getAsLong();
            }

            int multiplier = (int) Math.pow(2, Integer.parseInt(level) -1);

            return BazzarManager.qbPrice.get(lvl1EnchantName).getAsLong() * multiplier;

        } else {
            return price;
        }
    }

    public static ReforgePriceSourcePair ResolveReforgePrice(String modifier) {
        if (Arrays.asList(blacksmithReforges).contains(modifier)) {
            return new ReforgePriceSourcePair(0L, "BLACKSMITH");
        }
        return new ReforgePriceSourcePair(0L, "WIP");
    }

    public static List<String> NBTBaseToList(NBTBase base) {
        if (base == null) return new ArrayList<>();
        String baseString = base.toString().replaceAll("\\d:|\\[|]|\"", "");
        String[] baseArray = baseString.split(",");
        return Arrays.asList(baseArray);
    }
}
