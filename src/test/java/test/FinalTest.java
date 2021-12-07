package test;


import com.image.ImageJointParam;
import com.image.ImageMerge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试类
 *
 * @author yaoweixiang
 * @since 1.0.0
 * <br>createDate 2021/12/7 16:53
 */
public class FinalTest {

    public static void main(String[] args) {
        List<ImageJointParam> list = new ArrayList<>();
        ImageJointParam param1 = new ImageJointParam();
        param1.setLongtitudeIndex(1);
        param1.setLatitudeIndex(1);
        param1.setImageUrl("src\\main\\resources\\image\\292_1.jpeg");
        ImageJointParam param2 = new ImageJointParam();
        param2.setLongtitudeIndex(1);
        param2.setLatitudeIndex(2);
        param2.setImageUrl("src\\main\\resources\\image\\292_2.jpeg");
        ImageJointParam param3 = new ImageJointParam();
        param3.setLongtitudeIndex(1);
        param3.setLatitudeIndex(3);
        param3.setImageUrl("src\\main\\resources\\image\\292_3.jpeg");
        ImageJointParam param4 = new ImageJointParam();
        param4.setLongtitudeIndex(2);
        param4.setLatitudeIndex(1);
        param4.setImageUrl("src\\main\\resources\\image\\292_4.jpeg");
        ImageJointParam param5 = new ImageJointParam();
        param5.setLongtitudeIndex(2);
        param5.setLatitudeIndex(2);
        param5.setImageUrl("src\\main\\resources\\image\\292_5.jpeg");
        ImageJointParam param6 = new ImageJointParam();
        param6.setLongtitudeIndex(2);
        param6.setLatitudeIndex(3);
        param6.setImageUrl("src\\main\\resources\\image\\292_6.jpeg");
        ImageJointParam param7 = new ImageJointParam();
        param7.setLongtitudeIndex(3);
        param7.setLatitudeIndex(1);
        param7.setImageUrl("src\\main\\resources\\image\\292_7.jpeg");
        ImageJointParam param8 = new ImageJointParam();
        param8.setLongtitudeIndex(3);
        param8.setLatitudeIndex(2);
        param8.setImageUrl("src\\main\\resources\\image\\292_8.jpeg");
        ImageJointParam param9 = new ImageJointParam();
        param9.setLongtitudeIndex(3);
        param9.setLatitudeIndex(3);
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
        //经向
        Map<Integer, List<ImageJointParam>> longitudeMap = list.stream().sorted(Comparator.comparing(ImageJointParam::getLatitudeIndex)).collect(Collectors.groupingBy(ImageJointParam::getLongtitudeIndex));
        List<String> fileList = new ArrayList<>();
        longitudeMap.forEach((k,v)->{
            String temp = "src\\main\\resources\\image\\aaaa" + k + ".jpeg";
            String[] pic = v.stream().map(ImageJointParam::getImageUrl).collect(Collectors.toList()).toArray(new String[v.size()]);
            ImageMerge.merge(pic, 1, temp);
            fileList.add(temp);
        });
        //纬度
        ImageMerge.merge((String[]) fileList.toArray(new String[fileList.size()]), 2, "src\\main\\resources\\image\\final.jpeg");
    }
}
