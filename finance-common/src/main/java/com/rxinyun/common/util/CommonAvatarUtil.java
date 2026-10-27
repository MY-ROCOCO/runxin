package com.rxinyun.common.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.RandomUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class CommonAvatarUtil {
   public static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");
   private static final int[] BEAUTIFUL_COLORS = new int[]{7497190, 16563968, 41646, 16083456, 1609983, 6319488};

   private CommonAvatarUtil() {
   }

   public static String generateImg(final String name) {
      int width = 100;
      int height = 100;
      int nameLength = name.length();
      String nameWritten = name;
      if (nameLength > 2) {
         if (isChinese(StringUtils.substring(name, 0, 1))) {
            nameWritten = name.substring(nameLength - 2);
         } else {
            nameWritten = StringUtils.substring(name, 0, 1).toUpperCase();
         }
      }

      BufferedImage bufferedImage = new BufferedImage(100, 100, 1);
      Graphics2D g2 = (Graphics2D)bufferedImage.getGraphics();
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setBackground(getRandomColor());
      g2.clearRect(0, 0, 100, 100);
      g2.setPaint(Color.WHITE);
      if (nameWritten.length() >= 2) {
         g2.setFont(new Font("微软雅黑", 1, 30));
         String firstWritten = StringUtils.substring(nameWritten, 0, 1);
         String secondWritten = StringUtils.substring(nameWritten, 0, 2);
         if (isChinese(firstWritten) && isChinese(secondWritten)) {
            g2.drawString(nameWritten, 20, 60);
         } else if (isChinese(firstWritten) && !isChinese(secondWritten)) {
            g2.drawString(nameWritten, 27, 60);
         } else {
            nameWritten = nameWritten.substring(0, 1);
         }
      }

      if (nameWritten.length() == 1) {
         if (isChinese(nameWritten)) {
            g2.setFont(new Font("微软雅黑", 0, 50));
            g2.drawString(nameWritten, 25, 70);
         } else {
            g2.setFont(new Font("微软雅黑", 0, 55));
            g2.drawString(nameWritten.toUpperCase(), 33, 67);
         }
      }

      return ImgUtil.toBase64DataUri(bufferedImage, "jpg");
   }

   private static Color getRandomColor() {
      return new Color(BEAUTIFUL_COLORS[RandomUtil.randomInt(BEAUTIFUL_COLORS.length)]);
   }

   private static boolean isChinese(String str) {
      return CHINESE_PATTERN.matcher(str).find();
   }
}
