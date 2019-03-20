package me.SebiZocer.SkinLoader.Methods.Management;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.SebiZocer.SkinLoader.Methods.Classes.Info;
import me.SebiZocer.SkinLoader.Methods.Classes.Profile;
import me.SebiZocer.SkinLoader.Methods.Classes.User;
import me.SebiZocer.SkinLoader.Methods.Management.Data.Database;
import me.SebiZocer.SkinLoader.Methods.Management.Data.Downloader;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLProfile;

public class ProfileManager {
	
	/**
	 * Returns a random profile from Database. You can say if you need an profile which name is not used at the time.
	 * 
	 * @param unused Should the profile not in use at the time
	 */
	public static Profile getRandomProfile(boolean unused){
		List<Profile> profiles = Database.getProfiles();
		if(unused){
			List<Profile> blacklist = new ArrayList<>();
			for(Profile profile : profiles){
				for(Player all : Bukkit.getOnlinePlayers()){
					User p = User.getUser(all);
					if(p.getName().equals(profile.getName()) || p.getRealname().equals(profile.getName())){
						blacklist.add(profile);
					}
				}
			}
			if(blacklist.size() == profiles.size()){
				blacklist.remove(0);
			}
			profiles.removeAll(blacklist);
		}
		return profiles.get(new Random().nextInt(profiles.size()));
	}
	
	/**
	 * Returns the profile by name. First checks the Database, then the MySQL and at least the Mojang-API for an profile with this name.
	 * 
	 * @param name The name of the requested Profile
	 */
	public static Profile getProfile(String name){
		try {
			if(name == null){
				throw new NullPointerException();
			}
			name = Manager.getRightChase(name);
        	Info.custom("Getting Profile of §6" + name + "§3...");
        	Info.custom("Loading Profile from §6Database§3...");
			for(Profile pro : Database.getProfiles()){
				if(pro.getName().equals(name)){
					Info.success();
					return pro;
				}
			}
			Info.fail();
			Profile profile = null;
			if(profile == null){
				Info.custom("Loading Profile from §6MySQL§3...");
				profile = MySQLProfile.getProfile(name);
			}
			if(profile == null){
				Info.fail();
        		Info.custom("Loading Profile from §6Downloader§3...");
				profile = Downloader.getProfile(name);
			}
			if(profile != null){
				Info.success();
				Database.addProfile(profile.getName(), profile);
				MySQLProfile.createProfile(profile);
				return profile;
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
	 * Returns the profile by uuid. First checks the Database, then the MySQL and at least the Mojang-API for an profile with this uuid.
	 * 
	 * @param uuid The uuid of the requested Profile
	 */
	public static Profile getProfile(UUID uuid){
		try {
			if(uuid == null){
				throw new NullPointerException();
			}
        	Info.custom("Getting Profile of §6" + uuid + "§3...");
        	Info.custom("Loading Profile from §6Database§3...");
			for(Profile pro : Database.getProfiles()){
				if(pro.getUUID().equals(uuid)){
					Info.success();
					return pro;
				}
			}
			Info.fail();
			Profile profile = null;
			if(profile == null){
				Info.custom("Loading Profile from §6MySQL§3...");
				profile = MySQLProfile.getProfile(uuid);
			}
			if(profile == null){
				Info.fail();
        		Info.custom("Loading Profile from §6Downloader§3...");
				profile = Downloader.getProfile(uuid);
			}
			if(profile != null){
				Info.success();
				Database.addProfile(profile.getName(), profile);
				MySQLProfile.createProfile(profile);
				return profile;
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
	 * Checks if an Profile with this name exists. First checks the Database, then the MySQL and at least the Mojang-API for an profile with that name.
	 * 
	 * @return Is an Profile with this name existing
	 */
	public static boolean profileExists(String name){
		try {
			name = Manager.getRightChase(name);
        	Info.custom("Checking if Profile of §6" + name + " §3exists...");
        	Info.custom("Loading Profile from §6Database§3...");
			for(Profile profile : Database.getProfiles()){
				if(profile.getName().equals(name)){
					Info.success();
					return true;
				}
			}
			Info.fail();
			Profile profile = null;
			if(profile == null){
				Info.custom("Loading Profile from §6MySQL§3...");
				profile = MySQLProfile.getProfile(name);
			}
			if(profile == null){
				Info.fail();
        		Info.custom("Loading Profile from §6Downloader§3...");
        		profile = Downloader.getProfile(name);
			}
			if(profile != null){
				Info.success();
				Database.addProfile(name, profile);
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