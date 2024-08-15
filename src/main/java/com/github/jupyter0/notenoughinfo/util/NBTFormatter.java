package com.github.jupyter0.notenoughinfo.util;

import net.minecraft.nbt.NBTTagCompound;

public class NBTFormatter {
    public static String GetName(NBTTagCompound nbtData) {
        String formattedName = nbtData.getCompoundTag("tag").getCompoundTag("display").getString("Name");

        return CleanTextColor(formattedName);
    }

    public static String CleanTextColor(String formattedString) {
        return formattedString.replaceAll("(?i)\\u00A7.", "");
    }
}
