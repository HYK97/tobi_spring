package RealConnection.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object>{
    private Object target; //트랜잭션 타깃
    private PlatformTransactionManager transactionManager; //트랜젝션 매니저
    private String pattern;
    Class<?> serviceInterface;


    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        TransactionHandler transactionHandler =new TransactionHandler();
        transactionHandler.setTransactionManager(transactionManager);
        transactionHandler.setPattern(pattern);
        transactionHandler.setTarget(target);
        return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{serviceInterface},transactionHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
