package me.SebiZocer.SkinLoader.Methods;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import me.SebiZocer.SkinLoader.Main.Mainclass;

public class ConfigManager {
	
	//ConfigManagement//
	
	public static List<ConfigManager> Configs = new ArrayList<>();
	
	private static void addConfig(ConfigManager cfgm){
		Configs.add(cfgm);
	}
	
	public static boolean containsConfigManager(String cfgm){
		for(ConfigManager cfg : Configs){
			if(cfg.getName().equalsIgnoreCase(cfgm)){
				return true;
			}
		}
		return false;
	}
	
	public static ConfigManager getConfigManager(String cfgm){
		for(ConfigManager cfg : Configs){
			if(cfg.getName().equalsIgnoreCase(cfgm)){
				return cfg;
			}
		}
		return null;
	}
	
	public static boolean removeConfig(ConfigManager cfgm){
		if(Configs.contains(cfgm)){
			Configs.remove(cfgm);
			return true;
		}
		return false;
	}
	
	//ConfigManagement end//
	
	private String name;
	private String path;
	private String path2;
	private File file;
	private YamlConfiguration yaml;
	
	public ConfigManager(String name){
		this.name = name;
		this.path = "plugins//" + Mainclass.plugin.getDescription().getName();
		this.path2 = name + ".yml";
		refreshFiles();
		createFile();
		addConfig(this);
	}
	
	public void refreshFiles(){
		this.file = new File(this.path, this.path2);
		this.yaml = YamlConfiguration.loadConfiguration(this.file);
	}
	
	public boolean createFile(){
		refreshFiles();
		new File(path).mkdir();
		if(!file.exists()){
			try {
				file.createNewFile();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void setName(String name){
		this.name = name;
		refreshFiles();
	}
	
	public String getName(){
		return this.name;
	}
	
	public YamlConfiguration getYaml(){
		refreshFiles();
		return yaml;
	}
	
	public File getFile(){
		refreshFiles();
		return file;
	}
	
	public void setLocation(String path, Location loc){
		yaml.set(path + ".x", loc.getX());
		yaml.set(path + ".y", loc.getY());
		yaml.set(path + ".z", loc.getZ());
		yaml.set(path + ".yaw", loc.getYaw());
		yaml.set(path + ".pitch", loc.getPitch());
		yaml.set(path + ".world", loc.getWorld().getName());
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Location getLocation(String path){
		Location loc = new Location(Bukkit.getWorld(yaml.getString(path + ".world")), yaml.getDouble(path + ".x"), yaml.getDouble(path + ".y"), yaml.getDouble(path + ".z"), (float)yaml.getDouble(path + ".yaw"), (float)yaml.getDouble(path + ".yaw"));
		return loc;
	}
	
	public void set(String path, Object object){
		refreshFiles();
		if(object instanceof World){
			Bukkit.getConsoleSender().sendMessage("§cEs wurde versucht, eine Welt namens §6" + ((World)object).getName() + " §czu speichern");
			return;
		}
		yaml.set(path, object);
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ConfigManager addDefault(String path, Object object){
		refreshFiles();
		try {
			if(yaml.get(path) == null){
				yaml.set(path, object);
				yaml.save(file);
			}
		} catch(Exception e){
		}
		return this;
	}
	
	public void saveFile(){
		refreshFiles();
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clear(){
		file.delete();
		try {
			file.createNewFile();
		} catch(Exception e){
		}
	}
}
