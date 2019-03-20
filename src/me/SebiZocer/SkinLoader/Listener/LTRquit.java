package me.SebiZocer.SkinLoader.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.SebiZocer.SkinLoader.Methods.Classes.User;

public class LTRquit implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		User.onQuit(e.getPlayer());
	}
}