package learningtest.template;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public Integer calcSum(String filepath) throws IOException {
           return fileReadTempleate(filepath, br -> {
               Integer sum =0;
               String line =null;
               while ((line = br.readLine()) != null) {
                   sum += Integer.valueOf(line);
               }
               return sum;
           });
        }
    public Integer calcMuliply(String filepath) throws IOException {
        return fileReadTempleate(filepath, br -> {
            Integer sum =1;
            String line =null;
            while ((line = br.readLine()) != null) {
                sum *= Integer.valueOf(line);
            }
            return sum;
        });
    }



    public Integer fileReadTempleate(String filepath,BufferedReaderCallback callback) throws IOException {
        BufferedReader br =null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            String line =null;
            int ret= callback.doSomethingWithReader(br);
            return ret;
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
