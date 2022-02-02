package springbook.learningtest.jdk;

import org.junit.Test;
import org.junit.runner.RunWith;
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
        Hello hello=new HelloTarget();

        assertThat(hello.sayHello("Hy"),is("Hello Hy"));
        assertThat(hello.sayHi("Hy"),is("Hi Hy"));
        assertThat(hello.sayThankyou("Hy"),is("Thank You Hy"));

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