package learningtest.template;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CalculatorTest {
    Calculator calculator;
    String numFilepath;

    @Before
    public void setUp() {
        this.calculator = new Calculator();
        this.numFilepath = getClass().getResource("number.txt").getPath();
    }

    @Test
    public void sumOfNumber()throws IOException {
        assertThat(calculator.calcSum(numFilepath),is(10));

    }


    @Test
    public void multiplyOfNumbers()throws IOException {
        assertThat(calculator.calcMuliply(numFilepath),is(24));
    }
    @Test
    public void concatenateOfString() throws IOException {
        System.out.println("numFilepath = " + numFilepath);
        assertThat(calculator.concatenate(numFilepath),is("1234"));
    }




}