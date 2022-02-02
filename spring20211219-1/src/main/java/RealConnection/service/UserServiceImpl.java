package RealConnection.service;

import RealConnection.dao.UserDao;
import RealConnection.domain.Level;
import RealConnection.domain.User;

import java.util.*;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceImpl implements UserService{


    UserDao userDao;

    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public void upgradeLevels() { // 메소드 분리방식
            List<User> users =userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
    }

    public static final int MiN_LOGIN_FOR_SILVER =50;
    public static final int MIN_RECOMMEND_FOR_GOLD =30;
    protected boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MiN_LOGIN_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalStateException("없는레벨입니다."+ currentLevel);
        }
    }


    protected void upgradeLevel(User user) {

        user.upgradeLevel();
    
        userDao.update(user);

        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {


        SimpleMailMessage mailMessage =new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@kusug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "으로 업그레이드 되었습니다.");
        this.mailSender.send(mailMessage);

    }


    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }


    static class TestUserServiceImpl extends UserServiceImpl {

        private String id;

        protected TestUserServiceImpl(String id) {
            this.id =id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }

    }
    static class TestUserServiceException extends RuntimeException {
    }



    }



