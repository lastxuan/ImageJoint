package com.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 该类实现了图片的合并功能，可以选择水平合并或者垂直合并。
 * 当然此例只是针对两个图片的合并，如果想要实现多个图片的合并，只需要自己实现方法 BufferedImage
 * mergeImage(BufferedImage[] imgs, boolean isHorizontal)即可；
 * 而且这个方法更加具有通用性，但是时间原因不实现了，方法和两张图片实现是一样的
 * @author yaoweixiang
 * @since 1.0.0
 * <br>createDate 2021/12/3 18:39
 */

public class ImageJointUtil {

    /**
     * @param fileUrl 文件绝对路径或相对路径
     * @return 读取到的缓存图像
     * @throws IOException 路径错误或者不存在该文件时抛出IO异常
     */
    public static BufferedImage getBufferedImage(String fileUrl)
            throws IOException {
        File f = new File(fileUrl);
        return ImageIO.read(f);
    }


    /**
     * 远程图片转BufferedImage
     *
     * @param destUrl 远程图片地址
     * @return
     */
    public static BufferedImage getBufferedImageDestUrl(String destUrl) {
        HttpURLConnection conn = null;
        BufferedImage image = null;
        try {
            URL url = new URL(destUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                image = ImageIO.read(conn.getInputStream());
                return image;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * 输出图片
     *
     * @param buffImg  图像拼接叠加之后的BufferedImage对象
     * @param savePath 图像拼接叠加之后的保存路径
     */
    public static void generateSaveFile(BufferedImage buffImg, String savePath) {
        int temp = savePath.lastIndexOf(".") + 1;
        try {
            File outFile = new File(savePath);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            ImageIO.write(buffImg, savePath.substring(temp), outFile);
            System.out.println("ImageIO write...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param buffImg  源文件(BufferedImage)
     * @param waterImg 水印文件(BufferedImage)
     * @param x        距离右下角的X偏移量
     * @param y        距离右下角的Y偏移量
     * @param alpha    透明度, 选择值从0.0~1.0: 完全透明~完全不透明
     * @return BufferedImage
     * @throws IOException
     * @Title: 构造图片
     * @Description: 生成水印并返回java.awt.image.BufferedImage
     */
    public static BufferedImage overlyingImage(BufferedImage buffImg, BufferedImage waterImg, int x, int y, float alpha) throws IOException {

        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 绘制
        g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
        g2d.dispose();// 释放图形上下文使用的系统资源
        return buffImg;
    }

    public static BufferedImage overlyingImage(List<ImageJointParam> list) throws IOException {

        // 创建Graphics2D对象，用在底图对象上绘图
        int height = list.stream().max(Comparator.comparingInt(ImageJointParam::getLatitudeIndex)).get().getLatitudeIndex() + 1;
        int width = list.stream().max(Comparator.comparingInt(ImageJointParam::getLongtitudeIndex)).get().getLongtitudeIndex() + 1;
        BufferedImage buffImg = new BufferedImage(width * (list.get(0).getPixelWidth() + list.get(0).getLatitudeOverlape()), height * (list.get(0).getPixelHeight() + list.get(0).getLongtitudeOverlape()),
                BufferedImage.TYPE_INT_RGB);
        //
        Graphics2D g2d = buffImg.createGraphics();
        for (int i = 0; i < list.size(); i++) {
            ImageJointParam imageJointParam = list.get(i);
            BufferedImage waterImage = getBufferedImage(imageJointParam.getImageUrl());
            //图片旋转
            Integer rotationOritation = imageJointParam.getRotationOritation();
            Integer imageRotationAngle = imageJointParam.getImageRotationAngle();
            if(Objects.nonNull(rotationOritation) && Objects.nonNull(imageRotationAngle) && imageJointParam.getImageRotationAngle() != 0){
                if(rotationOritation == 0){
                    imageRotationAngle = 360 - imageRotationAngle;
                }
                waterImage = rotateImage(waterImage, imageRotationAngle);
            }
            int waterImgWidth = waterImage.getWidth();// 获取层图的宽度
            int waterImgHeight = waterImage.getHeight();// 获取层图的高度
            // 在图形和图像中实现混合和透明效果
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
            //获取绘制的x,y坐标
            int x = imageJointParam.getLatitudeIndex() * imageJointParam.getPixelWidth() - imageJointParam.getLatitudeOverlape() * imageJointParam.getLatitudeIndex();
            int y = imageJointParam.getLongtitudeIndex() * imageJointParam.getPixelHeight() + imageJointParam.getLongtitudeOverlape() * imageJointParam.getLatitudeIndex();
            // 绘制
            g2d.drawImage(waterImage, x, y, waterImgWidth, waterImgHeight, null);
        }
        g2d.dispose();// 释放图形上下文使用的系统资源
        return buffImg;
    }

    /**
     * 待合并的两张图必须满足这样的前提，如果水平方向合并，则高度必须相等；如果是垂直方向合并，宽度必须相等。
     * mergeImage方法不做判断，自己判断。
     *
     * @param img1         待合并的第一张图
     * @param img2         带合并的第二张图
     * @param isHorizontal 为true时表示水平方向合并，为false时表示垂直方向合并
     * @return 返回合并后的BufferedImage对象
     * @throws IOException
     */
    public static BufferedImage mergeImage(BufferedImage img1,
                                           BufferedImage img2, boolean isHorizontal) throws IOException {
        int w1 = img1.getWidth();
        int h1 = img1.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        // 从图片中读取RGB
        int[] ImageArrayOne = new int[w1 * h1];
        ImageArrayOne = img1.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
        int[] ImageArrayTwo = new int[w2 * h2];
        ImageArrayTwo = img2.getRGB(0, 0, w2, h2, ImageArrayTwo, 0, w2);

        // 生成新图片
        BufferedImage DestImage = null;
        if (isHorizontal) { // 水平方向合并
            DestImage = new BufferedImage(w1 + w2, h1, BufferedImage.TYPE_INT_RGB);
            DestImage.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            DestImage.setRGB(w1, 0, w2, h2, ImageArrayTwo, 0, w2);
        } else { // 垂直方向合并
            DestImage = new BufferedImage(w1, h1 + h2, BufferedImage.TYPE_INT_RGB);
            DestImage.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            DestImage.setRGB(0, h1, w2, h2, ImageArrayTwo, 0, w2); // 设置下半部分的RGB
        }

        return DestImage;
    }


    /**
     * 旋转图片为指定角度
     *
     * @param bufferedimage
     *            目标图像
     * @param degree
     *            旋转角度
     * @return
     */
    public static BufferedImage rotateImage(BufferedImage bufferedimage, int degree) {
        if(degree == 0){
            return bufferedimage;
        }
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * Java 测试图片叠加方法
     */
    public static void overlyingImageTest() {

        List<ImageJointParam> list = new ArrayList<>();
        ImageJointParam param1 = new ImageJointParam();
        param1.setLatitudeIndex(0);
        param1.setLongtitudeIndex(0);
        param1.setImageUrl("src\\main\\resources\\image\\292_1.jpeg");
        ImageJointParam param2 = new ImageJointParam();
        param2.setLatitudeIndex(1);
        param2.setLongtitudeIndex(0);

//        param2.setImageRotationAngle(-3);
        param2.setImageUrl("src\\main\\resources\\image\\292_2.jpeg");
        ImageJointParam param3 = new ImageJointParam();
        param3.setLongtitudeIndex(0);
        param3.setLatitudeIndex(2);
//        param3.setImageRotationAngle(-3);
        param3.setImageUrl("src\\main\\resources\\image\\292_3.jpeg");
        ImageJointParam param4 = new ImageJointParam();
        param4.setLongtitudeIndex(1);
        param4.setLatitudeIndex(0);
        param4.setImageUrl("src\\main\\resources\\image\\292_4.jpeg");
        ImageJointParam param5 = new ImageJointParam();
        param5.setLongtitudeIndex(1);
        param5.setLatitudeIndex(1);
//        param5.setImageRotationAngle(-3);
        param5.setImageUrl("src\\main\\resources\\image\\292_5.jpeg");
        ImageJointParam param6 = new ImageJointParam();
        param6.setLongtitudeIndex(1);
        param6.setLatitudeIndex(2);
//        param6.setImageRotationAngle(-3);
        param6.setImageUrl("src\\main\\resources\\image\\292_6.jpeg");
        ImageJointParam param7 = new ImageJointParam();
        param7.setLongtitudeIndex(2);
        param7.setLatitudeIndex(0);
        param7.setImageUrl("src\\main\\resources\\image\\292_7.jpeg");
        ImageJointParam param8 = new ImageJointParam();
        param8.setLongtitudeIndex(2);
        param8.setLatitudeIndex(1);
//        param8.setImageRotationAngle(-3);
        param8.setImageUrl("src\\main\\resources\\image\\292_8.jpeg");
        ImageJointParam param9 = new ImageJointParam();
        param9.setLongtitudeIndex(2);
        param9.setLatitudeIndex(2);
//        param9.setImageRotationAngle(-3);
        param9.setImageUrl("src\\main\\resources\\image\\292_9.jpeg");
        list.add(param1);
        list.add(param2);
        list.add(param3);
        list.add(param4);
        list.add(param5);
        list.add(param6);
        list.add(param7);
        list.add(param8);
        list.add(param9);
        String saveFilePath = "src\\main\\resources\\image\\overlyingImageTest.jpeg";
        try {
            // 构建叠加层
            BufferedImage buffImg = overlyingImage(list);
            // 输出水印图片
            generateSaveFile(buffImg, saveFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // 测试图片的叠加
        overlyingImageTest();
    }

}
