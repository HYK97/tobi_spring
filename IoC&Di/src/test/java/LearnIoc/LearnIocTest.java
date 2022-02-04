package LearnIoc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;



public class LearnIocTest {
    //private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";
    private String basePath = "web/LearnIoc/";


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
        assertThat(hello2.sayHello(), is("LearnIoc.Hello Spring"));
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

        assertThat(ac.getBean("printer").toString(), is("LearnIoc.Hello Spring"));
    }


    @Test
    public void GenericApplicationContextTest() throws Exception{
        //설정하기
        GenericXmlApplicationContext ac1 =new GenericXmlApplicationContext("web/WEB-INF/applicationContext.xml");
        // 혹은 아래와 같이 가져와서 사용
        GenericApplicationContext ac2 =new GenericApplicationContext();
        XmlBeanDefinitionReader reader =new XmlBeanDefinitionReader(ac2);
        reader.loadBeanDefinitions("web/WEB-INF/applicationContext.xml");

        // 컨텍스트 초기화
        ac2.refresh();
        //ac1 인 xmlApplicationContext는 refresh를 자동으로 초기화 해줘서 ㅎ써줄필요가없다.


        Hello hello1 =ac1.getBean("hello",Hello.class);
        hello1.print();

        assertThat(ac1.getBean("printer").toString(),is("Hello Spring"));

        Hello hello2 =ac2.getBean("hello",Hello.class);
        hello2.print();

        assertThat(ac2.getBean("printer").toString(),is("Hello Spring"));


    }

    @Test
    public void treeContext() throws Exception{
    //given

        // 부모 Context 생성
        ApplicationContext parent =new GenericXmlApplicationContext(basePath+"parentContext.xml");
        //basePath 이슈 - > resources 하위 디렉터리에서만 xml 파일의 경로를 인시함 다른디렉터리에서 찾을시 사전 작업이 필요함.


        //자식 Context 생성
        GenericApplicationContext child =new GenericApplicationContext(parent);
        XmlBeanDefinitionReader reader =new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions(basePath+"childContext.xml");
        child.refresh(); //XmlBead reader를 사용해서 설정을 읽으면 항상 refresh 를 해줘야된다.


    //when
        Printer printer = child.getBean("printer", Printer.class);
        assertThat(printer, is(notNullValue()));


        Hello hello = child.getBean("hello", Hello.class);
        assertThat(hello, is(notNullValue()));
        parent.getBean("printer",Printer.class).toString();

        hello.print();
        assertThat(printer.toString(), is("Hello Child"));


    //then

    }
}

