package RealConnection.dao;

import RealConnection.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDao {
	
	// connection ������
	private DataSource DataSource;

	private JdbcContext jdbcContext;



	public void setDataSource(DataSource DataSource) {
		this.jdbcContext= new JdbcContext();
		this.DataSource = DataSource;
		this.jdbcContext.setDataSource(DataSource);
	}

	public void add(User user) throws  SQLException { //�͸�Ŭ����



		jdbcContext.workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("insert into user values(?,?,?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		});


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

	public int getCount() throws SQLException  {
		Connection conn =null;
		PreparedStatement ps =null;
		ResultSet rs =null;
		int count = 0;

		try {
			conn =DataSource.getConnection();
			ps =conn.prepareStatement("select count(*) from user");
			rs = ps.executeQuery();
			rs.next();
			return count =rs.getInt(1);

		} catch (SQLException e) {
			throw e;
		}finally {

			try {
				rs.close();
			} catch (SQLException e) {
			}
			try {
				ps.close();
			} catch (SQLException e) {
			}
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}





	}
	
	
	public void allDelete() throws SQLException  {

		jdbcContext.executeSql("delete from user");

	}








}
