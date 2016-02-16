package reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
	// JDBCドライバの登録
    String driver;
    // データベースの指定
    String server, dbname, url, user, password;
    Connection con;
    Statement stmt;
    ResultSet rs;
	public MySQL(){
	    this.driver  = "org.gjt.mm.mysql.Driver";
		this.server  = "j11000.sangi01.net";		// MySQLサーバ ( IP または ホスト名 );
		this.dbname  = "50416007";					// データベース名;
		this.url = "jdbc:mysql://" + server + "/" + dbname + "?useUnicode=true&characterEncoding=UTF-8";
		this.user = "50416007";						// データベース作成ユーザ名;
		this.password  = "50416007";				// データベース作成ユーザパスワード;
		try {
			this.con = DriverManager.getConnection(url, user, password);
			this.stmt = con.createStatement ();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close(){
		try {
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ResultSet getReservation(String rdate, String facility){
		String sql = "SELECT * FROM reservation WHERE date ='" + rdate + "' AND facility_name = '"+ facility +"' ORDER BY start_time;";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery (sql);  //try catchで囲む
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public ResultSet getLogin(String reservation_userid){
		String sql = "SELECT * FROM user WHERE user_id ='" + reservation_userid + "';";
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return rs;
		
	}
	public ResultSet selectReservation(String rdate, String facility){
		
		String sql = "SELECT * FROM reservation WHERE facility_name ='" + facility +
			      "' AND date = '" + rdate + "' ;";
			      // クエリーを実行して結果のセットを取得
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public int setReservation(String rdate, String st, String et, String reservation_userid, String facility){
		int rs_int = 0;
		String sql = "INSERT INTO reservation (date,start_time,end_time,user_id,facility_name) VALUES ( '"
		    + rdate +"', '"  + st +"','" + et + "','" + reservation_userid +"','" + facility +"');";
		    	  try {
					rs_int = stmt.executeUpdate(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return rs_int;
	}


	public ResultSet getReservationId(String id){
		String sql = "SELECT * FROM reservation WHERE user_id ='" + id+ "' ORDER BY date;";;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery (sql);  //try catchで囲む
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	public ResultSet getFacility(String facility){
		String sql = "SELECT * FROM facility WHERE facility_name ='" + facility+ "';";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery (sql);  //try catchで囲む
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	
	public ResultSet deletechcekReservation(String rdate, String st, String et, String reservation_userid, String facility){
		String sql = "SELECT * FROM reservation WHERE date ='"+ rdate +"' AND start_time ='"  + st +
				"' AND end_time ='" + et + "' AND user_id ='" + reservation_userid +"' AND facility_name ='" + facility +"';";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public void deleteReservation(String rdate, String st, String et, String reservation_userid, String facility){
		String sql = "DELETE FROM reservation WHERE date ='"+ rdate +"' AND start_time ='"  + st +
				"' AND end_time ='" + et + "' AND user_id ='" + reservation_userid +"' AND facility_name ='" + facility +"';";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
}
