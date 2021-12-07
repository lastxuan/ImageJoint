package com.image;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;



/**
 * 图片基础信息类
 *
 * @author yaoweixiang
 * @since 1.0.0
 * <br>createDate 2021/12/3 17:39
 */
@Data
public class ImageJointParam implements Serializable {

    /**
     * 图片距离整批布匹左侧距离,cm
     */
    private BigDecimal imageLeft;

    /**
     * 图片距离整批布匹顶部距离,cm
     */
    private BigDecimal imageTop;

    /**
     * 旋转角度，角度值：0-360，以图片的中心为原点旋转
     */
    private Integer imageRotationAngle = 0;

    /**
     * 旋转角度方向，0：逆时针，1：顺时针
     */
    private Integer rotationOritation = 1;

    /**
     * 图像块在经向标号，从0开始，整数范围的一个数
     */
    private Integer longtitudeIndex;

    /**
     * 图像块的纬向标号，从0开始，整数范围的一个数，一般不大于50
     */
    private Integer latitudeIndex;

    /**
     * 0=第一帧图像，1=中间帧图像；2=最后一帧图像
     */
    private Integer latitudeStatus;

    /**
     * 图片url
     */
    private String imageUrl;


    /**
     * 纬向重叠的像素值
     */
    private Integer latitudeOverlape = 150;

    /**
     * 经向重叠的像素值
     */
    private Integer longtitudeOverlape = 80;

    /**
     * 【公共】整数，单张图片的宽度，单位pixel
     */
    private Integer pixelWidth = 3072;

    /**
     * 【公共】整数，单张图片的高度，单位pixel
     */
    private Integer pixelHeight = 2048;
}
