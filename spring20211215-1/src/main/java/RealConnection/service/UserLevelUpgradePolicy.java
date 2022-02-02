package RealConnection.service;

import RealConnection.domain.User;

public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
    void add(User user);
    void upgradeLevels();

}
