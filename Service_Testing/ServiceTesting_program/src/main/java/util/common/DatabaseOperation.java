package util.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Configuration.PropertiesHandle;

public class DatabaseOperation
{
	private static Connection conn = null;
	private static String JDBC_DRIVER = null;
	private static String DB_URL =null;
	private static String USER=null;
	private static String PASS =null;
	protected String query = null;
	protected Statement stmt = null;
	protected ResultSet rs = null;
	
	public static void ConnectionSetup(PropertiesHandle config) throws SQLException,ClassNotFoundException
	{
		JDBC_DRIVER =config.getProperty("jdbc_driver");
		DB_URL = config.getProperty("db_url");
		USER=config.getProperty("db_username");
		PASS =config.getProperty("db_password");
		if(conn == null)
		{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);	
		}		
	}
	
	public static void ConnectionSetup(String Platform) throws SQLException,ClassNotFoundException
	{
		JDBC_DRIVER = "com.mysql.jdbc.Driver";
		DB_URL = "jdbc:mysql://192.168.35.2:3391/Starr_Config_" + Platform;
		USER="root";
		PASS ="password";
		if(conn == null)
		{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);	
		}		
	}
	
	public static void CloseConn() throws SQLException
	{
		conn.close();
		conn = null;
	}
	
	public void GetDataObjects(String query)throws SQLException
	{
		this.query = query;
        stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
	    rs =    stmt.executeQuery(this.query);
	    rs.first();
	}
	
	public boolean MoveForward() throws SQLException
	{
		return rs.next();
	}
	
	public String ReadData(String column_name) throws SQLException
	{
		return rs.getString(column_name);
	}
	
	public void WriteData(String column_name,String value) throws SQLException
	{
		rs.updateString(column_name, value);
	}
	
	public void UpdateRow() throws SQLException
	{
		rs.updateRow();
	}
	
}
