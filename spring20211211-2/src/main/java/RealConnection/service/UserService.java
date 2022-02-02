package RealConnection.service;

import RealConnection.dao.UserDao;
import RealConnection.domain.Level;
import RealConnection.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserService {


    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
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
    }


    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }


    static class TestUserService extends UserService {
    private String id;

        protected TestUserService(String id) {
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

