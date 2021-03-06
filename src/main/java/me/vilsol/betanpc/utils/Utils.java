package me.vilsol.betanpc.utils;

import net.minecraft.server.v1_8_R1.NBTTagCompound;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class Utils {
	
	public static String NPC = "Beta Vendor: " + ChatColor.YELLOW;
	
	public static boolean isInteger(String s){
		try{
			Integer.parseInt(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
    public static ItemStack removePotionLore(ItemStack i) {
        if (i == null) {
            return i;
        }
    	net.minecraft.server.v1_8_R1.ItemStack x = CraftItemStack.asNMSCopy(i);
        try {
            net.minecraft.server.v1_8_R1.NBTTagList cpe = new net.minecraft.server.v1_8_R1.NBTTagList();
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("Id", (byte) 6);
            cpe.add(tag);
            NBTTagCompound xtag = x.getTag();
            xtag.set("CustomPotionEffects", cpe);
            x.setTag(xtag);
        } catch (NullPointerException npe) {
            return CraftItemStack.asBukkitCopy(x);
        }
        return CraftItemStack.asBukkitCopy(x);
    }
	
}
