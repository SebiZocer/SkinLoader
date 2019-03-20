package me.SebiZocer.SkinLoader.Methods.Management.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.SebiZocer.SkinLoader.Methods.Classes.Profile;
import me.SebiZocer.SkinLoader.Methods.Classes.Skin;

public class Database {
	
	private static HashMap<String, Skin> skins = new HashMap<>();
	private static HashMap<String, Profile> profiles = new HashMap<>();
	private static HashMap<String, UUID> infos = new HashMap<>();
	
	public static List<Profile> getProfiles(){
		List<Profile> l = new ArrayList<>();
		l.addAll(profiles.values());
		return l;
	}
	
	public static List<Skin> getSkins(){
		List<Skin> l = new ArrayList<>();
		l.addAll(skins.values());
		return l;
	}
	
	public static List<String> getNames(){
		List<String> l = new ArrayList<>();
		l.addAll(infos.keySet());
		return l;
	}
	
	public static List<UUID> getUUIDs(){
		List<UUID> l = new ArrayList<>();
		l.addAll(infos.values());
		return l;
	}
	
	public static String getName(UUID uuid){
		for(String s : infos.keySet()){
			if(infos.get(s).equals(uuid)){
				return s;
			}
		}
		return null;
	}
	
	public static UUID getUUID(String name){
		if(infos.containsKey(name)){
			return infos.get(name);
		}
		return null;
	}
	
	public static Profile getProfile(String owner){
		if(profiles.containsKey(owner)){
			return profiles.get(owner);
		}
		return null;
	}
	
	public static Skin getSkin(String owner){
		if(skins.containsKey(owner)){
			return skins.get(owner);
		}
		return null;
	}
	
	public static void addProfile(String owner, Profile profile){
		if(!profiles.containsKey(owner)){
			profiles.put(owner, profile);
		}
	}
	
	public static void addSkin(String owner, Skin skin){
		if(!skins.containsKey(owner)){
			skins.put(owner, skin);
		}
	}
	
	public static void addInfo(String name, UUID uuid){
		if(!infos.containsKey(name)){
			infos.put(name, uuid);
		}
	}
	
	public static void updateProfile(Profile profile){
		List<String> l = new ArrayList<>();
		for(String s : profiles.keySet()){
			if(profiles.get(s).getUUID().equals(profile.getUUID())){
				l.add(s);
			}
		}
		for(String s : l){
			profiles.remove(s);
		}
		profiles.put(profile.getName(), profile);
	}
	
	public static void updateSkin(Skin skin){
		List<String> l = new ArrayList<>();
		for(String s : skins.keySet()){
			if(s.equals(skin.getName())){
				l.add(s);
			}
		}
		for(String s : l){
			skins.remove(s);
		}
		skins.put(skin.getName(), skin);
	}
	
	public static void updateName(UUID uuid, String name){
		if(getName(uuid) != null){
			String oldName = getName(uuid);
			infos.remove(oldName);
		}
		infos.put(name, uuid);
	}
}