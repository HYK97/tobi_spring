package RealConnection.dao;

import RealConnection.domain.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    User get(String id);
    int getCount();
    void deleteAll();
    List<User> getAll();
    int update(User user);


}
