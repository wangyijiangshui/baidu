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
			/*****百度云数据库连接信息*****/
			String databaseName = "sBanHmsWVscLxJvnfqRN"; 
			String host = "sqld.duapp.com";
			String port = "4050";
			String username = "sZN4tIAlCIVen4Spe1fen6fX";//用户名(api key);
			String password = "2r11CjCTZL8ZBnHty2AMlVPBYDVqBpGF";//密码(secret key)
			if (!System.getProperty("os.name").toLowerCase().equals("linux") || 1==1) {
				/*****本地数据库连接信息*****/
				databaseName = "sBanHmsWVscLxJvnfqRN";
				//databaseName = "testdb";
				host = "127.0.0.1";
				port = "3306";
				username = "root";//用户名(api key);
				password = "123456789";//密码(secret key)
			}
			String driverName = "com.mysql.jdbc.Driver";
			//url="jdbc:mysql://192.168.1.16:3306/base?useUnicode=true&amp;characterEncoding=gbk"
			String dbUrl = "jdbc:mysql://";
			String serverName = host + ":" + port + "/";
			//String connName = dbUrl + serverName + databaseName;
			String connName = dbUrl + serverName + databaseName+"?useUnicode=true&amp;characterEncoding=utf-8";
			 
			//System.out.println("connect to : " + connName);
			
			/******2. 接着连接并选择数据库名为databaseName的服务器******/
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
