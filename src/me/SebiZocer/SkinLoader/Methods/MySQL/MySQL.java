package me.SebiZocer.SkinLoader.Methods.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
	
	private String host;
	private int port;
	private String database;
	private String username;
	private String password;
	private Connection con;
	
	public MySQL(String host, int port, String database, String username, String password){
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		connect();
	}
	
	public boolean connect(){
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true" +  "&" + "user=" + username + "&" + "password=" + password);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public boolean isConnected(){
		if(con != null){
			return true;
		} else {
			return false;
		}
	}
	
	public Connection getConnection(){
		if(con != null){
			return con;
		} else {
			connect();
			return con;
		}
	}
	
	public void disconnect(){
		try {
			con.close();
		} catch (SQLException e) {
		}
	}
}