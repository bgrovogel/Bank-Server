package Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class CustomersTable extends ConnectDB {
	
	//constructor that initalize the parent class constructor
	public CustomersTable() {
		super();
	}
	

	/*public void printCustomersTable() throws SQLException {
		printDatabase("Customers");
	}*/

	//print all the data entries of the customerTable
	//This class is Lolars code but I (Levi) changed this method because
	//I wanted to format it differently
	public void printCustomersTable() throws SQLException{
		rs = st.executeQuery("SELECT * FROM "+"Customers");
		metadata = rs.getMetaData();

		String[] fields=new String[10];

		while(rs.next()) {
			for(int i = 1; i <= metadata.getColumnCount(); i++) {
				fields[i]=rs.getString(metadata.getColumnName(i))+" | ";
			}

			System.out.print(fields[9]);
			System.out.print(fields[1]);
			System.out.print(fields[2]);
			System.out.print(fields[8]);
			for(int i=3; i<8; i++)
				System.out.print(fields[i]);
			System.out.println();
		}
	}
	
	//print all the information related to the customers
	public void printCustomerInfo(int ID) throws SQLException {
		printInfo(ID,"Customers");
	}
	
	//return an ArrayList of all the username
	public ArrayList<String> getUserName() throws SQLException {
		ArrayList<String> users = new ArrayList<String>();
		
		rs=st.executeQuery("SELECT Username FROM Customers");
		
		while(rs.next()) {
			
			users.add(rs.getString(1));
			
		}
		
		return users;
	}
	
	//return an ArrayList of each username follow by their customerID. The format is [username,id,username,id...]
	public ArrayList<String> getUserNameAndID() throws SQLException {
		ArrayList<String> users = new ArrayList<String>();
		
		rs=st.executeQuery("SELECT Username, ID FROM Customers");
		
		while(rs.next()) {
			
			users.add(rs.getString(1));
			users.add(rs.getString(2));
			
		}
		
		return users;
	}
	//return the password of the specify username. To be use for verification.
	public String getPassword(String username) throws SQLException {
		
		rs=st.executeQuery("SELECT Password FROM Customers WHERE Username='"+username+"'");
		rs.next();
		return rs.getString(1);
	}
	
	//return the customerID base on the username and password.
	public int getCustomerID(String username, String password) throws SQLException{
		rs = st.executeQuery("SELECT ID FROM Customers WHERE Username='"+username+"' AND Password ='"+password+"'");
		rs.next();
		return rs.getInt("ID");
	}
	
	//return the first name of the customer base on the customer ID.
	public String getFirstName(int CustomerID) throws SQLException {
		rs = st.executeQuery("SELECT FirstName FROM Customers WHERE ID="+CustomerID);
		rs.next();
		return rs.getString(1);
	}
	
	//return the last name of the customer base on the customer ID.
	public String getLastName(int CustomerID) throws SQLException {
		rs = st.executeQuery("SELECT LastName FROM Customers WHERE ID="+CustomerID);
		rs.next();
		return rs.getString(1);
	}
	
	//return the email of the customer base on teh customer ID.
	public String getEmail(int CustomerID) throws SQLException {
		rs = st.executeQuery("SELECT Email FROM Customers WHERE ID="+CustomerID);
		rs.next();
		return rs.getString(1);
	}
	
	//return all the emails in the customers table.
	public ArrayList<String> getAllEmails() throws SQLException {
		ArrayList<String> emails = new ArrayList<String>();
		
		rs=st.executeQuery("SELECT Email FROM Customers");
		
		while(rs.next()) {
			
			emails.add(rs.getString(1));
			
		}
		
		return emails;
	}
	
	//return the address of the customer base on the customerID
	public String getAddress(int CustomerID) throws SQLException {
		rs = st.executeQuery("SELECT Address FROM Customers WHERE ID="+CustomerID);
		rs.next();
		return rs.getString(1);
	}
	
	//return the social security number of the customer base off the customer ID;
	public String getSSN(int CustomerID) throws SQLException {
		rs = st.executeQuery("SELECt SSN FROM Customers WHERE ID="+CustomerID);
		rs.next();
		String ssn = rs.getString(1);
		String fssn = "";
		for(int i = 0; i<ssn.length();i++) {
			if(i == 3 || i == 5) {
				fssn += "-";
				fssn += Character.toString(ssn.charAt(i));
			}else {
				fssn += Character.toString(ssn.charAt(i));
			}
		}
		return fssn;
	}
	
	//return all the addresses in the customers table.
	public ArrayList<String> getAllAddress() throws SQLException {
		ArrayList<String> addresses = new ArrayList<String>();
		
		rs=st.executeQuery("SELECT Address FROM Customers");
		
		while(rs.next()) {
			
			addresses.add(rs.getString(1));
			
		}
		
		return addresses;
	}

	//add a new entries to the customer table [first name, last name, email, address, date of birth, ssn, username, password,)
	public void addNewCustomers(String fname, String lname, String email, String address, String DOB, String SSN, String username, String password, CheckingTable ch, SavingTable sa, CreditTable cr) throws SQLException {
		int ssn = Integer.parseInt(SSN.replace("-",""));
		nextID = numberOfCustomers() + 1;
		st.executeUpdate("INSERT INTO Customers VALUES '"+fname+"','"+lname+"',"+ssn+",'"+username+"','"+email+"','"+address+"','"+password+"','"+DOB+"',"+nextID);
		ch.openChecking();
		sa.openSaving();
		cr.openCredit();
	}
}
