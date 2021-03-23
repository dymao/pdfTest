package com.mervin.eum;

/***************************************
 * @author: mervin
 * @Date: 2021-03-23 12:06
 * @Description: 转换结果枚举类
 ***************************************/
public enum ResultEnum {

    RESULT_ERROR(-1, "转换异常!请联系系统管理员!"),
    RESULT_SUCCESS(0, "转换成功!"),
    RESULT_FILE_NOT_EXISTS(2, "选择路径不存在PDF文件，请检查!"),
    RESULT_FILE_EMPTY(3, "PDF文件读取的内容为空，请检查!"),
    RESULT_PART_SUCCESS_FILE_MOVE(4, "部分转换成功，有文件被移走："),
    RESULT_PART_SUCCESS_IO_ERROR(5, "部分转换成功，加载PDF文件时出现异常："),
    RESULT_PART_SUCCESS_PAGE_ERROR(6, "部分转换成功，有文件页数转换差异："),
    RESULT_MERGE_ERROR(7, "合同图片文件时出现异常，请联系管理员！"),
    RESULT_SAVE_ERROR(8, "保存文件时出现异常，请联系管理员！"),
    RESULT_OTHER(Integer.MIN_VALUE, "其他异常，未知转换码！"),
    ;

    ResultEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ResultEnum getResult(int code){
        for (ResultEnum value : ResultEnum.values()) {
            if(value.code == code){
                return value;
            }
        }
        return RESULT_OTHER;
    }


    /**
     * 返回码
     */
    private int code;

    /**
     * 状态描述
     */
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
