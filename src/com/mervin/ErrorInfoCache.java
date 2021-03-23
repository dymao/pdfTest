package com.mervin;

import com.mervin.eum.ResultEnum;
import com.mervin.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***************************************
 * @author: mervin
 * @Date: 2021-03-23 21:17
 * @Description: 错误信息容器
 ***************************************/
public class ErrorInfoCache {
    private static final Map<ResultEnum, List<String>> errorInfoMap = new ConcurrentHashMap<>();

    /**
     * 清空缓存
     */
    public static void clear(){
        errorInfoMap.clear();
    }

    /**
     * 判断是否有错误信息
     * @return
     */
    public static boolean isEmpty(){
        return errorInfoMap.isEmpty();
    }

    /**
     * 获取第一个错误信息
     * @return
     */
    public static ResultEnum getFirstResultEnum(){
        if(isEmpty()){
            return null;
        }
        return errorInfoMap.entrySet().iterator().next().getKey();
    }

    /**
     * 添加错误信息
     * @param errorEnum
     * @param msg
     */
    public static void addErrorInfo(ResultEnum errorEnum, String msg){
        List<String> msgList = errorInfoMap.get(errorEnum);
        if(msgList == null){
            msgList = new ArrayList<>();
            errorInfoMap.put(errorEnum, msgList);
        }
        msgList.add(msg);
    }

    /**
     * 获取详细提示信息
     * @param code
     * @return
     */
    public static String getInfoMsg(int code){
        ResultEnum resultEnum = ResultEnum.getResult(code);
        if(errorInfoMap.containsKey(resultEnum)){
            return resultEnum.getDesc() + Utils.combineList(errorInfoMap.get(resultEnum));
        }
        return resultEnum.getDesc();
    }


}
