package me.SebiZocer.SkinLoader.Methods.Management;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.SebiZocer.SkinLoader.Methods.GameProfileEditor;
import me.SebiZocer.SkinLoader.Methods.Classes.Info;
import me.SebiZocer.SkinLoader.Methods.Classes.Profile;
import me.SebiZocer.SkinLoader.Methods.Classes.Skin;
import me.SebiZocer.SkinLoader.Methods.Classes.User;
import me.SebiZocer.SkinLoader.Methods.Management.Data.Database;
import me.SebiZocer.SkinLoader.Methods.Management.Data.Downloader;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLProfile;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLSkin;

public class Manager {
	
	/**
	 * Gets called at onEnable(). Loads all profiles, skins and playerinfos to Database. Dont call this method!
	 */
	public static void loadMySQLStuff(){
		try {
			Info.custom("Loading everything from §6MySQL§3...");
			for(Profile profile : MySQLProfile.getProfiles()){
				Database.addProfile(profile.getName(), profile);
				Database.addSkin(profile.getName(), profile.getSkin());
				Database.addInfo(profile.getName(), profile.getUUID());
				Info.custom("Profile of §6" + profile.getName() + " §3successfully loaded");
			}
			Info.success();
		} catch(Exception e){
			Info.error();
			if(Info.active){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns a random nickname. Gets the nickname by an random profile from ProfileManager.
	 * 
	 * @param unused Should the nickname not in use currently on the server
	 */
	public static String getRandomNickname(boolean unused){
		return ProfileManager.getRandomProfile(unused).getName();
	}
	
	/**
	 * Renicks all nicked players if the new player has the same name as the nicked player. Gets called at PlayerJoinEvent.
	 * 
	 * @param p The joined player
	 */
	public static void renickCheck(Player p){
		for(Player all : Bukkit.getOnlinePlayers()){
			User x = User.getUser(all);
			if(x.isNicked()){
				if(x.getName().equalsIgnoreCase(p.getName())){
					Skin skin = MySQLSkin.getCurrentSkin(x.getUniqueId());
					if(skin.getName().equals(x.getRealname())){
						skin = SkinManager.getSkin(getRandomNickname(true));
					}
					String nickname = MySQLProfile.getProfile(getRandomNickname(true)).getName();
					GameProfileEditor gpe = x.getGameProfileEditor();
					gpe.edit(nickname, skin, false, false, true, true, false);
				}
			}
		}
	}
	
	/**
	 * Returns the name of the owner of the uuid. Returns null if the uuid is not in use.
	 * 
	 * @param uuid The uuid of the requested player
	 */
	public static String getName(UUID uuid){
        try {
        	Info.custom("Getting Name of §6" + uuid + "§3...");
        	Info.custom("Loading Name from §6Database§3...");
        	if(Database.getUUIDs().contains(uuid)){
        		Info.success();
        		return Database.getName(uuid);
        	} else {
            	Info.fail();
        	}
        	String name = null;
        	if(name == null){
        		Info.custom("Loading Name from §6MySQL§3...");
        		name = MySQLProfile.getName(uuid);
        	}
        	if(name == null){
        		Info.fail();
        		Info.custom("Loading Name from §6Downloader§3...");
        		name = Downloader.getName(uuid);
        	}
        	if(name != null){
        		Info.success();
                Database.addInfo(name, uuid);
                return name;
        	} else {
        		Info.fail();
        	}
		} catch (Exception e){
			Info.error();
			if(Info.active){
				e.printStackTrace();
			}
		}
        return null;
	}
	
	/**
	 * Returns the uuid of the player with the name. If no player uses this name it will return null.
	 * 
	 * @param name The name of the owner of the uuid
	 */
	public static UUID getUUID(String name){
		try {
			name = getRightChase(name);
        	Info.custom("Getting UUID of §6" + name + "§3...");
        	Info.custom("Loading UUID from §6Database§3...");
			for(String s : Database.getNames()){
				if(s.equalsIgnoreCase(name)){
					Info.success();
					return Database.getUUID(s);
				}
			}
			Info.fail();
			UUID uuid = null;
			if(uuid == null){
				Info.custom("Loading UUID from §6MySQL§3...");
				uuid = MySQLProfile.getUUID(name);
			}
			if(uuid == null){
				Info.fail();
        		Info.custom("Loading UUID from §6Downloader§3...");
        		uuid = Downloader.getUUID(name);
			}
			if(uuid != null){
				Info.success();
				Database.addInfo(name, uuid);
				return uuid;
			} else {
				Info.fail();
			}
		} catch (Exception e){
			Info.error();
			if(Info.active){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Returns the name in the right chase. Looks in the Database if any name there equals and ignores the chase of the name.
	 * 
	 * @param name The name of the player
	 */
	public static String getRightChase(String name){
		String retur = name;
		for(String s : Database.getNames()){
			if(s.equalsIgnoreCase(name)){
				retur = s;
			}
		}
		return retur;
	}
}