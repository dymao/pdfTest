package com.mervin.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/***************************************
 * @author: mervin
 * @Date: 2021-03-23 11:28
 * @Description: 文件读取工具类
 ***************************************/
public class FileUtil {

    /**
     * 循环读取filepath及其子目录下所有符合指定后缀格式的文件
     * @param filepath
     * @param fileList
     * @param suffix
     * @return
     */
    public static List<String> readfile(String filepath, List<String> fileList, String suffix) {
        try {
            File file = new File(filepath);
            if (file.isFile() && file.getAbsolutePath().endsWith(suffix)) {
                fileList.add(file.getAbsolutePath());
            }else{
                File[] directoryFiles = file.listFiles();
                if(directoryFiles != null && directoryFiles.length > 0){
                    Arrays.stream(directoryFiles).forEach(itemFile -> {
                        readfile(itemFile.getAbsolutePath(), fileList, suffix);
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("出现异常...", e);
        }
        return fileList;
    }
}
