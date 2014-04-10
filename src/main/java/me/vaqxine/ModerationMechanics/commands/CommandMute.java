package me.vaqxine.ModerationMechanics.commands;

import me.vaqxine.ChatMechanics.ChatMechanics;
import me.vaqxine.CommunityMechanics.CommunityMechanics;
import me.vaqxine.ModerationMechanics.ModerationMechanics;
import me.vaqxine.PermissionMechanics.PermissionMechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class CommandMute implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		
		String rank = "";
		if(p != null) {
			rank = PermissionMechanics.getRank(p.getName());
			if(rank == null) { return true; }
			
			if(!rank.equalsIgnoreCase("pmod") && !rank.equalsIgnoreCase("gm") && !(p.isOp())) { return true; }
		}
		
		if(args.length != 2) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/mute <PLAYER> <TIME(in minutes)>");
			}
			return true;
		}
		
		String p_name_2mute = args[0];
		int minutes_to_mute = 0;
		try {
			minutes_to_mute = Integer.parseInt(args[1]);
		} catch(NumberFormatException nfe) {
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Non-Numeric Time. " + ChatColor.RED + "/mute <PLAYER> <TIME(in minutes)>");
			return true;
		}
		
		if(p != null) {
			if(rank.equalsIgnoreCase("pmod") && (minutes_to_mute > 1440)) {
				p.sendMessage(ChatColor.RED + "As a PLAYER MODERATOR, you can only mute players for up to 24 hours. (1440 minutes)");
				return true;
			}
			
			int count = ModerationMechanics.mute_count.get(p.getName());
			if(count >= 20) {
				p.sendMessage(ChatColor.RED + "You have already issued your maximum of " + ChatColor.BOLD + count + ChatColor.RED + " mutes today.");
				return true;
			}
			
			count += 1;
			ModerationMechanics.mute_count.put(p.getName(), count);
		}
		
		//long unmute_time = (System.currentTimeMillis() + ((minutes_to_mute * 60) * 1000));
		
		if(Bukkit.getPlayer(p_name_2mute) != null && Bukkit.getPlayer(p_name_2mute).isOnline()) {
			p_name_2mute = Bukkit.getPlayer(p_name_2mute).getName();
			if(PermissionMechanics.getRank(p_name_2mute).equalsIgnoreCase("gm")) {
				p.sendMessage(ChatColor.RED + "You cannot mute a Game Moderator.");
				return true;
			}
		}
		
		ChatMechanics.mute_list.put(p_name_2mute, (long) minutes_to_mute);
		ChatMechanics.setMuteStateSQL(p_name_2mute);
		
		if(p != null) {
			p.sendMessage(ChatColor.AQUA + "You have issued a " + minutes_to_mute + " minute " + ChatColor.BOLD + "MUTE" + ChatColor.AQUA + " on the user " + ChatColor.BOLD + p_name_2mute);
			p.sendMessage(ChatColor.GRAY + "If this was made in error, type '/unmute " + p_name_2mute + "'");
		} else if(p == null) {
			ModerationMechanics.log.info("[ModerationMechanics] Muted player " + p_name_2mute + " for " + minutes_to_mute + " minute(s).");
		}
		
		String banner = "SYSTEM";
		if(p != null) {
			banner = p.getName();
		}
		
		if(Bukkit.getPlayer(p_name_2mute) != null && Bukkit.getPlayer(p_name_2mute).isOnline()) {
			Player muted = Bukkit.getPlayer(p_name_2mute);
			muted.sendMessage("");
			muted.sendMessage(ChatColor.RED + "You have been " + ChatColor.BOLD + "GLOBALLY MUTED" + ChatColor.RED + " by " + ChatColor.BOLD + banner + ChatColor.RED + " for " + minutes_to_mute + " minute(s).");
			muted.sendMessage("");
		} else if(ModerationMechanics.isPlayerOnline(p_name_2mute)) {
			int server_num = ModerationMechanics.getPlayerServer(p_name_2mute);
			CommunityMechanics.sendPacketCrossServer("@mute@" + p.getName() + "/" + p_name_2mute + ":" + minutes_to_mute, server_num, false);
			//ConnectProtocol.sendResultCrossServer(CommunityMechanics.server_list.get(server_num), "@mute@" + p.getName() + "/" + p_name_2mute + ":" + unmute_time);
		}
		
		return true;
	}
	
}
