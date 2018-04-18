package com.mervin;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
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
public class PdfToJpg {
    /**
     * 将pdf中的maxPage页，转换成一张图片
     *
     * @param pdfFile
     *            pdf的路径
     * @param outpath
     *            输出的图片的路径[包括名称]
     * @param maxPage
     *            pdf的页数
     *            【比如Pdf有3页，如果maxPage=2，则将pdf中的前2页转成图片，如果超过pdf实际页数，则按实际页数转换】
     */
    private static void pdf2multiImage(String pdfFile, String outpath, int maxPage) {
        try {
            InputStream is = new FileInputStream(pdfFile);
            PDDocument pdf = PDDocument.load(is, true);
            List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();
            List<BufferedImage> piclist = new ArrayList<BufferedImage>();
            int actSize = pages.size(); // pdf中实际的页数
            if (actSize < maxPage) maxPage = actSize;
            for (int i = 0; i < maxPage; i++) {
                piclist.add(pages.get(i).convertToImage());
            }
            yPic(piclist, outpath);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将宽度相同的图片，竖向追加在一起 ##注意：宽度必须相同
     *
     * @param piclist
     *            文件流数组
     * @param outPath
     *            输出路径
     */
    public static void yPic(List<BufferedImage> piclist, String outPath) {// 纵向处理图片
        if (piclist == null || piclist.size() <= 0) {
            System.out.println("图片数组为空!");
            return;
        }
        try {
            int height = 0, // 总高度
                    width = 0, // 总宽度
                    _height = 0, // 临时的高度 , 或保存偏移高度
                    __height = 0, // 临时的高度，主要保存每个高度
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
                }
                height += _height; // 获取总高度
                _imgRGB = new int[width * _height];// 从图片中读取RGB
                _imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
                imgRGB.add(_imgRGB);
            }
            _height = 0; // 设置偏移高度为0
            // 生成新图片
            BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < picNum; i++) {
                __height = heightArray[i];
                if (i != 0) _height += __height; // 计算偏移高度
                imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中
            }
            File outFile = new File(outPath);
            ImageIO.write(imageResult, "jpg", outFile);// 写图片
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        pdf2multiImage("C:\\Users\\Administrator\\Desktop\\printMb.pdf", "f:\\bbbbb.jpg", 4);
    }
}
