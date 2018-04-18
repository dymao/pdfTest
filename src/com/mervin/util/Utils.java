package com.mervin.util;

/**
 * @author Mervin
 * @Description:
 * @date 2018-04-03 21:17
 */
public class Utils {

    public static boolean isNumeric(String str) {
        if(str == null || "".equals(str.trim())){
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            //System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
