package RealConnection.test;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/web/WEB-INF/applicationContext.xml")
public class FactoryBeanTest {
    @Autowired
    ApplicationContext context;

    @Test
    public void getMessage() {
        Object message = context.getBean("message");

        assertThat(message,instanceOf(Message.class));
        assertThat(((Message)message).getText(),is("factoryBean"));
    }
    @Test
    public void getMessageFactory() {
        Object factory = context.getBean("&message");

        assertThat(factory,instanceOf(MessageFactoryBean.class));

    }


}
