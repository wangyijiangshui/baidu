package com.duapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * 
 * @author Administrator
 *
 */
public class DBUtil {

	/**
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			String databaseName = "sBanHmsWVscLxJvnfqRN"; 
			String host = "127.0.0.1";
			String port = "3306";
			String username = "root";
			String password = "buzhidao";
			String driverName = "com.mysql.jdbc.Driver";
			String dbUrl = "jdbc:mysql://";
			String serverName = host + ":" + port + "/";
			String connName = dbUrl + serverName + databaseName+"?useUnicode=true&amp;characterEncoding=utf-8";
			Class.forName(driverName);
			conn = DriverManager.getConnection(connName, username, password);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 
	 * 
	 * @param stmt
	 * @param con
	 */
	public static void close(Statement stmt, Connection conn) {
		try {
			if(null != stmt)stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(null != conn)conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 
	 * @param rs
	 * @param stmt
	 * @param con
	 */
	public static void close(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if(null != rs)rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(null != stmt)stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(null != conn)conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
