package me.SebiZocer.SkinLoader.Methods.Classes;

import org.bukkit.Bukkit;

public class Info {
	
	public static boolean active = false;
	
	public static void custom(String msg){
		if(active){
			Bukkit.getConsoleSender().sendMessage("§3" + msg);
		}
	}
	
	public static void success(){
		if(active){
			Bukkit.getConsoleSender().sendMessage("§aSuccess");
		}
	}
	
	public static void fail(){
		if(active){
			Bukkit.getConsoleSender().sendMessage("§cFailed");
		}
	}
	
	public static void error(){
		if(active){
			Bukkit.getConsoleSender().sendMessage("§4Error");
		}
	}
}