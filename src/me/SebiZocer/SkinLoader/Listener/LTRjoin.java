package me.SebiZocer.SkinLoader.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.SebiZocer.SkinLoader.Methods.ConfigManager;
import me.SebiZocer.SkinLoader.Methods.Classes.User;
import me.SebiZocer.SkinLoader.Methods.Management.Manager;
import me.SebiZocer.SkinLoader.Methods.Management.ProfileManager;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLNick;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLProfile;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLSkin;

public class LTRjoin implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e){
		User p = User.getUser(e.getPlayer());
		MySQLSkin.createUser(p.getPlayer());
		MySQLProfile.createProfile(ProfileManager.getProfile(e.getPlayer().getUniqueId()));
		MySQLNick.createUser(p.getUniqueId());
		Manager.renickCheck(p.getPlayer());
		boolean skin = !MySQLSkin.getCurrentSkin(p.getUniqueId()).getName().equals(p.getRealname());
		if(ConfigManager.getConfigManager("config").getYaml().getBoolean("autonick") == true){
			if(p.getAutonick() == false){
				if(skin){
					p.loadSkin();
				}
			}
		} else if(skin){
			p.loadSkin();
		}
	}
}