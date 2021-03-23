package com.mervin;

import com.mervin.eum.ResultEnum;
import com.mervin.util.FileUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * @author Mervin
 * @Description:
 * @date 2018-04-01 23:41
 */
public class PdfToImageUtil {

    // PDF 文件后缀
    public static final String PDF_FILE_SUFFIX = ".pdf";

    // 每个图片放多少个page
    public static final int PER_PAGE_SIZE = 6;

    /**
     * 将pdf中的所有页，转换成一张图片
     * @param filePath pdf的路径
     * @param outpath 输出的图片的路径[包括名称]
     * @param intervalWidthLen 每个图片之间的间隔宽度
     * @param intervalHeightLen 每个图片之间的间隔高度
     */
    public static int pdf2multiImage(String filePath, String outpath,int intervalWidthLen, int intervalHeightLen){
        List<String> fileList = new ArrayList<>();
        // 1. 读取所有的文件
        FileUtil.readfile(filePath, fileList, PDF_FILE_SUFFIX);
        if(fileList.size() <= 0){
            return ResultEnum.RESULT_FILE_NOT_EXISTS.getCode();
        }
        // 2. 将所有文件的PDF页转成图片类型
        List<BufferedImage> bufferedImageList = pageToImage(fileList);
        if(bufferedImageList.size() <= 0){
            return ResultEnum.RESULT_FILE_EMPTY.getCode();
        }
        // 3. 按宽度、高度进行排序
        sortImageList(bufferedImageList);

        // 4. 将所有图片类型转换成画布列表
        List<BufferedImage> outImageList = null;
        try {
            outImageList = bufferedToImages(bufferedImageList, intervalWidthLen, intervalHeightLen);
        }catch (Exception e){
            return ResultEnum.RESULT_MERGE_ERROR.getCode();
        }
        // 5. 将所有画布保存成图片
        try {
            writerToPngFile(outImageList, outpath);
        }catch (Exception e){
            return ResultEnum.RESULT_SAVE_ERROR.getCode();
        }
        // 部分错误信息的情况，返回部分成功，提示哪些地方报错
        if(!ErrorInfoCache.isEmpty()){
            return ErrorInfoCache.getFirstResultEnum().getCode();
        }
        return 0;
    }

    /**
     * 将PDF的每页转成图片类型
     * @param fileList
     * @return
     */
    public static List<BufferedImage> pageToImage(List<String> fileList){
        List<BufferedImage> piclist = new ArrayList<BufferedImage>();
        InputStream is = null;
        for(String pdfFile: fileList){
            try {
                is = new FileInputStream(pdfFile);
                PDDocument pdf = PDDocument.load(is, true);
                List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();
                if (pages != null && pages.size() > 0) {
                    for(int i=0; i< pages.size(); i++){
                        try {
                            piclist.add(pages.get(i).convertToImage());
                        }catch (IOException e){
                            ErrorInfoCache.addErrorInfo(ResultEnum.RESULT_PART_SUCCESS_PAGE_ERROR, "文件[" + pdfFile + "]第[" + (i+1) + "]页");
                        }
                    }
                }
            }catch (FileNotFoundException e){
                ErrorInfoCache.addErrorInfo(ResultEnum.RESULT_PART_SUCCESS_FILE_MOVE, pdfFile);
            } catch (IOException e) {
                ErrorInfoCache.addErrorInfo(ResultEnum.RESULT_PART_SUCCESS_IO_ERROR, pdfFile);
            } finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return piclist;
    }

    /**
     * 对BufferedImage 按宽度、高度进行排序
     * @param bufferedImageList
     */
    public static void sortImageList(List<BufferedImage> bufferedImageList){
        if(bufferedImageList != null && bufferedImageList.size() > 1){
            bufferedImageList.sort((image1, image2) -> {
                if(image1.getHeight() - image2.getHeight() == 0){
                    return image1.getWidth() - image2.getWidth();
                }
                return image1.getHeight() - image2.getHeight();
            });
        }
    }

    /**
     * 将每页的bufferImage按6个合成一张图片
     * @param pageBufferImageList
     * @param intervalWidthLen 间隔宽度
     * @param intervalHeightLen 间隔高度
     * @return
     */
    public static List<BufferedImage> bufferedToImages(List<BufferedImage> pageBufferImageList, int intervalWidthLen, int intervalHeightLen){
        int totalNums = pageBufferImageList.size();
        int totalPage = totalNums % PER_PAGE_SIZE == 0? totalNums/PER_PAGE_SIZE : totalNums/PER_PAGE_SIZE +1;
        List<BufferedImage> imageResultList = new ArrayList(totalPage);
        for(int pageIndex=0; pageIndex<totalPage; pageIndex++) {
            List<BufferedImage> subBufferedImageList = subDataList(pageBufferImageList, pageIndex);
            // 获取该页中最大的行和最大的高度值
            int maxWidth = getMaxValue(subBufferedImageList, BufferedImage::getWidth);
            int maxHeight = getMaxValue(subBufferedImageList, BufferedImage::getHeight);
            // 创建一块画布
            BufferedImage imageResult = new BufferedImage(maxWidth * 2 + intervalWidthLen * 3, maxHeight * 3 + intervalHeightLen * 4, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = imageResult.getGraphics();
            graphics.setColor(Color.black);
            int picNum = subBufferedImageList.size();
            int leftHeightOffset = intervalHeightLen;
            int rightHeightOffset = intervalHeightLen;
            for (int i = 0; i < picNum; i += 2) {
                BufferedImage leftBufferedImage = subBufferedImageList.get(i);
                int leftImageWidth = leftBufferedImage.getWidth();
                int leftImageHeight = leftBufferedImage.getHeight();
                int[] leftImgRGBArray = new int[leftImageWidth * leftImageHeight];// 从图片中读取RGB
                leftBufferedImage.getRGB(0, 0, leftImageWidth, leftImageHeight, leftImgRGBArray, 0, leftImageWidth);
                imageResult.setRGB(intervalWidthLen, leftHeightOffset, leftImageWidth, leftImageHeight, leftImgRGBArray, 0, leftImageWidth); // 写入流中
                graphics.drawLine(intervalWidthLen + leftImageWidth, leftHeightOffset + 4, intervalWidthLen + leftImageWidth, leftImageHeight + leftHeightOffset - 3);
                leftHeightOffset = leftHeightOffset + leftImageHeight + intervalHeightLen;
                if (i + 1 < picNum) {
                    BufferedImage rightBufferedImage = subBufferedImageList.get(i+1);
                    int rightImageWidth = rightBufferedImage.getWidth();
                    int rightImageHeight = rightBufferedImage.getHeight();
                    int[] rightImgRGBArray = new int[rightImageWidth * rightImageHeight];// 从图片中读取RGB
                    rightBufferedImage.getRGB(0, 0, rightImageWidth, rightImageHeight, rightImgRGBArray, 0, rightImageWidth);
                    imageResult.setRGB(intervalWidthLen * 2 + leftImageWidth, rightHeightOffset, rightImageWidth, rightImageHeight, rightImgRGBArray, 0, rightImageWidth); // 写入流中
                    graphics.drawLine(intervalWidthLen * 2 + leftImageWidth + rightImageWidth, rightHeightOffset + 4, intervalWidthLen * 2 + leftImageWidth + rightImageWidth, rightImageHeight + rightHeightOffset - 3);
                    rightHeightOffset = rightHeightOffset + rightImageHeight + intervalHeightLen;
                }
            }
            graphics.dispose();
            imageResultList.add(imageResult);
        }
        return imageResultList;
    }

    /**
     * 分页求出子数组
     * @param pageBufferImageList
     * @param pageIndex
     * @return
     */
    public static List<BufferedImage> subDataList(List<BufferedImage> pageBufferImageList, int pageIndex){
        int subIndex = pageIndex * PER_PAGE_SIZE;
        if(subIndex + PER_PAGE_SIZE > pageBufferImageList.size() -1){
            return  pageBufferImageList.subList(subIndex, pageBufferImageList.size());
        }
        return pageBufferImageList.subList(subIndex, subIndex + PER_PAGE_SIZE);
    }

    /**
     * 获取最大高度和最大宽度
     * @param pageBufferImageList
     * @return
     */
    public static int getMaxValue(List<BufferedImage> pageBufferImageList, ToIntFunction<? super BufferedImage> mapper){
        return pageBufferImageList.stream().mapToInt(mapper).max().getAsInt();
    }

    /**
     * 保存所有图片
     * @param imageResultList
     * @param outPath
     */
    public static void writerToPngFile(List<BufferedImage> imageResultList, String outPath){
        for(int i=1; i<= imageResultList.size(); i++){
            String newOutPath = outPath + File.separator + "{number}.png".replace("{number}",i+"");
            File outFile = new File(newOutPath);
            try {
                ImageIO.write(imageResultList.get(i-1), "png", outFile);// 写图片
            } catch (IOException e) {
                throw new RuntimeException("保存图片时出现异常！", e);
            }
        }
    }
}
