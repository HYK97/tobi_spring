package RealConnection.service;

import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class TransactionHandler implements InvocationHandler {

    // 이 3가지는 di를 통해서 외부에서 주입해줌
    private Object target; //트랜잭션 타깃
    private PlatformTransactionManager transactionManager; //트랜젝션 매니저
    private String pattern;;


    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override  // args 는 매개변수
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith(pattern)) { //특정 패턴
            return invokeTransaction(method, args);
        } else {
            return method.invoke(target,args);

        }


    }

    private Object invokeTransaction(Method method, Object[] args) throws Throwable {
        TransactionStatus status=
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {

            Object ret =method.invoke(target,args);

            this.transactionManager.commit(status);

            return ret;
        } catch (InvocationTargetException e) {

            this.transactionManager.rollback(status);

            throw e.getTargetException();
        }

    }

    @Test
    public void ClassforNameTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Date now = (Date) Class.forName("java.util.Date").newInstance();
        System.out.println("now = " + now);
    }

}