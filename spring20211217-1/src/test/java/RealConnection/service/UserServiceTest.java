package RealConnection.service;

import RealConnection.dao.UserDao;
import RealConnection.domain.Level;
import RealConnection.domain.User;
import com.sun.istack.internal.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static RealConnection.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static RealConnection.service.UserService.MiN_LOGIN_FOR_SILVER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/web/WEB-INF/applicationContext.xml")
public class UserServiceTest {

    @Autowired
    MailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;



    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(

                new User("bumjin", "박범진", "p1", "user1@ksug.org", Level.BASIC, MiN_LOGIN_FOR_SILVER-1, 0),
                new User("joytouch", "강명성", "p2", "user2@ksug.org", Level.BASIC, MiN_LOGIN_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", "user3@ksug.org", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1),
                new User("madnite1", "이상호", "p4", "user4@ksug.org", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new User("green", "오민규", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void bean() {
        assertThat((userService), is(notNullValue()));
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        userDao.allDelete();
        for(User user : users) {
            userDao.add(user);
        }

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);
        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));

        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));


    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage[] mailMessage) throws MailException {
        }
    }


    private void checkLevelUpgraded(User user,boolean upgraded) {
        User userUpdate = userDao.getUser(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel(),is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(),is(user.getLevel()));

        }
    }

    private void checkLevel(User user,Level expectedLevel) {
        User userUpdate = userDao.getUser(user.getId());
        assertThat(userUpdate.getLevel(),is(expectedLevel));
    }

    @Test
    public void add() {
        userDao.allDelete();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead=userDao.getUser(userWithLevel.getId());
        User userWithoutLevelRead=userDao.getUser(userWithoutLevel.getId());

        checkLevel(userWithLevelRead,userWithLevel.getLevel());
        checkLevel(userWithoutLevelRead,Level.BASIC);

    }




    @Test
    public void upgradeAllOrNothing() throws Exception {
        UserService testUserService = new UserService.TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setTransactionManager(this.transactionManager);
        userDao.allDelete();
        for (User user : users) {
            userDao.add(user);
        }
        try {
            testUserService.setMailSender(this.mailSender);
            testUserService.upgradeLevels();
            fail("test실패");
        } catch (UserService.TestUserServiceException e) {

        }
        checkLevelUpgraded(users.get(1),false);


    }




}