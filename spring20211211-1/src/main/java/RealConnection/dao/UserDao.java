package RealConnection.dao;

import RealConnection.domain.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    User getUser(String id);
    int getCount();
    void allDelete();
    List<User> getAll();

}
