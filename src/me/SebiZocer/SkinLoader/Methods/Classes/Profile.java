package me.SebiZocer.SkinLoader.Methods.Classes;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Profile {
	
	private String name;
	private UUID uuid;
	private String signature;
	private String value;
	
	public Profile(String name, UUID uuid, String signature, String value){
		this.name = name;
		this.uuid = uuid;
		this.signature = signature;
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
	
	public UUID getUUID(){
		return uuid;
	}
	
	public String getSignature(){
		return signature;
	}
	
	public String getValue(){
		return value;
	}
	
	public Skin getSkin(){
		return new Skin(name, signature, value);
	}
	
	public GameProfile getGameProfile(){
		GameProfile profile = new GameProfile(uuid, name);
		profile.getProperties().removeAll("textures");
		profile.getProperties().put("textures", new Property("textures", value, signature));
		return profile;
	}
	
	public static Profile byGameProfile(GameProfile profile){
		Property prop = (Property)profile.getProperties().get("textures").iterator().next();
		UUID uuid = profile.getId();
		String name = profile.getName();
		String signature = prop.getSignature();
		String value = prop.getValue();
		return new Profile(name, uuid, signature, value);
	}
}