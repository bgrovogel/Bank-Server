package Database;

import java.sql.SQLException;

public class SavingTable extends ConnectDB {
	//Initialize the constructor for the parent class
	public SavingTable() {
		super();
	}
	
	//print all the entries in the Saving table.
	public void printSavingTable() throws SQLException {
		printDatabase("Saving");
	}
	
	//create a new saving accounts.
	public void openSaving() throws SQLException {
		String acctNum = "1";
		while(acctNum.length()<9) {
			acctNum += rand.nextInt(10);
		}
		nextID = numberOfCustomers();
		st.executeUpdate("INSERT INTO Saving VALUES "+Integer.parseInt(acctNum)+",0,'Saving',"+nextID);
	}
	
	//get the saving account number base off the customer ID
	public String getAcctNumber(int CustomerID) throws SQLException {
		
		rs = st.executeQuery("SELECT AcctNumber FROM Saving WHERE ID="+CustomerID);
		
		rs.next();
		
		return Integer.toString(rs.getInt("AcctNumber"));
	}
	
	//get the account value base off the customer ID
	public String getAcctValue(int CustomerID) throws SQLException {
		
		rs = st.executeQuery("SELECT Amount FROM Saving WHERE ID="+CustomerID);
		
		rs.next();
		String value = rs.getString(1);
		return value.substring(0,value.indexOf(".")+3);
	}
	
	//update the account using the customer ID and the amount deposit.
	public void deposite(int CustomerID, double value) throws SQLException{
		st.executeUpdate("UPDATE Saving SET Amount = Amount +"+value+" WHERE ID="+CustomerID);
	}
	
	
	//update the account using the customer ID and the amount widthdraw.
	public void widthdraw(int CustomerID, double value) throws SQLException{
		st.executeUpdate("UPDATE Saving SET Amount = Amount -"+value+" WHERE ID="+CustomerID);
	}
}
