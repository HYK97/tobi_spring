package RealConnection.service;

import RealConnection.domain.User;
import org.springframework.scheduling.quartz.SchedulerAccessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService{


    private PlatformTransactionManager transactionManager;
    UserService userService;


    public void setTransactionManager(PlatformTransactionManager transactionManager) { this.transactionManager = transactionManager;   }

    public void setUserService(UserService userService) { this.userService = userService; }


    @Override
    public void add(User user) {
            userService.add(user);

    }

    @Override
    public void upgradeLevels() {


        TransactionStatus status= this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            userService.upgradeLevels();
            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }

    }
}

