//cprj2792
//zzthzskf
//ssh linux1
//bash
//source /usr/local/oracle/bin/setup.sh
//sqlplus db024@\"db12:1521/db12.cse.cuhk.edu.hk\"
//set path = ( /usr/local/jdk1.7.0/bin $path )
//javac proj_p2.java
//java -classpath ./ojdbc6.jar:./ proj_p2

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;

class proj_p2{
	private static void menu(){
		System.out.println();
		System.out.println("-----Main Menu-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Operations for administrator");
		System.out.println("2. Operations for librarian");
		System.out.println("3. Operations for library director");
		System.out.println("4. Exit this program");
		System.out.print("Enter Your Choice: ");
		Scanner scan = new Scanner(System.in);
		switch(scan.nextInt()){
			case 1: adm();
			case 2: lib();
			case 3: dir();
			case 4: exit();
		}
		return;
	}
    private static void adm() {
		System.out.println();
        System.out.println("-----Operations for administrator menu-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Create all tables");
		System.out.println("2. Delete all tables");
		System.out.println("3. Load from datafile");
		System.out.println("4. Show number of records in each table");
		System.out.println("5. Return to the main menu");		
		System.out.print("Enter Your Choice: ");
		Scanner scan = new Scanner(System.in);
		switch(scan.nextInt()){
			case 1: create();
			case 2: delete();
			case 3: load();
			case 4: show_record();
			case 5: menu();
		}
		return;
    }
    private static void lib() {
		System.out.println();
        System.out.println("-----Operations for librarian menu-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Search for Books");
		System.out.println("2. Show loan record of a user");
		System.out.println("3. Return to the main menu");		
		System.out.print("Enter Your Choice: ");
		Scanner scan = new Scanner(System.in);
		switch(scan.nextInt()){
			case 1: search();
			case 2: show_loan();
			case 3: menu();
		}
		return;
    }		  
    private static void dir() {
		System.out.println();
        System.out.println("-----Operations for manager menu-----");
		System.out.println("What kinds of operation would you like to perform?");
		System.out.println("1. Show the N books that are most often to be overdue");
		System.out.println("2. Show total number of book checked-out within a period");
		System.out.println("3. Return to the main menu");		
		System.out.print("Enter Your Choice: ");
		Scanner scan = new Scanner(System.in);
		switch(scan.nextInt()){
			case 1: show_overdue();
			case 2: show_checkedout();
			case 3: menu();
		}
		return;
    }
	private static void create() {
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("CREATE TABLE category"
							+ "(cid INTEGER,"
							+ "max INTEGER NOT NULL,"
							+ "period INTEGER NOT NULL,"
							+ "PRIMARY KEY (cid))");	
			stmt.executeUpdate("CREATE TABLE libuser"
							+ "(libuid CHAR(10),"
							+ "name VARCHAR2(25) NOT NULL,"
							+ "address VARCHAR2(100) NOT NULL,"
							+ "cid INTEGER NOT NULL,"
							+ "PRIMARY KEY (libuid))");
			stmt.executeUpdate("CREATE TABLE book"
							+ "(callnum CHAR(8),"
							+ "title VARCHAR2(30) NOT NULL,"
							+ "publish DATE NOT NULL,"
							+ "PRIMARY KEY (callnum))");
			stmt.executeUpdate("CREATE TABLE copy"
							+ "(callnum CHAR(8),"
							+ "copynum INTEGER,"
							+ "PRIMARY KEY (callnum,copynum))");
			stmt.executeUpdate("CREATE TABLE borrow"
							+ "(libuid CHAR(10),"
							+ "callnum CHAR(8),"
							+ "copynum INTEGER,"
							+ "checkout DATE,"
							+ "return DATE,"
							+ "PRIMARY KEY (libuid,callnum,copynum,checkout))");
			stmt.executeUpdate("CREATE TABLE authorship"
							+ "(aname VARCHAR2(25),"
							+ "callnum CHAR(8),"
							+ "PRIMARY KEY (aname,callnum))");
			conn.commit();
			System.out.println("Processing...Done! Database is initialized!");
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}finally{
			menu();
			return;
		}
	}
	private static void delete() {
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DROP TABLE borrow");
			stmt.executeUpdate("DROP TABLE authorship");
			stmt.executeUpdate("DROP TABLE libuser");
			stmt.executeUpdate("DROP TABLE category");
			stmt.executeUpdate("DROP TABLE copy");
			stmt.executeUpdate("DROP TABLE book");
			conn.commit();
			System.out.println("Processing...Done! Database is removed!");
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e); 
		}finally{
			menu();
			return;
		}
	}
	private static void load() {
		System.out.print("Type in the Source Data Folder Path: ");
		Scanner scan = new Scanner(System.in);
		String path = scan.nextLine();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			conn.setAutoCommit(false);
			
			scan = new Scanner(new File(path + "/" + "category.txt"));
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO category VALUES (?,?,?)");
			while (scan.hasNextLine()){
				String[] result = scan.nextLine().split("	");
				pstmt.setInt(1,Integer.parseInt(result[0]));
				pstmt.setInt(2,Integer.parseInt(result[1]));
				pstmt.setInt(3,Integer.parseInt(result[2]));
				pstmt.executeUpdate();
			}
			scan = new Scanner(new File(path + "/user.txt"));
			pstmt = conn.prepareStatement("INSERT INTO libuser VALUES (?,?,?,?)");
			while (scan.hasNextLine()){
				String[] result = scan.nextLine().split("	");
				pstmt.setString(1,result[0]);
				pstmt.setString(2,result[1]);
				pstmt.setString(3,result[2]);
				pstmt.setInt(4,Integer.parseInt(result[3]));
				pstmt.executeUpdate();
			}
			scan = new Scanner(new File(path + "/check_out.txt"));
			pstmt = conn.prepareStatement("INSERT INTO borrow VALUES (?,?,?,?,?)");
			while (scan.hasNextLine()){
				String[] result = scan.nextLine().split("	");
				pstmt.setString(1,result[0]);
				pstmt.setString(2,result[1]);
				pstmt.setInt(3,Integer.parseInt(result[2]));
				java.sql.Date sqldate = new java.sql.Date(sdf.parse(result[3]).getTime());
				pstmt.setDate(4,sqldate);
				if (result[4].equals("null"))
					pstmt.setDate(5,null);
				else{
				sqldate = new java.sql.Date(sdf.parse(result[4]).getTime());
				pstmt.setDate(5,sqldate);
				}
				pstmt.executeUpdate();
			}
			scan = new Scanner(new File(path + "/book.txt"));
			pstmt = conn.prepareStatement("INSERT INTO copy VALUES (?,?)");
			while (scan.hasNextLine()){
				String[] result = scan.nextLine().split("	");
				for(int i=1; i<=Integer.parseInt(result[1]); i++){
					pstmt.setString(1,result[0]);
					pstmt.setInt(2,i);
					pstmt.executeUpdate();
				}
			}
			scan = new Scanner(new File(path + "/book.txt"));
			pstmt = conn.prepareStatement("INSERT INTO book VALUES (?,?,?)");
			while (scan.hasNextLine()){
				String[] result = scan.nextLine().split("	");
				pstmt.setString(1,result[0]);
				pstmt.setString(2,result[2]);
				java.sql.Date sqldate = new java.sql.Date(sdf.parse(result[4]).getTime());
				pstmt.setDate(3,sqldate);
				pstmt.executeUpdate();
			}
			scan = new Scanner(new File(path + "/book.txt"));
			pstmt = conn.prepareStatement("INSERT INTO authorship VALUES (?,?)");
			while (scan.hasNextLine()){
				String[] result = scan.nextLine().split("	");
				String[] author = result[3].split(",");
				for(int i=0; i<author.length; i++){
					pstmt.setString(1,author[i]);
					pstmt.setString(2,result[0]);
					pstmt.executeUpdate();
				}
			}
			conn.commit();
			System.out.println("Processing...Done! Data is inputted to the database!");
			pstmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}
		finally{
			menu();
			return;
		}
	}
	private static void show_record() {
		System.out.println("Number of records in each table:");
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM category");
			rs.next();
			System.out.println("category: " + rs.getInt(1));
			rs = stmt.executeQuery("SELECT COUNT(*) FROM libuser");
			rs.next();
			System.out.println("libuser: " + rs.getInt(1));
			rs = stmt.executeQuery("SELECT COUNT(*) FROM book");
			rs.next();
			System.out.println("book: " + rs.getInt(1));
			rs = stmt.executeQuery("SELECT COUNT(*) FROM copy");
			rs.next();
			System.out.println("copy: " + rs.getInt(1));
			rs = stmt.executeQuery("SELECT COUNT(*) FROM borrow");
			rs.next();
			System.out.println("borrow: " + rs.getInt(1));
			rs = stmt.executeQuery("SELECT COUNT(*) FROM authorship");
			rs.next();
			System.out.println("authorship: " + rs.getInt(1));
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}
		finally{
			menu();
			return;
		}
	}
	private static void search() {
		System.out.println("Choose the Search criterion:");
		System.out.println("1. call number");
		System.out.println("2. title");
		System.out.println("3. author");
		System.out.print("Choose the Search criterion: ");
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		System.out.print("Type in the Search Keyword:");
		scan = new Scanner(System.in);
		String keyword = scan.nextLine();
		switch(choice){
			case 1: search_by_callnum(keyword);
			case 2: search_by_title(keyword);
			case 3: search_by_author(keyword);
		}
		return;
	}
	private static void search_by_callnum(String k) {
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery("SELECT book.callnum, title, LISTAGG(aname, ', ') WITHIN GROUP (ORDER BY aname) FROM book, authorship "
											+ "WHERE book.callnum=authorship.callnum AND book.callnum='" + k + "' "
											+ "GROUP BY book.callnum, title");

			System.out.println("Callnum\tTitle\tAuthor\tAvailable Copy Num");
			while (rs1.next()){
				System.out.print(rs1.getString(1) + "\t" + rs1.getString(2) + "\t" + rs1.getString(3) + "\t");
				stmt = conn.createStatement();
				ResultSet rs2 = stmt.executeQuery("SELECT MAX(copynum) FROM copy WHERE callnum='" + rs1.getString(1) + "'");
				rs2.next();
				stmt = conn.createStatement();
				ResultSet rs3 = stmt.executeQuery("SELECT SUM(CASE WHEN return IS NULL THEN 1 ELSE 0 END) FROM borrow WHERE callnum='" + rs1.getString(1) + "'");
				rs3.next();
				System.out.println(rs2.getInt(1)-rs3.getInt(1));
				rs2.close();
				rs3.close();
			}
				
			System.out.println("End of Query");
			rs1.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}
		finally{
			menu();
			return;
		}
	}
	private static void search_by_title(String k) {
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery("SELECT book.callnum, title, LISTAGG(aname, ', ') WITHIN GROUP (ORDER BY aname) FROM book, authorship "
											+ "WHERE book.callnum=authorship.callnum AND title LIKE '%" + k + "%' "
											+ "GROUP BY book.callnum, title ORDER BY book.callnum ASC");
										
			System.out.println("Callnum\tTitle\tAuthor\tAvailable Copy Num");
			while (rs1.next()){
				System.out.print(rs1.getString(1) + "\t" + rs1.getString(2) + "\t" + rs1.getString(3) + "\t");
				stmt = conn.createStatement();
				ResultSet rs2 = stmt.executeQuery("SELECT MAX(copynum) FROM copy WHERE callnum='" + rs1.getString(1) + "'");
				rs2.next();
				stmt = conn.createStatement();
				ResultSet rs3 = stmt.executeQuery("SELECT SUM(CASE WHEN return IS NULL THEN 1 ELSE 0 END) FROM borrow WHERE callnum='" + rs1.getString(1) + "'");
				rs3.next();
				System.out.println(rs2.getInt(1)-rs3.getInt(1));
				rs2.close();
				rs3.close();
			}

			System.out.println("End of Query");
			rs1.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}
		finally{
			menu();
			return;
		}
	}
	private static void search_by_author(String k) {
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery("SELECT book.callnum FROM book, authorship "
											+ "WHERE book.callnum=authorship.callnum AND aname LIKE '%" + k + "%' "
											+ "ORDER BY book.callnum ASC");
										
			System.out.println("Callnum\tTitle\tAuthor\tAvailable Copy Num");
			while (rs1.next()){
				stmt = conn.createStatement();
				ResultSet rs2 = stmt.executeQuery("SELECT book.callnum, title, LISTAGG(aname, ', ') WITHIN GROUP (ORDER BY aname) FROM book, authorship "
												+ "WHERE book.callnum=authorship.callnum AND book.callnum='" + rs1.getString(1) + "' "
												+ "GROUP BY book.callnum, title");
				rs2.next();
				System.out.print(rs2.getString(1) + "\t" + rs2.getString(2) + "\t" + rs2.getString(3) + "\t");
				stmt = conn.createStatement();
				ResultSet rs3 = stmt.executeQuery("SELECT MAX(copynum) FROM copy WHERE callnum='" + rs1.getString(1) + "'");
				rs3.next();
				stmt = conn.createStatement();
				ResultSet rs4 = stmt.executeQuery("SELECT SUM(CASE WHEN return IS NULL THEN 1 ELSE 0 END) FROM borrow WHERE callnum='" + rs1.getString(1) + "'");
				rs4.next();
				System.out.println(rs3.getInt(1)-rs4.getInt(1));
				rs2.close();
				rs3.close();
				rs4.close();
			}

			System.out.println("End of Query");
			rs1.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}
		finally{
			menu();
			return;
		}
	}
	private static void show_loan() {
		System.out.print("Enter The User ID: ");
		Scanner scan = new Scanner(System.in);
		String userid = scan.nextLine();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT libuid, name, address, cid FROM libuser WHERE libuid='" + userid + "'");
			rs.next();
			System.out.println("User ID: " + rs.getString(1));
			System.out.println("Name: " + rs.getString(2));
			System.out.println("Address: " + rs.getString(3));
			System.out.println("User Category: " + rs.getInt(4));
			rs = stmt.executeQuery("SELECT borrow.callnum, copynum, title, LISTAGG(aname, ', ') WITHIN GROUP (ORDER BY aname), checkout, return FROM borrow, book, authorship "
										+ "WHERE borrow.libuid='" + userid + "' "
										+ "AND borrow.callnum=book.callnum AND borrow.callnum=authorship.callnum "
										+ "GROUP BY borrow.callnum, copynum, title, checkout, return ORDER BY checkout DESC");
			System.out.println("Loan Record:");
			System.out.println("CallNum\tCopyNum\tTitle\tAuthor\tCheck-out\tReturned?");
			while (rs.next())
				System.out.println(rs.getString(1) + "\t" + rs.getInt(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4)+ "\t" + sdf.format(rs.getDate(5)) + "\t" + ((rs.getDate(6)==null)?"No":"Yes"));
			System.out.println("End of Query");
			rs.close();
			stmt.close();
			conn.close();	
		}catch(Exception e){
			System.out.println(e);
		}finally{
			menu();
			return;
		}
	}
	private static void show_overdue() {
		System.out.print("Type in the number of books: ");
		Scanner scan = new Scanner(System.in);
		int N = scan.nextInt();
		try{ 
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT borrow.callnum, title, count(*) FROM borrow, libuser, category, book "
											+"WHERE borrow.libuid=libuser.libuid AND libuser.cid=category.cid AND borrow.callnum=book.callnum "
											+"AND return>checkout+period AND ROWNUM<=" + N + " GROUP BY borrow.callnum, title ORDER BY count(*) DESC");
			System.out.printf("%s\t%s\t%s\n", "CallNum", "Title", "Total overdue num");
			while(rs.next())
				System.out.printf("%s\t%s\t%s\n", rs.getString(1), rs.getString(2), rs.getInt(3));
			System.out.println("End of Query");
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}finally{
			menu();
			return;
		}
	}
	private static void show_checkedout() {
		System.out.print("Type in the starting date [dd/mm/yyyy]: ");
		Scanner scan = new Scanner(System.in);
		String start = scan.nextLine();
		System.out.print("Type in the ending date [dd/mm/yyyy]: ");
		String end = scan.nextLine();
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection conn = DriverManager.getConnection(
			"jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
			"db024", "lrvetudf");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM borrow WHERE checkout BETWEEN to_date('"
											+ start + "', 'dd/mm/yyyy') AND to_date('"
											+ end + "', 'dd/mm/yyyy')");
			rs.next();
			System.out.printf("Total books checked-out within the period [%s, %s] is: %d\n", start, end, rs.getInt(1));
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			System.out.println(e);
		}finally{
			menu();
			return;
		}
	}
	private static void exit() {
        System.exit(1);
    }
	public static void main (String args[]) {
	  System.out.println("Welcome to library inquiry System!");
	  menu();
	  }
}




