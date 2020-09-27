package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class ConnectDB {
	protected Connection con;
	protected Statement st;
	protected ResultSet rs;
	protected ResultSetMetaData metadata;
	protected int nextID;
	Random rand = new Random();
	
	public ConnectDB() {
		String path = "src\\Bank.mdb";
		String driver = "net.ucanaccess.jdbc.UcanaccessDriver";
		try {
			Class.forName(driver);
			con=DriverManager.getConnection("jdbc:ucanaccess://"+path);
			st = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printDatabase(String table) throws SQLException{
		
		rs = st.executeQuery("SELECT * FROM "+table);
		metadata = rs.getMetaData();
		
		while(rs.next()) {
			for(int i = 1; i <= metadata.getColumnCount(); i++) {
				System.out.print(rs.getString(metadata.getColumnName(i))+" | ");
			}
			System.out.println();
		}
	}
	
	public int numberOfCustomers() throws SQLException {
		rs = st.executeQuery("SELECT COUNT(ID) FROM Customers");
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}
	
	public void printInfo(int ID,String table) throws SQLException {
		rs = st.executeQuery("SELECT * FROM "+table+" WHERE ID="+ID);
		metadata = rs.getMetaData();
		rs.next();
		for(int i = 1; i <= metadata.getColumnCount(); i++) {
			System.out.print(metadata.getColumnLabel(i)+" = "+rs.getString(metadata.getColumnName(i))+" | ");
		}
		System.out.println();
	}
}
