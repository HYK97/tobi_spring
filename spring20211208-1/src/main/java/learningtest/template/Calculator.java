package learningtest.template;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public Integer calcSum(String filePath) throws IOException {
        return lineReadTempleate(filePath, (line, value) ->{
                    return value+Integer.valueOf(line) ;
                }
                ,0);
    }


    public Integer calcMuliply(String filePath) throws IOException {
        return lineReadTempleate(filePath, (line, value) -> { //람다
            return Integer.valueOf(line)*value;
        },1);
    }


    public String concatenate(String filePath) throws IOException {
        return lineReadTempleate(filePath, new LineCallback<String>() { //익명클래스
                    @Override
                    public String doSomethingWithLine(String line, String value) {
                        return line + value;

                    }
                }
        ,"");
    }



    public <T>T lineReadTempleate(String filePath, LineCallback<T> callback, T intiVal) throws IOException {
        BufferedReader br =null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line =null;
            T res = intiVal;
            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }






}
