package me.SebiZocer.SkinLoader.Methods.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.SebiZocer.SkinLoader.Methods.Classes.Info;
import me.SebiZocer.SkinLoader.Methods.Classes.Profile;
import me.SebiZocer.SkinLoader.Methods.Classes.Skin;
import me.SebiZocer.SkinLoader.Methods.Management.Manager;
import me.SebiZocer.SkinLoader.Methods.Management.ProfileManager;
import me.SebiZocer.SkinLoader.Methods.Management.SkinManager;

public class MySQLSkin {
	
	private static MySQL mysql;
	private static Connection con;
	private static String tablename = "skin_database";
	
	public static void setMySQL(MySQL mysql){
		MySQLSkin.mysql = mysql;
		con = mysql.getConnection();
		createTable();
	}
	
	public static MySQL getMySQL(){
		return mysql;
	}
	
	private static void createTable(){
		try {
			String list = "";
			for(int i1 = 0; i1 < 18; i1++){
				list = list + ", name" + (i1 + 1) + " VARCHAR(100)";
			}
			PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablename + "(uuid VARCHAR(100), current VARCHAR(100)" + list + ")");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Skin getRandomSkin(UUID uuid){
		if(!getSkins(uuid).isEmpty()){
			List<Skin> skins = getSkins(uuid);
			return skins.get(new Random().nextInt(skins.size()));
		} else {
			return SkinManager.getSkin(uuid);
		}
	}
	
	public static List<Skin> getSkins(UUID uuid){
		try {
			List<Skin> skins = new ArrayList<>();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				for(int i1 = 0; i1 < 18; i1++){
					String namex = rs.getString("name" + (i1 + 1));
					if(!namex.equals(" ")){
						namex = namex.replace(" ", "");
						Profile profile = ProfileManager.getProfile(namex);
						skins.add(profile.getSkin());
					}
				}
			}
			return skins;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static List<Skin> getSkins(String name){
		try {
			List<Skin> skins = new ArrayList<>();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, Manager.getUUID(name).toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				for(int i1 = 0; i1 < 18; i1++){
					String namex = rs.getString("name" + (i1 + 1));
					if(!namex.equals(" ")){
						namex = namex.replace(" ", "");
						Profile profile = ProfileManager.getProfile(namex);
						skins.add(profile.getSkin());
					}
				}
			}
			return skins;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getUUIDs(){
		List<String> uuids = new ArrayList<>();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + "");
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				uuids.add(rs.getString("uuid"));
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return uuids;
	}
	
	public static void setCurrentSkin(UUID uuid, Skin skin){
		try {
			PreparedStatement ps = con.prepareStatement("UPDATE " + tablename + " SET current = ? WHERE uuid = ?");
			ps.setString(1, skin.getName());
			ps.setString(2, uuid.toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Skin getCurrentSkin(UUID uuid){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return SkinManager.getSkin(rs.getString("current"));
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void createUser(Player p){
		if(!containsUser(p)){
			try {
				String x = "";
				for(int i1 = 0; i1 < 18; i1++){
					x = x + ", name" + (i1 + 1);
				}
				PreparedStatement ps = con.prepareStatement("INSERT INTO " + tablename + "(uuid, current" + x + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, p.getName());
				for(int i1 = 0; i1 < 18; i1++){
					ps.setString((i1 + 3), " ");
				}
				ps.executeUpdate();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static boolean containsUser(Player p){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public static void addSkin(Player p, Skin skin){
		try {
			String name = skin.getName();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			List<String> skins = new ArrayList<>();
			if(rs.next()){
				for(int i1 = 0; i1 < 18; i1++){
					if(!rs.getString("name" + (i1 + 1)).equals(" ")){
						skins.add(rs.getString("name" + (i1 + 1)));
					}
				}
			}
			String left = "name" + (skins.size() + 1);
			if(left.equals("name19")){
				Info.custom("§4Error at 151. Skinlimit reached");
			}
			ps = con.prepareStatement("UPDATE " + tablename + " SET " + left + " = ? WHERE uuid = ?");
			ps.setString(1, name);
			ps.setString(2, p.getUniqueId().toString());
			ps.executeUpdate();
			ps.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void removeSkin(Player p, Skin skin){
		try {
			String name = skin.getName();
			List<String> skins = new ArrayList<>();
			for(Skin s : getSkins(p.getUniqueId())){
				skins.add(s.getName());
			}
			if(skins.contains(skin.getName())){
				PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
				ps.setString(1, p.getUniqueId().toString());
				ResultSet rs = ps.executeQuery();
				skins.clear();
				if(rs.next()){
					for(int i1 = 0; i1 < 18; i1++){
						if(!rs.getString("name" + (i1 + 1)).equals(" ")){
							skins.add(rs.getString("name" + (i1 + 1)));
						}
					}
				}
				skins.remove(name);
				for(int i1 = 0; i1 < 18; i1++){
					ps = con.prepareStatement("UPDATE " + tablename + " SET name" + (i1 + 1) + " = ? WHERE uuid = ?");
					if(skins.size() > i1){
						ps.setString(1, skins.get(i1));
					} else {
						ps.setString(1, " ");
					}
					ps.setString(2, p.getUniqueId().toString());
					ps.executeUpdate();
				}
				ps.close();
			} else {
				Info.error();
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}