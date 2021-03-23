package com.mervin.util;

import java.util.List;

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
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String combineList(List<String> msgList){
        if(msgList == null || msgList.size() <= 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < msgList.size(); i++) {
            if(i == msgList.size() -1){
                sb.append(msgList.get(i));
            }else{
                sb.append(msgList.get(i) + ",");
            }
        }
        return sb.toString();
    }
}
