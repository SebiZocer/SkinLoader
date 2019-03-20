package me.SebiZocer.SkinLoader.Methods.Management.Data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mojang.authlib.GameProfile;

import me.SebiZocer.SkinLoader.Methods.Classes.Info;
import me.SebiZocer.SkinLoader.Methods.Classes.Profile;
import me.SebiZocer.SkinLoader.Methods.Management.Manager;

public class Downloader {
	
	public static Profile getProfile(String name){
		try {
			GameProfile gp = GameProfileBuilder.fetch(UUID.fromString(Manager.getUUID(name).toString()));
			return Profile.byGameProfile(gp);
		} catch(Exception e){
			Info.custom("§4Error while downloading§c: Profile of §6" + name + " §cnot exists");
			return null;
		}
	}
	
	public static Profile getProfile(UUID uuid){
		try {
			GameProfile gp = GameProfileBuilder.fetch(uuid);
			return Profile.byGameProfile(gp);
		} catch(Exception e){
			Info.custom("§4Error while downloading§c: Profile of §6" + uuid + " §cnot exists");
			return null;
		}
	}
	
	public static UUID getUUID(String name){
		try {
			URL api = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(api.openStream()));
			Object obj = new JSONParser().parse(reader);
			JSONObject object = (JSONObject) obj;
			reader.close();
			return UUID.fromString(((String) object.get("id")).replaceFirst(
					"([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)",
					"$1-$2-$3-$4-$5"));
		} catch(Exception e){
			Info.custom("§4Error while downloading§c: Profile of §6" + name + " §cnot exists");
			return null;
		}
	}
	
	public static String getName(UUID uuid){
		try {
	    	URL api = new URL("https://api.mojang.com/user/profiles/UUID/names".replace("UUID", uuid.toString().replace("-", "")));
	        BufferedReader reader = new BufferedReader(new InputStreamReader(api.openStream()));
	        Object obj = new JSONParser().parse(reader);
	        JSONArray array = (JSONArray)obj;
	        JSONObject object = (JSONObject) array.get(array.size() - 1);
	        reader.close();
	        return (String) object.get("name");
		} catch(Exception e){
			Info.custom("§4Error while downloading§c: Profile of §6" + uuid + " §cnot exists");
			return null;
		}
	}
}