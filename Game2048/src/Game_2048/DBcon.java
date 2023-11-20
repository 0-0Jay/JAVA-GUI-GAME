package Game_2048;

import java.sql.*;
import java.util.ArrayList;

public class DBcon  {
	String url = "jdbc:oracle:thin:@10.30.3.95:1521:orcl";
	String user = "C##2201091";
	String pw = "p2201091";
	Connection con;
	Statement st;
	
	public DBcon() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("드라이버 적재 완료");
			con = DriverManager.getConnection(url, user, pw);
			st = con.createStatement();
			con.setAutoCommit(true);
			
		} catch (ClassNotFoundException e) {
			System.out.println("error");
		} catch (SQLException e) {
			System.out.println("error");
		}
	}
	
	public int IDcheck(String id, String p) throws SQLException {
		ResultSet rs = st.executeQuery("SELECT * FROM users WHERE id = '" + id + "'");
		if (!rs.next()) return 0; // 아이디 없음
		String dbid = rs.getString(1);
		String dbpw = rs.getString(2);
		if (dbid.equals(id) && dbpw.equals(p)) return 1; // 로그인 성공
		if (dbid.equals(id) && !dbpw.equals(p)) return 2; // 암호 틀림
		return 4; // 오류 발생
	}
	
	public int createID(String id, String p) throws SQLException {
		ResultSet rs = st.executeQuery("SELECT * FROM users WHERE id = '" + id + "'");
		if (!rs.next()) {  // 이미 있는 회원인지 확인
			PreparedStatement pst = con.prepareStatement("INSERT INTO users(id, pw, highscore) VALUES (?, ?, ?)");
			pst.setString(1, id);
			pst.setString(2, p);
			pst.setInt(3, 0);
			pst.executeUpdate();
			return 1;
		} else {  // 있는 회원이면 
			return 0;
		}
	}
	
	public void scoreUpdate(String id, int score) throws SQLException {
		PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE id = ?");
		pst.setString(1, id);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) {
			int before = rs.getInt("highscore");
			if (score > before) {
				pst = con.prepareStatement("UPDATE users SET highscore = ? WHERE id = ?");
				pst.setInt(1, score);
				pst.setString(2, id);
				pst.executeUpdate();
			}
		} 
	}
	
	public ArrayList<Object[]> scoreView() throws SQLException {
		PreparedStatement pst = con.prepareStatement("SELECT id, highscore FROM users WHERE highscore > 0 ORDER BY highscore desc");
		ResultSet rs = pst.executeQuery();
		ArrayList<Object[]> scoreboard = new ArrayList<Object[]>();
		while(rs.next()) {
			String dbid = rs.getString("id");
			int dbscore = rs.getInt("highscore");
			scoreboard.add(new Object[] {dbid, dbscore});
		}
		
		return scoreboard;
	}
}
