package me.SebiZocer.SkinLoader.Methods.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLNick {
	
	private static MySQL mysql;
	private static Connection con;
	
	public static void setMySQL(MySQL mysql){
		MySQLNick.mysql = mysql;
		con = mysql.getConnection();
		createTable();
	}
	
	public static MySQL getMySQL(){
		return mysql;
	}
	
	private static void createTable(){
		try {
			PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS autonick_database(uuid VARCHAR(100), status BOOLEAN)");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createUser(UUID uuid){
		try {
			if(userExists(uuid)){
				return;
			}
			PreparedStatement ps = con.prepareStatement("INSERT INTO autonick_database (uuid, status) VALUES (?, ?)");
			ps.setString(1, uuid.toString());
			ps.setBoolean(2, false);
			ps.executeUpdate();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static boolean userExists(UUID uuid){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM autonick_database WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	public static boolean getAutonick(UUID uuid){
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM autonick_database WHERE uuid = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getBoolean("status");
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	public static void setAutonick(UUID uuid, boolean b){
		try {
			PreparedStatement ps = con.prepareStatement("UPDATE autonick_database SET status = ? WHERE uuid = ?");
			ps.setBoolean(1, b);
			ps.setString(2, uuid.toString());
    	 ps.executeUpdate();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}