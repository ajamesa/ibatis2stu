package cd.test;

import java.util.StringTokenizer;

/**
 * @author James Chen
 * @date 2019/7/7.
 */
public class StringTokenTest {

    public static void main(String[] args){
        String sqlStatement = "1#2#3";
        StringTokenizer parser = new StringTokenizer(sqlStatement, "#", false);
//        parser.nextToken();
        while (parser.hasMoreTokens()){
            String token = parser.nextToken();
            System.out.println(token);
        }
    }
}
