package RealConnection.dao;

import RealConnection.domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserDao {

	// connection 모으기


	private JdbcTemplate jdbcTemplate;

	private RowMapper<User> rowMapper=
			new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			return user;
		}
	};

	/*public void setRowMapper(RowMapper<User> user) {
		this.rowMapper =user;
	}
*/

	public void setJdbcTemplate(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public void add(User user) throws SQLException { //익명클래스

		this.jdbcTemplate.update("insert into user values(?,?,?)", user.getId(), user.getName(), user.getPassword());

	}

	public User getUser(String id) throws ClassNotFoundException, SQLException {

		return jdbcTemplate.queryForObject("select * from user where id = ?",
				new Object[]{id}
				, this.rowMapper);

	}

	/*public int getCount() throws SQLException  {
		return this.jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select count(*) from user");
			}
		}, new ResultSetExtractor<Integer>() {
			public Integer extractData(ResultSet rs) throws SQLException ,
					DataAccessException{
				rs.next();
				return rs.getInt(1);
			}
		});

	}   -> 이걸바꾸면 ?*/
	public int getCount() throws SQLException {
		return jdbcTemplate.queryForInt("select count(*) from user ;");
	}


	public void allDelete() throws SQLException {

		this.jdbcTemplate.update("delete from user");

	}


	public List<User> getAll() {
		return jdbcTemplate.query("select * from user order by id"
				, this.rowMapper);
	}
}
