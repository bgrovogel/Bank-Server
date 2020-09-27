package Database;

import java.sql.SQLException;

public class CheckingTable extends ConnectDB {
	
	//Initialize the super constructor
	public CheckingTable() {
		super();
	}
	
	//print all the entries in the CheckingTable
	public void printCheckingTable() throws SQLException {
		printDatabase("Checking");
	}
	
	//create a new Checking Accounts
	public void openChecking() throws SQLException {
		String acctNum = "1";
		while(acctNum.length()<9) {
			acctNum += rand.nextInt(10);
		}
		nextID = numberOfCustomers();
		st.executeUpdate("INSERT INTO Checking VALUES "+Integer.parseInt(acctNum)+",0,'Checking',"+nextID);
		
	}
	
	//get the checking account number base off the customer ID
	public String getAcctNumber(int CustomerID) throws SQLException {
		
		rs = st.executeQuery("SELECT AcctNumber FROM Checking WHERE ID="+CustomerID);
		
		rs.next();
		
		return Integer.toString(rs.getInt("AcctNumber"));
	}
	
	//get the account amount base off the customer ID
	public String getAcctValue(int CustomerID) throws SQLException {
		
		rs = st.executeQuery("SELECT Amount FROM Checking WHERE ID="+CustomerID);
		
		rs.next();
		String value = rs.getString(1);
		return value.substring(0,value.indexOf(".")+3);
	}
	
	//update the account using the customer ID and the amount deposit.
	public void deposite(int CustomerID, double value) throws SQLException{
		st.executeUpdate("UPDATE Checking SET Amount = Amount +"+value+" WHERE ID="+CustomerID);
	}
	
	
	//update the account using the customer ID and the amount widthdrwa.
	public void widthdraw(int CustomerID, double value) throws SQLException{
		st.executeUpdate("UPDATE Checking SEt Amount = Amount -"+value+" WHERE ID="+CustomerID);
	}
}
