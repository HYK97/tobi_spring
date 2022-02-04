
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;




public class test {

    @Test
    public void HelloClassBean () throws Exception{
        StaticApplicationContext ac = new StaticApplicationContext();// ioc 컨테이너 생성.
        ac.registerSingleton("hello1", Hello.class); //싱글톤 빈으로 생성할때.

        Hello hello1 = ac.getBean("hello1", Hello.class);
        assertThat(hello1, is(notNullValue()));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class); // 빈 오브젝트 생성.. 은 Root BeanDefinition 으로 만든다.
        helloDef.getPropertyValues().addPropertyValue("name", "Spring"); //빈의 name 프로퍼티 즉 setter에 들어갈 값을 지정하는것.
        ac.registerBeanDefinition("hello2", helloDef);//생성한 빈 메타정보를 hello2 라는 이름을 가진 빈으로 해서 등록하는것.

        Hello hello2 = ac.getBean("hello2", Hello.class);
        assertThat(hello2.sayHello(), is("Hello Spring"));
        assertThat(hello1, is(not(hello2)));

        assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));

    }
    @Test
    public void registerBeanWithDependency() {
        StaticApplicationContext ac = new StaticApplicationContext();

        ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class); //<bean id="hello" class ="~~ hello.class " >
        helloDef.getPropertyValues().addPropertyValue("name", "Spring"); //<property name= "name" value="Spring"/>

        helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));//<property name= "printer" ref="~~~.printer"/>
        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class); // 위에 생성한 빈을 불러옴.
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
    }
}

