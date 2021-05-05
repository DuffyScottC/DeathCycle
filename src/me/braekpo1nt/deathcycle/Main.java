package me.braekpo1nt.deathcycle;

import org.bukkit.plugin.java.JavaPlugin;

import me.braekpo1nt.commands.EndCommand;
import me.braekpo1nt.commands.CycleCommand;


public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		new CycleCommand(this);
		new EndCommand(this);
	}
	
}
