package me.SebiZocer.SkinLoader.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.SebiZocer.SkinLoader.Listener.LTRjoin;
import me.SebiZocer.SkinLoader.Listener.LTRquit;
import me.SebiZocer.SkinLoader.Methods.ConfigManager;
import me.SebiZocer.SkinLoader.Methods.GameProfileEditor;
import me.SebiZocer.SkinLoader.Methods.Classes.Info;
import me.SebiZocer.SkinLoader.Methods.Classes.User;
import me.SebiZocer.SkinLoader.Methods.Management.Manager;
import me.SebiZocer.SkinLoader.Methods.Management.ProfileManager;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQL;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLNick;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLProfile;
import me.SebiZocer.SkinLoader.Methods.MySQL.MySQLSkin;

public class Mainclass extends JavaPlugin implements Listener {
	
	public static Mainclass plugin;
	
    @Override
    public void onEnable() {
    	plugin = this;
    	Info.custom("Registering Events...");
    	registerEvents();
    	Info.success();
    	Info.custom("Setting Infos...");
    	setInfos();
        System.out.println("[" + getDescription().getName() + "] enabled. Plugin by Sebi_Zocer");
    }
    
	public void onDisable(){
        super.onDisable();
        for(Player all : Bukkit.getOnlinePlayers()){
        	User p = User.getUser(all);
        	GameProfileEditor gpe = p.getGameProfileEditor();
        	gpe.edit(p.getRealname(), ProfileManager.getProfile(p.getUniqueId()).getSkin(), true, true, true, true, true);
        	p.setNicked(false);
        }
        System.out.println("[" + getDescription().getName() + "] disabled");
    }
	
	public void registerEvents(){
		getServer().getPluginManager().registerEvents(new LTRjoin(), this);
		getServer().getPluginManager().registerEvents(new LTRquit(), this);
	}
	
	public void setInfos(){
		ConfigManager cfg = new ConfigManager("config");
		if(cfg.getYaml().contains("info")){
			if(cfg.getYaml().getBoolean("info")){
				Info.active = true;
			}
		}
		Info.custom("Creating config...");
		Info.success();
		Info.custom("Creating mysql login defaults...");
		cfg.addDefault("autonick", true);
		cfg.addDefault("mysql.host", "localhost");
		cfg.addDefault("mysql.port", 3306);
		cfg.addDefault("mysql.database", "spigot");
		cfg.addDefault("mysql.username", "minecraft");
		cfg.addDefault("mysql.password", "connect");
		Info.success();
		Info.custom("Connection to MySQL database...");
		MySQL mysql = new MySQL(cfg.getYaml().getString("mysql.host"), cfg.getYaml().getInt("mysql.port"), cfg.getYaml().getString("mysql.database"), cfg.getYaml().getString("mysql.username"), cfg.getYaml().getString("mysql.password"));
		if(!mysql.isConnected()){
			Info.fail();
			Info.custom("§cPlugin is useless without MySQL access. Shutdown...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else {
			Info.success();
		}
		MySQLSkin.setMySQL(mysql);
		MySQLProfile.setMySQL(mysql);
		MySQLNick.setMySQL(mysql);
		Info.custom("Loading MySQL stuff...");
		Manager.loadMySQLStuff();
		Info.success();
		Info.custom("Loading skins for players online...");
		boolean b1 = false;
		for(Player all : Bukkit.getOnlinePlayers()){
			b1 = true;
			Info.custom("Loading skin for §6" + all.getName());
			MySQLSkin.createUser(all);
			MySQLProfile.createProfile(ProfileManager.getProfile(all.getUniqueId()));
			MySQLNick.createUser(all.getUniqueId());
			User.getUser(all).loadSkin();
		}
		if(b1 == false){
			Info.custom("§cNo players found");
		}
		Info.success();
	}
}