package com.mervin;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mervin
 * @Description:
 * @date 2018-04-01 23:41
 */
public class PdfToJpgUtil {

    /**
     * 将pdf中的maxPage页，转换成一张图片
     *
     * @param filePath
     *            pdf的路径
     * @param outpath
     *            输出的图片的路径[包括名称]
     *            pdf的页数
     *            【比如Pdf有3页，如果maxPage=2，则将pdf中的前2页转成图片，如果超过pdf实际页数，则按实际页数转换】
     */
    public static int pdf2multiImage(String filePath, String outpath,int offWidthLen, int offHeightLen)throws Exception {
        try {
            List<String> fileList = readfile(filePath);
            if(fileList != null && fileList.size() >0 ){
                List<BufferedImage> piclist = new ArrayList<BufferedImage>();
                for(String pdfFile: fileList){
                    InputStream is = new FileInputStream(pdfFile);
                    PDDocument pdf = PDDocument.load(is, true);
                    List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();
                   // int actSize = pages.size(); // pdf中实际的页数
                   // if (actSize < maxPage) maxPage = actSize;
                    for (int i = 0; i < pages.size(); i++) {
                        piclist.add(pages.get(i).convertToImage());
                    }
                    is.close();
                }
                if(piclist.size() <= 0){
                    return 2;
                }
                yPic(piclist, outpath,offWidthLen,offHeightLen);
            }else{
                return 2;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static List<String> readfile(String filepath) throws Exception {
        List<String> fileList = new ArrayList<String>();
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                String fileName = file.getName();
                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
                if("pdf".equalsIgnoreCase(fileType)) {
                    fileList.add(file.getAbsolutePath());
                }

            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        String fileName = readfile.getName();
                        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
                        if("pdf".equalsIgnoreCase(fileType)) {
                            fileList.add(readfile.getAbsolutePath());
                        }

                    } else if (readfile.isDirectory()) {
                        readfile(filepath + "\\" + filelist[i]);
                    }
                }

            }

        } catch (Exception e) {
        }
        return fileList;
    }

    /**
     * 将宽度相同的图片，竖向追加在一起 ##注意：宽度必须相同
     *
     * @param piclist
     *            文件流数组
     * @param outPath
     *            输出路径
     */
    public static void yPic(List<BufferedImage> piclist, String outPath,int offWidthLen, int offHeightLen) {// 纵向处理图片
        if (piclist == null || piclist.size() <= 0) {
           // System.out.println("图片数组为空!");
            return;
        }
        try {
            int fixHeight = 0, // 总高度
                    width = 0, // 总宽度
                    _height = 0, // 临时的高度 , 或保存偏移高度
                  //  __height = 0, // 临时的高度，主要保存每个高度
                    picNum = piclist.size();// 图片的数量
            File fileImg = null; // 保存读取出的图片
            int[] heightArray = new int[picNum]; // 保存每个文件的高度
            BufferedImage buffer = null; // 保存图片流
            List<int[]> imgRGB = new ArrayList<int[]>(); // 保存所有的图片的RGB
            int[] _imgRGB; // 保存一张图片中的RGB数据
            for (int i = 0; i < picNum; i++) {
                buffer = piclist.get(i);
                heightArray[i] = _height = buffer.getHeight();// 图片高度
                if (i == 0) {
                    width = buffer.getWidth();// 图片宽度
                    fixHeight = _height;
                }
                _imgRGB = new int[width * _height];// 从图片中读取RGB
                _imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
                imgRGB.add(_imgRGB);
            }
            // 生成新图片
            int number = 0;
            BufferedImage imageResult = new BufferedImage(width * 2 + offWidthLen * 3, fixHeight *3 + offHeightLen * 4, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = imageResult.getGraphics();
            graphics.setColor(Color.black);
            _height = offHeightLen; // 设置偏移高度为offHeightLen
            int picHeight = 0;
            int row = 1;
            for (int i = 0; i < picNum;i += 2) {
                picHeight = heightArray[i];
                if (row != 1) {
                    _height += picHeight;
                    _height += offHeightLen;
                }// 计算偏移高度
                imageResult.setRGB(offWidthLen, _height, width, picHeight, imgRGB.get(i), 0, width); // 写入流中
                graphics.drawLine(offWidthLen + width,_height+4,offWidthLen + width,picHeight + _height-2);
                if(i + 1 < picNum ){
                    imageResult.setRGB(offWidthLen*2 + width, _height, width, picHeight, imgRGB.get(i+1), 0, width); // 写入流中
                    graphics.drawLine(offWidthLen*2 + width*2,_height+4,offWidthLen*2 + width*2,picHeight + _height-2);
                }
                if(row == 3 || (i+1) == picNum-1 || i == picNum -1 ){
                    graphics.dispose();
                    String newOutPath = outPath + File.separator + "order{number}.jpg".replace("{number}",number+"");
                    number += 1;
                    File outFile = new File(newOutPath);
                    ImageIO.write(imageResult, "png", outFile);// 写图片
                    _height = offHeightLen;
                    row = 1;
                    if((i+1) != picNum-1 && i != picNum -1){
                        imageResult = new BufferedImage(width * 2 + offWidthLen * 3, fixHeight *3 + offHeightLen * 4, BufferedImage.TYPE_INT_ARGB);
                        graphics = imageResult.getGraphics();
                        graphics.setColor(Color.black);
                        continue;
                    }else{
                        break;
                    }
                }
                row ++;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

//    public static void main(String[] args) throws Exception {
//        pdf2multiImage("F:\\pdfTest", "F:\\");
//    }
}
