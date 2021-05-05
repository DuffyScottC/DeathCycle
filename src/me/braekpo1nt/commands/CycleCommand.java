package me.braekpo1nt.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import me.braekpo1nt.deathcycle.Main;


public class CycleCommand implements CommandExecutor {
	
	private Main plugin;
	public int initialCountdownTask;
	public int mainDeathSwapTask;
	
	public CycleCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("deathcycle").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//holds all players to use for Death Cycle
		List<Player> players = new ArrayList();
		
		if (args.length < 2) {
			sender.sendMessage("Error: Please provide at least two players.");
			return false;
		}
		
		//convert argument player names to list of players
		for (int i = 0; i < args.length; i++ ) {
			String playerName = args[i];
			Player player = this.plugin.getServer().getPlayer(playerName);
			players.add(player);
		}
		
		String anouncementMessage = "DeathCycle game starting between ";
		for (int i = 0; i < args.length; i++) {
			anouncementMessage += args[i] + ", ";
		}
		
		this.plugin.getServer().broadcastMessage(anouncementMessage);
		
		BukkitScheduler sched = this.plugin.getServer().getScheduler();
		
		//Initial count down of 10 seconds
		initialCountdownTask = sched.scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			int num = 5;
			
			@Override
			public void run() {
				if (num == 0) { 
					CycleCommand.this.plugin.getServer().broadcastMessage("Game is on! Go!");
					CycleCommand.this.plugin.getServer().getScheduler().cancelTask(initialCountdownTask);
				} else {
					CycleCommand.this.plugin.getServer().broadcastMessage("Grace period: " + Integer.toString(num--));
				}
			}
		}, 20L, 20L); //20 ticks/s
		
		//main 
		sched.scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				mainDeathSwapTask = sched.scheduleSyncRepeatingTask(CycleCommand.this.plugin, new Runnable() {
					
					int num = 10;
					
					@Override
					public void run() {
						
						if (num == 0) {
							
							// Cycle the payers locations to the right
							// [0, 1, 2, 3] becomes [3, 0, 1, 2]
							Location lastLocation = players.get(players.size() - 1).getLocation();
							for (int i = players.size() - 1; i > 0; i--) {
//								players.get(i).teleport(players.get(i - 1).getLocation());
								Player playerA = players.get(i);
								Player playerB = players.get(i - 1);
								playerA.teleport(playerB.getLocation());
								playerA.sendMessage("You were cycled to " + playerB.getName() + "'s position!");
								
								
							}
							players.get(0).teleport(lastLocation);
							players.get(0).sendMessage("You were cycled to " + players.get(players.size() - 1).getName() + "'s position!");
							
							Bukkit.getScheduler().cancelTask(mainDeathSwapTask);
							
						} else {
							CycleCommand.this.plugin.getServer().broadcastMessage("Swapping in " + Integer.toString(num--));
						}
					}
				}, 20L, 20L);
			}
//		}, 6020L, 6000L); //20+10*20 = 220 (wait for 10 second countdown) then 5*60*20 = 6220 (5 minutes) then subtract 10*20 = 6020 delay
		}, 3600L, 3600L); //20+10*20 = 220 (wait for 10 second countdown) then 5*60*20 = 6220 (5 minutes) then subtract 10*20 = 6020 delay
		
		return true;
	}
	
}
