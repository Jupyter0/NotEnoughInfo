package com.github.jupyter0.notenoughinfo.util;

import net.minecraft.nbt.NBTTagCompound;

public class NBTUtil {
    public static String ResolveInternalNameFromNBT(NBTTagCompound itemNBT) {

        String internalID = itemNBT.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getString("id");

        if (internalID != null) return internalID;
        return "";
    }
}
