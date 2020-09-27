package Database;

import java.sql.SQLException;

public class CreditTable extends ConnectDB {
	
	//Initialize the constructor for the parent class
	public CreditTable() {
		super();
	}
	
	//print all the entries in the Credit table
	public void printCreditTable() throws SQLException {
		printDatabase("Credit");
	}
	
	//create a new credit account
	public void openCredit() throws SQLException {
		String acctNum = "1";
		while(acctNum.length()<9) {
			acctNum += rand.nextInt(10);
		}
		nextID = numberOfCustomers();
		st.executeUpdate("INSERT INTO Credit VALUES "+Integer.parseInt(acctNum)+",0,.15,'Credit',"+nextID);
		
	}
	
	//get the account number base off the customer ID
	public String getAcctNumber(int CustomerID) throws SQLException {
		
		rs = st.executeQuery("SELECT AcctNumber FROM Credit WHERE ID="+CustomerID);
		
		rs.next();
		
		return Integer.toString(rs.getInt("AcctNumber"));
	}
	
	//get the account value base off the customer ID
	public String getAcctValue(int CustomerID) throws SQLException {
		
		rs = st.executeQuery("SELECT Amount FROM Credit WHERE ID="+CustomerID);
		
		rs.next();
		String value = rs.getString(1);
		return value.substring(0,value.indexOf(".")+3);
	}
	
	//add new credit to the account base of the customer ID and amount.
	public void addCredit(int CustomerID, double value) throws SQLException{
		st.executeUpdate("UPDATE Credit SET Amount = Amount +"+value+" WHERE ID="+CustomerID);
	}
	
	//remove credit amount base of the customer ID and amount.
	public void payCredit(int CustomerID, double value) throws SQLException{
		st.executeUpdate("UPDATE Credit SEt Amount = Amount -"+value+" WHERE ID="+CustomerID);
	}

}
