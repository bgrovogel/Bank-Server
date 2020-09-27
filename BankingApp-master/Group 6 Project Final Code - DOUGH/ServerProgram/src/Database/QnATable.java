package Database;

import java.sql.SQLException;

public class QnATable extends ConnectDB{
	
	public QnATable(){
		super();
	}

	public void printQnATable() throws SQLException {
		printDatabase("QnA");
	}
	
	public void printQuestion(int ID) throws SQLException {
		rs = st.executeQuery("SELECT Message FROM QnA WHERE ID="+ID);
		rs.next();
		System.out.println(rs.getString(1));
	}
	public String getQuestion(int ID) throws SQLException {
		rs = st.executeQuery("SELECT Message FROM QnA WHERE ID="+ID);
		rs.next();
		return rs.getString(1);
	}
	
	public String getAnswer(int ID) throws SQLException {
		rs = st.executeQuery("SELECT Answer FROM QnA WHERE ID="+ID);
		rs.next();
		return rs.getString(1);
	}
	
	
	public void answerQuestion(int ID, String ans) throws SQLException {
		st.executeUpdate("UPDATE QnA SET Answer ='"+ans+"' WHERE ID="+ID);
	}
	
	public void addQuestion(String name, String email, String subject, String question) throws SQLException {
		rs = st.executeQuery("SELECT COUNT(ID) FROM QnA");
		rs.next();
		int questionID = Integer.parseInt(rs.getString(1))+1;
		question = question.replace("'", "");
		st.executeUpdate("INSERT INTO QnA VALUES "+questionID+",'"+name+"','"+email+"','"+subject+"','"+question+"',''");
		//st.executeUpdate("INSERT INTO QnA VALUES "+questionID+",'"+name+"','"+email+"','"+subject+"','"+message+"',''");
	}
}
