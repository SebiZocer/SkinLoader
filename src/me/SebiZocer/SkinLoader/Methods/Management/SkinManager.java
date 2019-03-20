package me.SebiZocer.SkinLoader.Methods.Management;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.SebiZocer.SkinLoader.Methods.Classes.Info;
import me.SebiZocer.SkinLoader.Methods.Classes.Skin;
import me.SebiZocer.SkinLoader.Methods.Classes.User;
import me.SebiZocer.SkinLoader.Methods.Management.Data.Database;
import me.SebiZocer.SkinLoader.Methods.Management.Data.Downloader;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLProfile;

public class SkinManager {
	
	/**
	 * Returns a random Skin from Database. You can say if you need an skin which name is not used at the time.
	 * 
	 * @param unused Should the skin not in use at the time
	 */
	public static Skin getRandomSkin(boolean unused){
		List<Skin> skins = Database.getSkins();
		if(unused){
			List<Skin> blacklist = new ArrayList<>();
			for(Skin skin : skins){
				for(Player all : Bukkit.getOnlinePlayers()){
					User p = User.getUser(all);
					if(p.getSkin().getName().equals(skin.getName())){
						blacklist.add(skin);
					}
				}
			}
			if(blacklist.size() == skins.size()){
				blacklist.remove(0);
			}
			skins.removeAll(blacklist);
		}
		return skins.get(new Random().nextInt(skins.size()));
	}
	
	/**
	 * Returns the Skin by name. First checks the Database, then the MySQL and at least the Mojang-API for a skin with this name.
	 * 
	 * @param name The name of the requested Skin
	 */
	public static Skin getSkin(String name){
		try {
			name = Manager.getRightChase(name);
        	Info.custom("Getting Skin of §6" + name + "§3...");
        	Info.custom("Loading Skin from §6Database§3...");
        	for(Skin skin : Database.getSkins()){
        		if(skin.getName().equals(name)){
    				Info.success();
    				return skin;
        		}
        	}
        	Info.fail();
			Skin skin = null;
			if(skin == null){
				Info.custom("Loading Skin from §6MySQL§3...");
				skin = MySQLProfile.getSkin(name);
			}
			if(skin == null){
				Info.fail();
        		Info.custom("Loading Skin from §6Downloader§3...");
				skin = Downloader.getProfile(name).getSkin();
			}
			if(skin != null){
				Info.success();
				Database.addSkin(name, skin);
				return skin;
			} else {
				Info.fail();
			}
		} catch(Exception e){
			Info.error();
			if(Info.active){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Returns the profile by uuid. First checks the Database, then the MySQL and at least the Mojang-API for an Skin with this uuid.
	 * 
	 * @param uuid The uuid of the requested Skin
	 */
	public static Skin getSkin(UUID uuid){
		try {
        	Info.custom("Getting Skin of §6" + uuid + "§3...");
        	Info.custom("Loading Skin from §6Database§3...");
        	for(Skin skin : Database.getSkins()){
        		if(skin.getName().equals(Manager.getName(uuid))){
    				Info.success();
    				return skin;
        		}
        	}
        	Info.fail();
			Skin skin = null;
			if(skin == null){
				Info.custom("Loading Skin from §6MySQL§3...");
				skin = MySQLProfile.getSkin(uuid);
			}
			if(skin == null){
				Info.fail();
        		Info.custom("Loading Skin from §6Downloader§3...");
				skin = Downloader.getProfile(uuid).getSkin();
			}
			if(skin != null){
				Info.success();
				Database.addSkin(Manager.getName(uuid), skin);
				return skin;
			} else {
				Info.fail();
			}
		} catch(Exception e){
			Info.error();
			if(Info.active){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Checks if an Skin with this name exists. First checks the Database, then the MySQL and at least the Mojang-API for an Skin with that name.
	 * 
	 * @return Is an Skin with this name existing
	 */
	public static boolean skinExists(String name){
		try {
			name = Manager.getRightChase(name);
        	Info.custom("Checking if Skin of §6" + name + " §3exists...");
        	Info.custom("Loading Skin from §6Database§3...");
			for(Skin skin : Database.getSkins()){
				if(skin.getName().equals(name)){
					Info.success();
					return true;
				}
			}
			Info.fail();
			Skin skin = null;
			if(skin == null){
				Info.custom("Loading Skin from §6MySQL§3...");
				skin = MySQLProfile.getSkin(name);
			}
			if(skin == null){
				Info.fail();
        		Info.custom("Loading Skin from §6Downloader§3...");
				skin = Downloader.getProfile(name).getSkin();
			}
			if(skin != null){
				Info.success();
				Database.addSkin(name, skin);
				return true;
			} else {
				Info.fail();
			}
		} catch(Exception e){
			Info.error();
			if(Info.active){
				e.printStackTrace();
			}
		}
		return false;
	}
}