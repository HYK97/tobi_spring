package springbook.learningtest.jdk;

import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/web/WEB-INF/applicationContext.xml")
public class ReflectionTest {

    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        assertThat(name.length(),is(6));

        Method lengthMethod = String.class.getMethod("length");

        assertThat(name.charAt(0),is('S'));

        Method charAtMethod =String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name,0), is('S'));


    }

    @Test
    public void simpleProxy() {
       Hello proxiedHello = (Hello) Proxy.newProxyInstance(
               getClass().getClassLoader(),
               new Class[] {Hello.class},
               new UppercaseHandler(new HelloTarget())
       );


    }

    @Test
    public void proxyFactoryBean(){
        ProxyFactoryBean pfBean =new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();
        assertThat(proxiedHello.sayHello("HY"),is("HELLO HY"));
        assertThat(proxiedHello.sayHi("Hy"),is("HI HY"));
        assertThat(proxiedHello.sayThankyou("Hy"),is("THANK YOU HY"));

    }
    @Test
    public void pointcutAdvisor(){
        ProxyFactoryBean pfBean =new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut =new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("HY"),is("HELLO HY"));
        assertThat(proxiedHello.sayHi("Hy"),is("HI HY"));
        assertThat(proxiedHello.sayThankyou("Hy"),is("Thank You Hy"));
    }

    static class UppercaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            return ret.toUpperCase();
        }
    }

    @Test
    public void UpperProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class}, //구현할 메소드들을 다만들어준다 구현할 인터페이스
                new UppercaseHandler(new HelloTarget())   // 부가기능과 위임코드를 담은 코드
        );

        assertThat(proxiedHello.sayHello("HY"),is("HELLO HY"));
        assertThat(proxiedHello.sayHi("Hy"),is("HI HY"));
        assertThat(proxiedHello.sayThankyou("Hy"),is("THANK YOU HY"));

    }





}