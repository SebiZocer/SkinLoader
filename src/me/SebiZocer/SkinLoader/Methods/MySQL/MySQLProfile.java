package me.SebiZocer.SkinLoader.Methods.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.SebiZocer.SkinLoader.Methods.Classes.Profile;
import me.SebiZocer.SkinLoader.Methods.Classes.Skin;
import me.SebiZocer.SkinLoader.Methods.Management.Manager;
import me.SebiZocer.SkinLoader.Methods.Management.Data.Database;

public class MySQLProfile {
	
	private static MySQL mysql;
	private static Connection con;
	private static String tablename = "profile_database";
	
	public static void setMySQL(MySQL mysql){
		MySQLProfile.mysql = mysql;
		con = mysql.getConnection();
		createTable();
	}
	
	public static MySQL getMySQL(){
		return mysql;
	}
	
	private static void createTable(){
		try {
			PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablename + "(uuid VARCHAR(100), name VARCHAR(100), signature TEXT, value TEXT)");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean containsProfile(UUID uuid){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	public static boolean containsProfile(String name){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE name = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	public static void createProfile(Profile profile){
		String uuid = profile.getUUID().toString();
		String name = profile.getName();
		String signature = profile.getSignature();
		String value = profile.getValue();
		if(containsProfile(UUID.fromString(uuid))){
			updateProfile(profile);
			return;
		}
		try {
			PreparedStatement ps = con.prepareStatement("INSERT INTO " + tablename + "( uuid, name, signature, value) VALUES ( ?, ?, ?, ? )");
			ps.setString(1, uuid);
			ps.setString(2, name);
			ps.setString(3, signature);
			ps.setString(4, value);
			ps.executeUpdate();
			Database.addProfile(profile.getName(), profile);
			Database.addSkin(profile.getName(), profile.getSkin());
			Database.addInfo(profile.getName(), profile.getUUID());
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static Profile getProfile(UUID uuid){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				String name = rs.getString("name");
				String value = rs.getString("value");
				String signature = rs.getString("signature");
				return new Profile(name, uuid, signature, value);
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Profile getProfile(String name){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE name = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				String uuid = rs.getString("uuid");
				String value = rs.getString("value");
				String signature = rs.getString("signature");
				return new Profile(name, UUID.fromString(uuid), signature, value);
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Skin getSkin(UUID uuid){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				String value = rs.getString("value");
				String signature = rs.getString("signature");
				return new Skin(Manager.getName(uuid), signature, value);
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Skin getSkin(String name){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE name = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				String value = rs.getString("value");
				String signature = rs.getString("signature");
				return new Skin(name, signature, value);
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static UUID getUUID(String name){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE name = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				return UUID.fromString(rs.getString("uuid"));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getName(UUID uuid){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				return rs.getString("name");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Profile> getProfiles(){
		try {
			List<Profile> profiles = new ArrayList<>();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tablename + " ORDER BY name");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String uuid = rs.getString("uuid");
				String value = rs.getString("value");
				String signature = rs.getString("signature");
				profiles.add(new Profile(name, UUID.fromString(uuid), signature, value));
			}
			return profiles;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static void updateProfile(Profile profile){
		String uuid = profile.getUUID().toString();
		String name = profile.getName();
		String signature = profile.getSignature();
		String value = profile.getValue();
		String cname = "";
		String csignature = "";
		String cvalue = "";
		try {
			PreparedStatement psname = con.prepareStatement("SELECT name FROM " + tablename + " WHERE uuid = ?");
			psname.setString(1, uuid);
			ResultSet rs = psname.executeQuery();
			while(rs.next()){
				cname = rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			PreparedStatement pssignature = con.prepareStatement("SELECT signature FROM " + tablename + " WHERE uuid = ?");
			pssignature.setString(1, uuid);
			ResultSet rs = pssignature.executeQuery();
			while(rs.next()){
				csignature = rs.getString("signature");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			PreparedStatement psvalue = con.prepareStatement("SELECT value FROM " + tablename + " WHERE uuid = ?");
			psvalue.setString(1, uuid);
			ResultSet rs = psvalue.executeQuery();
			while(rs.next()){
				cvalue = rs.getString("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		boolean change = false;
		
		if(!name.equals(cname)){
			Database.updateName(UUID.fromString(uuid), name);
			change = true;
			PreparedStatement ps;
			try {
				ps = con.prepareStatement("UPDATE " + tablename + " SET name = ? WHERE uuid = ?");
				ps.setString(1, name);
				ps.setString(2, uuid);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(!signature.equals(csignature)){
			change = true;
			PreparedStatement ps;
			try {
				ps = con.prepareStatement("UPDATE " + tablename + " SET signature = ? WHERE uuid = ?");
				ps.setString(1, signature);
				ps.setString(2, uuid);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(!value.equals(cvalue)){
			change = true;
			PreparedStatement ps;
			try {
				ps = con.prepareStatement("UPDATE " + tablename + " SET value = ? WHERE uuid = ?");
				ps.setString(1, value);
				ps.setString(2, uuid);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			if(change){
				Skin s = new Skin(name, signature, value);
				Profile pro = new Profile(name, UUID.fromString(uuid), signature, value);
				Database.updateProfile(pro);
				Database.updateSkin(s);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}