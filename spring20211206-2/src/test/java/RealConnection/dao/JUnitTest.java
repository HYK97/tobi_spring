package RealConnection.dao;


import jdk.nashorn.internal.runtime.arrays.IteratorAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/web/WEB-INF/JUnitTest.xml")
public class JUnitTest {

    @Autowired
    ApplicationContext context;

    static Set<JUnitTest> test=new HashSet<JUnitTest>();
    static ApplicationContext contextObject =null;

    @Test
    public void unitTest1() {
        System.out.println("test.hashCode() = " + this.context.toString());
        assertThat(test,not(hasItem(this)));
        test.add(this);
        assertThat(contextObject == null || contextObject ==this.context,is(true));
        contextObject=this.context;
    }
    @Test
    public void unitTest2() {
        System.out.println("test.hashCode() = " + this.context.toString());
        assertThat(test,not(hasItem(this)));
        test.add(this);
        assertTrue(contextObject==this.context || contextObject==null);
        contextObject=this.context;

    }
    @Test
    public void unitTest3() {
        System.out.println("test.hashCode() = " + this.context.toString());
        assertThat(test,not(hasItem(this)));
        test.add(this);
        assertThat(contextObject,either(is(nullValue())).or(is(this.context)));
        contextObject=this.context;

     /*   Iterator<JUnitTest> iter= test.iterator();
        while (iter.hasNext()){
            JUnitTest jUnitTest = iter.next();
            System.out.println("jUnitTest.toString() = " + jUnitTest.toString());
        }*/


    }
}
