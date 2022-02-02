package RealConnection.service;

import RealConnection.dao.UserDao;
import RealConnection.domain.Level;
import RealConnection.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Arrays;
import java.util.List;

import static RealConnection.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static RealConnection.service.UserServiceImpl.MiN_LOGIN_FOR_SILVER;
import static org.hamcrest.CoreMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/web/WEB-INF/applicationContext.xml")
public class UserServiceImplTest {

    @Autowired
    MailSender mailSender;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    UserService userService;



    @Autowired
    UserService testUserService;

    @Autowired
    UserDao userDao;

    @Autowired
    ApplicationContext context;



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



    @Test  //mockito 사용 테스트코드
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl =new UserServiceImpl();

        UserDao mockUserDao= mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);

        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao,times(2)).update(Matchers.any(User.class));
        verify(mockUserDao,times(2)).update(Matchers.any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(),is(Level.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(),is(Level.GOLD));



        ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor=
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mockMailSender,times(2)).send(mailMessageArgumentCaptor.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArgumentCaptor.getAllValues();

        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));



    }

    /*@Test // 목오브젝트 직접사용 테스트코드
    public void upgradeLevels() throws Exception {

        UserServiceImpl userServiceImpl =new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users); //add 할
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated =mockUserDao.getUpdated();
        assertThat(updated.size(),is(2));
        checkUserAndLevel(updated.get(0),"joytouch",Level.SILVER);
        checkUserAndLevel(updated.get(1),"madnite1",Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));


    }

    private void checkUserAndLevel(User updated, String expectedId,Level expectedLevel) {
        assertThat(updated.getId(),is(expectedId));
        assertThat(updated.getLevel(),is(expectedLevel));

    }


   static class MockUserDao implements UserDao { //userDao 전용 mock 오브젝트



        private List<User> users;

        private List<User> updated = new ArrayList();

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return updated;
        }

        @Override
        public List<User> getAll() {
            return this.users; //받아온것처럼
        }

        @Override
        public int update(User user) {
            updated.add(user);
            return 1;
        }

        // 사용안하는 메서드
        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User getUser(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void allDelete() {
            throw new UnsupportedOperationException();
        }

    }
    static class MockMailSender implements MailSender { //메일 확인 전용 목오브젝트

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


    */







    private void checkLevelUpgraded(User user,boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel(),is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(),is(user.getLevel()));

        }
    }

    private void checkLevel(User user,Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(),is(expectedLevel));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead=userDao.get(userWithLevel.getId());
        User userWithoutLevelRead=userDao.get(userWithoutLevel.getId());

        checkLevel(userWithLevelRead,userWithLevel.getLevel());
        checkLevel(userWithoutLevelRead,Level.BASIC);

    }








    @Test

    public void upgradeAllOrNothing() throws Exception {

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }
        try {
            this.testUserService.upgradeLevels();
            fail("test실패");
        } catch (UserServiceImpl.TestUserServiceException e) {

        }
        checkLevelUpgraded(users.get(1),false);


    }
    @Test
    public void advisorAutoProxyCreator() {
        assertThat(testUserService, instanceOf(java.lang.reflect.Proxy.class));
    }



    @Test//(expected=TransientDataAccessResourceException.class)
    //중요 h2 db는 지원하지않는다. https://github.com/scratchstudio/toby-spring/issues/7
   /* h2 인메모리 DB 를 사용할때는 read-only 속성의 트랜잭션 안에서 update 를 실행해도 예외가 발생하지 않고 성공한다.
            반면, MySQL 에서는 TransientDataAccessResourceException 예외가 발생한다.*/

    public void readOnlyTransactionAttribute() {
        testUserService.getAll();
    }




    @Test
    public void transactionSync() {

        userDao.deleteAll();
        assertThat(userDao.getCount(),is(0));

        DefaultTransactionDefinition txDefiontion =new DefaultTransactionDefinition();

        TransactionStatus txStatus = transactionManager.getTransaction(txDefiontion);

        userService.add(users.get(0));
        userService.add(users.get(1));
        assertThat(userDao.getCount(),is(2));

        transactionManager.rollback(txStatus);
        assertThat(userDao.getCount(),is(0));


    }


    @Test //롤백테스트 기존의 데이터를 유지할수있음
    public void transactionSync2() {


        DefaultTransactionDefinition txDefiontion =new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefiontion);

        try{
        userDao.deleteAll();
        userService.add(users.get(0));
        userService.add(users.get(1));

        }finally {

            transactionManager.rollback(txStatus);
        }

    }




    @Test
    @Transactional
    //@Rollback(value = false)
    public void transactionSync3() {


            userDao.deleteAll();
            userService.add(users.get(0));
            userService.add(users.get(1));


    }

}