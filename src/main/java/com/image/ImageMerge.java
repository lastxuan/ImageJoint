package com.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 图片拼接
 * @author yaoweixiang
 * @since 1.0.0
 * <br>createDate 2021/12/7 15:08
 */
public class ImageMerge {
    /**
     * Java拼接多张图片
     *
     * @param pics    图片列表
     * @param type    1-横向；2-纵向
     * @param dst_pic 目标图片名
     * @return true-成功；false-失败
     */
    public static boolean merge(String[] pics, Integer type, String dst_pic) {

        int len = pics.length;
        if (len < 1) {
            System.out.println("pics len < 1");
            return false;
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File(pics[i]);
                images[i] = RotateImage.Rotate(src[i], 0);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];// 从图片中读取RGB
            ImageArrays[i] = images[i].getRGB(0, 0, width, height,
                    ImageArrays[i], 0, width);
        }

        int dst_height = 0;
        int dst_width = 0;
        //横向
        if (type == 1) {
            for (BufferedImage image : images) {
                dst_height = Math.max(dst_height, image.getHeight());

                dst_width += image.getWidth();
            }
        } else {
            for (BufferedImage image : images) {
                dst_width = Math.max(dst_width, image.getWidth());
                dst_height += image.getHeight();
            }
        }

        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(dst_width, dst_height,
                    BufferedImage.TYPE_INT_RGB);
            //横向拼接
            if (type == 1) {
                int width = 0;
                for (int i = 0; i < images.length; i++) {
                    ImageNew.setRGB(width, 0, images[i].getWidth(), images[i].getHeight(),
                            ImageArrays[i], 0, images[i].getWidth());
                    width += images[i].getWidth();
                }
            } else {
                //纵向拼接
                int height_i = 0;
                for (int i = 0; i < images.length; i++) {
                    ImageNew.setRGB(0, height_i, dst_width, images[i].getHeight(),
                            ImageArrays[i], 0, dst_width);
                    height_i += images[i].getHeight();
                }
            }
            File outFile = new File(dst_pic);
            ImageIO.write(ImageNew, dst_pic.substring(dst_pic.lastIndexOf(".") + 1), outFile);// 写图片
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *param qrcodePath : 最后图片保存路劲
     */
    public static void overlapImage(String qrcodePath) {
        try {
            BufferedImage big = new BufferedImage(1080, 1920, BufferedImage.TYPE_INT_RGB);
            //BufferedImage big = ImageIO.read(new File(screenPath));
            //BufferedImage small = ImageIO.read(new File(qrcodePath));
            BufferedImage small = new BufferedImage(540, 540,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = big.createGraphics();
            int x = (big.getWidth() - small.getWidth()) / 2;
            int y = 200 ;
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
            g.dispose();
            ImageIO.write(big, "jpg", new File(qrcodePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

//        String[] pics = new String[]{"292_1.jpeg", "292_2.jpeg", "292_3.jpeg"};
//        System.out.println(merge(pics, 1, "lolololo.jpeg"));
        overlapImage("1.jpeg");
    }
}
