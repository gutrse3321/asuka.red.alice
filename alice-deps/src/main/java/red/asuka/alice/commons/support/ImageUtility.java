package red.asuka.alice.commons.support;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-05-14 16:13
 */
@UtilityClass
public class ImageUtility {

    private final Logger log = LoggerFactory.getLogger(ImageUtility.class);

    /**
     * 根据图片url获取Image实例
     * @param url
     * @return
     */
    public Image get(String url) {
        try {
            Image temp = new ImageIcon(new URL(url)).getImage();
            return temp;
        } catch (MalformedURLException e) {
            log.info("[ImageUtility] 没有获取到Image实例");
            e.printStackTrace();
        }
        return null;
    }

    /***
     * @author zhao
     * @date 2020/9/3 10:19
     * @param url 图片地址
     * @param num  分辨率缩放比例，取值范围大于1
     * @param quality  图片质量缩放比例，取值范围大于0小于1
     * @param softenFactor  图片软化比例，取值范围大于0，小于1，小于0.1时，图片大小增加
     * @return byte[]
     */
    public byte[] compression(String url,
                              double num,
                              float softenFactor,
                              float quality) {
        Image temp = get(url);
        return compression(temp, num, softenFactor, quality);
    }

    /***
     * @author zhao
     * @date 2020/9/3 10:19
     * @param img 图片地址
     * @param num  分辨率缩放比例，取值范围大于1
     * @param quality  图片质量缩放比例，取值范围大于0小于1
     * @param softenFactor  图片软化比例，取值范围大于0，小于1，小于0.1时，图片大小增加
     * @return byte[]
     */
    public byte[] compression(Image img,
                              double num,
                              float softenFactor,
                              float quality) {
        try {
            boolean numFlag = !"".equals(num) && num > 1;
            boolean softenFactorFlag = !"".equals(softenFactor)
                    && softenFactor > 0 && softenFactor < 1;
            boolean qualityFlag = !"".equals(quality)
                    && quality > 0 && quality < 1;
//            //判断是否是jpeg或者jpg图片，其他类型不支持质量压缩，至少我没找到方法
//            boolean suffixFlag = jpeg.equals(suffix) || jpg.equals(suffix);

            int w = img.getWidth(null);
            int h = img.getHeight(null);

            if (numFlag) {
                //分辨率压缩的后的大小
                h = new Double(h / num).intValue();
                w = new Double(w / num).intValue();
            }
            System.out.println("缩放后的大小 width：" + w + ", height：" + h);

            // 分辨率压缩
            BufferedImage bimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            // 将图像复制到缓冲的图像。
            Graphics g = bimage.createGraphics();
            g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
            g.dispose();

            //如果三个值都不符合取值范围，则不进行压缩
            //其实ImageIo在write时是对质量有影响的。
            if (!numFlag && !softenFactorFlag && !qualityFlag) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bimage, "jpg", out);
                return out.toByteArray();
            }

            // 软化
            if (softenFactorFlag) {
                float[] softenArray = {0, softenFactor, 0, softenFactor,
                        1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0};
                Kernel kernel = new Kernel(3, 3, softenArray);
                ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
                bimage = cOp.filter(bimage, null);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bimage, "jpg", out);
            return out.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转base64
     * @param bytes
     * @return
     */
    public String toBase64(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }
}
