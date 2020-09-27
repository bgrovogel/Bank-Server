package Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Main {
	
	//testing random number generator
	static Random rand = new Random();
	public static int AccountNumberGenerator() {
		String acct = "3";
		for(int i = 0; i<8; i++) {
			acct += Integer.toString(rand.nextInt(10));
		}
		
		return Integer.parseInt(acct);
	}

	public static void main1(String args[]) throws SQLException {
		CustomersTable customers = new CustomersTable(); //create a connection to each customers
		CheckingTable checking = new CheckingTable(); // create a connection to checking table
		SavingTable saving = new SavingTable(); //create a connection to saving table
		CreditTable credit = new CreditTable(); //create a connection to credit table
		
		/*
		customers.printCustomersTable(); //print the customers table
		checking.printCheckingTable(); //print the checking table
		saving.printSavingTable(); //print the saving table		 */
		credit.printCreditTable(); //print the credit table

		
		//Customers Methods
		System.out.println("Customers:");
		System.out.println(customers.getUserName());
		
		System.out.println(customers.getUserNameAndID());
		
		String password = customers.getPassword("michaelj29");
		
		int ID = customers.getCustomerID("michaelj29",password);
		
		System.out.println(customers.getFirstName(ID)+" "+customers.getLastName(ID));
		
		System.out.println(customers.getEmail(ID));
		
		System.out.println(customers.getAddress(ID));
		
		customers.printCustomerInfo(ID);
		
		System.out.println(customers.getSSN(ID));
		
		//Once this line is executed, you will need to close the re-open the database file if you are using Microsoft Access.
		customers.addNewCustomers("Bob", "Silly", "Youknow@wahoo.com", "1111 Slow Street", "10/10/10", "333-33-333","uknowit", "idonit",checking,saving,credit);
		
		
		//Checking Account Methods
		System.out.println("\nChecking Accounts:");
		System.out.println(checking.getAcctNumber(ID));
		
		System.out.println(checking.getAcctValue(ID));
		
		checking.deposite(ID, 1.50);//add one dollar
		
		System.out.println(checking.getAcctValue(ID));//print amount after adding 1 dollar to it
		
		checking.widthdraw(ID, 1.50);//widthrawing 1 dollar
		
		System.out.println(checking.getAcctValue(ID));//print amount after widthrawing 1 dollar to it
		
		
		
		//Saving Account Methods
		System.out.println("\nSaving Accounts:");
		System.out.println(saving.getAcctNumber(ID));
		
		System.out.println(saving.getAcctValue(ID));
		
		saving.deposite(ID, 1.24);//add one dollar
		
		System.out.println(saving.getAcctValue(ID));//print amount after adding 1 dollar to it
		
		saving.widthdraw(ID, 1.24);//withdrawing 1 dollar
		
		System.out.println(saving.getAcctValue(ID));//print amount after withdrawing 1 dollar to it
		
		
		//Credit Account Methods
		System.out.println("\nCredit Accounts:");
		System.out.println(credit.getAcctNumber(ID));
		
		System.out.println(credit.getAcctValue(ID));
		
		credit.addCredit(ID, 1.24);//add one dollar
		
		System.out.println(saving.getAcctValue(ID));//print amount after adding 1 dollar to it
		
		credit.payCredit(ID, 1.24);//withdrawing 1 dollar
		
		System.out.println(saving.getAcctValue(ID));//print amount after withdrawing 1 dollar to it
	
	}
}
