package RealConnection.service;

import RealConnection.dao.UserDao;
import RealConnection.domain.Level;
import RealConnection.domain.User;
import com.sun.istack.internal.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    UserService userService;

    @Autowired
    UserDao userDao;

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
        new User("bumjin","박범진","p1", Level.BASIC,MiN_LOGIN_FOR_SILVER-1,0),
        new User("joytouch","강명성","p2", Level.BASIC,MiN_LOGIN_FOR_SILVER,10),
        new User("erwins","신승한","p3", Level.SILVER,60,MIN_RECOMMEND_FOR_GOLD-1),
        new User("madnite1","이상호","p3", Level.SILVER,60,MIN_RECOMMEND_FOR_GOLD),
        new User("green","오민규","p3", Level.GOLD,100,Integer.MAX_VALUE)
        );
    }

    @Test
    public void bean() {
        assertThat((userService), is(notNullValue()));
    }

    @Test
    public void upgradeLevels() {
        userDao.allDelete();
        for (User user : users) {
            userDao.add(user);
        }
        userService.upgradeLevels();


        checkLevelUpgraded(users.get(0),false);
        checkLevelUpgraded(users.get(1),true);
        checkLevelUpgraded(users.get(2),false);
        checkLevelUpgraded(users.get(3),true);
        checkLevelUpgraded(users.get(4),false);

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
    public void upgradeAllOrNothing() {
        UserService testUserService = new UserService.TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        userDao.allDelete();
        for (User user : users) {
            userDao.add(user);
        }
        try {
            testUserService.upgradeLevels();
            fail("test실패");
        } catch (UserService.TestUserServiceException e) {
        }
        checkLevelUpgraded(users.get(1),false);

    }




}