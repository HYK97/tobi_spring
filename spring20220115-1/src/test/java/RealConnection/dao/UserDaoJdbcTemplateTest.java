package RealConnection.dao;

import RealConnection.domain.Level;
import RealConnection.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/web/WEB-INF/applicationContext.xml")
public class UserDaoJdbcTemplateTest {



    @Autowired
    private UserDaoJdbcTemplate dao;

   /* @Autowired
    private SimpleDriverDataSource simpleDriverDataSource;*/

    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setup() {


        //given
        this.user1 = new User("gyumee", "박성철", "springno1", "user1@ksug.org", Level.BASIC, 1, 0);
        this.user2 = new User("leegw700", "이길원", "springno2", "user2@ksug.org", Level.SILVER, 55, 10);
        this.user3 = new User("bumjin", "박범진", "springno3", "user3@ksug.org", Level.GOLD, 100, 40);

    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {

        //when
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));


        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(),is(2));

        //then
        User result1 =dao.get(user1.getId());
        checkSameuser(user1,result1);

        User result2 =dao.get(user2.getId());
        checkSameuser(user2,result2);

    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        dao.add(user1);
        assertThat(dao.getCount(),is(1));

        dao.add(user2);
        assertThat(dao.getCount(),is(2));
    }

    @Test(expected = EmptyResultDataAccessException.class) // 이방식은 junit 4버전에서 사용하는 테스트기법이다.
    public void getFailure() throws SQLException, ClassNotFoundException {


        dao.deleteAll();
        assertThat(dao.getCount(),is(0));
        dao.get("없는 id");
        //다음과같이 junit 5 에서는 람다로 사용
//        assertThrows(EmptyResultDataAccessException.class, () -> {  //junit 5 일때 사용하는 테스트 기법
//            dao.getUser("없는 id");
//
//        });
    }

    @Test
    public void getAll() throws SQLException {
        dao.deleteAll();

        assertThat(dao.getCount(),is(0));
        List<User> users0 = dao.getAll();
        assertThat(users0.size(),is(0));

        dao.add(user1);
        List<User> users1 =dao.getAll();
        assertThat(users1.size(),is(1));
        checkSameuser(this.user1,users1.get(0));


        dao.add(user2);
        List<User> users2 =dao.getAll();
        assertThat(users2.size(),is(2));
        checkSameuser(this.user1,users2.get(0));
        checkSameuser(this.user2,users2.get(1));


        dao.add(user3);
        List<User> users3 =dao.getAll();
        assertThat(users3.size(),is(3));
        checkSameuser(this.user1,users3.get(0));
        checkSameuser(this.user2,users3.get(1));
        checkSameuser(this.user3,users3.get(2));

        assertThat(dao.getCount(),is(3));

    }

    private void checkSameuser(User user1,User user2) {
        assertThat(user1.getId(),is(user2.getId()));
        assertThat(user1.getName(),is(user2.getName()));
        assertThat(user1.getPassword(),is(user2.getPassword()));
        assertThat(user1.getLevel(),is(user2.getLevel()));
        assertThat(user1.getLogin(),is(user2.getLogin()));
        assertThat(user1.getRecommend(),is(user2.getRecommend()));

    }


    @Test(expected = DataAccessException.class)
    public void duplicateKey() {
        dao.deleteAll();
        dao.add(user1);
        dao.add(user1);
    }


    @Test
    public void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);
        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update =dao.get(user1.getId());
        checkSameuser(user1update,user1);


        User user2update =dao.get(user2.getId());
        checkSameuser(user2update,user2);


    }


    @After
    public void clear() throws SQLException, ClassNotFoundException {
        dao.deleteAll();

    }



}
