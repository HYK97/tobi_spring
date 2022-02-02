package RealConnection.dao;

import RealConnection.domain.Level;
import RealConnection.domain.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class UserDaoJdbcTemplate implements UserDao {

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
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			user.setEmail(rs.getString("email"));

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
		this.jdbcTemplate.update("insert into user values(?,?,?,?,?,?,?)", user.getId(), user.getName(), user.getPassword(),user.getLevel().intValue(),user.getLogin(),user.getRecommend(),user.getEmail());

	}

	public User get(String id) {

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


	public void deleteAll() {

		this.jdbcTemplate.update("delete from user");

	}


	public List<User> getAll() {
		return jdbcTemplate.query("select * from user order by id"
				, this.rowMapper);
	}


	@Override
	public int update(User user) {

		return jdbcTemplate.update("update user set name =?,password =? ,level =?,login =?,recommend=?,email=? where id=?", user.getName(), user.getPassword(),user.getLevel().intValue(),user.getLogin(),user.getRecommend(),user.getEmail(),user.getId());

	}
}
