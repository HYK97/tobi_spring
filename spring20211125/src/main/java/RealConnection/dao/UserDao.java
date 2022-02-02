package RealConnection.dao;

import RealConnection.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDao {
	
	// connection 모으기
	private DataSource DataSource;


	public void setDataSource(DataSource DataSource) {
		this.DataSource = DataSource;
	}

	public void add(User user) throws ClassNotFoundException, SQLException {
		Connection conn = DataSource.getConnection();
		
		String sql ="insert into user(id,name,password) values(?,?,?)";
		PreparedStatement ps =conn.prepareStatement(sql);
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		ps.executeUpdate();
		ps.close();
		conn.close();

	}
	
	public User getUser(String id) throws ClassNotFoundException, SQLException {
		
		Connection conn = DataSource.getConnection();
		String sql ="select * from USER where ID = ?";
		PreparedStatement ps =conn.prepareStatement(sql);
		ps.setString(1, id);
		ResultSet rs =ps.executeQuery();

		User user =null;
		if(rs.next()){
		user= new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));

		};


		if(user == null) throw new EmptyResultDataAccessException(1);

		rs.close();
		ps.close();
		conn.close();
		
		return user;	
	
	}

	public int getCount() throws SQLException {
		Connection conn =DataSource.getConnection();
		String sql= "select count(*) from user";
		PreparedStatement ps =conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int count =rs.getInt(1);

		rs.close();
		ps.close();
		conn.close();
		return count;



	}
	
	
	public String allDelete() throws ClassNotFoundException, SQLException {
		
		Connection conn = DataSource.getConnection();
		String sql ="delete from user";
		PreparedStatement ps =conn.prepareStatement(sql);
		int result = ps.executeUpdate();
		
		
	
		ps.close();
		conn.close();
		
		return (result > 0) ? "성공 삭제 개수는 "+ result : "삭제할게없음";
	
	}
	
	
	

}
