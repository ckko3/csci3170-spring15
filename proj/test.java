import java.io.*;
import java.sql.*;
import java.util.*;

class test{
	public static void main (String args[]) {
	  try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DROP TABLE test");
			conn.close();
			System.out.println("Processing...Done! Database is initialized!");
		}catch(Exception e){
			System.out.println(e); 
		}
		finally{
			System.out.println("finally");
		}
	}
}
