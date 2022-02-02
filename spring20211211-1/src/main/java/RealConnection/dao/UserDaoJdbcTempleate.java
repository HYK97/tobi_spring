package RealConnection.dao;

import RealConnection.domain.User;
import RealConnection.exceptions.DuplicateUserIdException;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
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


public class UserDaoJdbcTempleate implements UserDao {

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
		this.rowMapper =user;d
	}
*/

	public void setJdbcTemplate(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	public void add(User user) //throws DuplicateUserIdException -> 아래주석과 같이 사용하면 예외포장을하는것이다.
	{

//		try {
//			this.jdbcTemplate.update("insert into user values(?,?,?)", user.getId(), user.getName(), user.getPassword());
//		} catch (DataAccessException e) {
//			throw new DuplicateUserIdException(e);
//		} finally {
//		}
		this.jdbcTemplate.update("insert into user values(?,?,?)", user.getId(), user.getName(), user.getPassword());

	}

	public User getUser(String id) {

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
	public int getCount() {
		return jdbcTemplate.queryForInt("select count(*) from user ;");
	}


	public void allDelete() {

		this.jdbcTemplate.update("delete from user");

	}


	public List<User> getAll() {
		return jdbcTemplate.query("select * from user order by id"
				, this.rowMapper);
	}
}
