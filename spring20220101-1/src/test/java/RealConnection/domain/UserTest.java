package RealConnection.domain;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UserTest {
    User user;

    @Before
    public void setup() {
        user =new User();
        
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) { //골드 실버-골드 베이직-실버
            if(level.nextLevel()==null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel(),is(level.nextLevel()));
        }

    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if(level.nextLevel()!=null)continue;
            user.setLevel(level);
            user.upgradeLevel();
        }

    }


}