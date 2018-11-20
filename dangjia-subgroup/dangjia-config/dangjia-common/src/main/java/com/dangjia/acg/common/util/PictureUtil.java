package com.dangjia.acg.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qiyuxiang
 * @date 2018-07- 27 15:34:21
 **/
public class PictureUtil {

  /**
   * 导入本地图片到缓冲区
   */
  public static BufferedImage loadImageLocal(String imgName) {
    try {
      return ImageIO.read(new File(imgName));
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  /**
   * 导入网络图片到缓冲区
   */
  public static BufferedImage loadImageUrl(String imgName) {
    try {
      URL url = new URL(imgName);
      return ImageIO.read(url);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  /**
   * 生成新图片到本地
   *
   * @param newImage
   * @param img
   */
  public static void writePicture(String newImage, BufferedImage img) {
    if (newImage != null && img != null) {
      try {
        File outputfile = new File(newImage);
        ImageIO.write(img, "png", outputfile);
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * 设定文字的字体等
   */
  //  public void setFont(String fontStyle, int fontSize) {
  //    this.fontSize = fontSize;
  //    this.font = new Font(fontStyle, Font.PLAIN, fontSize);
  //  }

  /**
   * 修改图片,返回修改后的图片缓冲区（只输出一行文本）
   */
  public static BufferedImage generatedText(Font font, String colorStr, BufferedImage img, Object content, int x, int y, boolean isCenter, boolean isMiddle, Integer width) {

    try {
      int left = 0;
      int top = 0;

      int w = img.getWidth();
      int h = img.getHeight();
      Graphics2D g = img.createGraphics();
      g.setBackground(Color.WHITE);
      g.setColor(new Color(Integer.parseInt(colorStr, 16)));//设置字体颜色(16进制)
      if (font != null)
        g.setFont(font);
      // 抗锯齿
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      // 验证输出位置的纵坐标和横坐标
      if (x >= w || y >= h) {
        left = h - font.getSize() + 2;
        top = w;
      } else {
        left = x;
        top = y;
      }
      if (content != null) {
        // 是否水平居中
        if (!CommonUtil.isEmpty(isCenter) && isCenter) {
          // 计算文字长度，计算居中的x点坐标
          FontMetrics fm = g.getFontMetrics(font);
          int textWidth = fm.stringWidth(content.toString());
          left = (w - textWidth) / 2;
        }
        // 是否垂直居中
        if (!CommonUtil.isEmpty(isMiddle) && isMiddle) {
          // 计算文字高度，计算居中的y点坐标
          FontMetrics fm = g.getFontMetrics(font);
          int textHeight = fm.getHeight();
          top = (h - textHeight) / 2;
        }

        String text = content.toString();
        if (CommonUtil.isEmpty(width)) {
          g.drawString(text, left, top);
        } else {
          // 换行
          changeLine(g, font, text, left, top, width);
        }
      }
      g.dispose();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return img;
  }

  /**换行
   *
   * @param g
   * @param font
   * @param text
   * @param top
   * @param left
   * @param width
   */
  private static void changeLine(Graphics2D g, Font font, String text, int left, int top, int width) {
    char[] chars = text.toCharArray();

    FontMetrics fm = g.getFontMetrics(font);
    int charWidth = fm.charWidth(chars[0]);

    List<String> texts = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    //超过两行用...代替
    int index = 1;
    for (char aChar : chars) {
      int lineWidth = fm.stringWidth(sb.toString());
      if (lineWidth + charWidth <= width) {
        sb.append(aChar);
      } else {
        if (index < 2) {

          texts.add(sb.toString());
          sb = new StringBuilder();
          sb.append(aChar);
        } else {

          String str = sb.substring(0, sb.length() - 3) + "...";
          texts.add(str);
          sb = new StringBuilder();
          break;
        }
        index++;
      }
    }
    String s = sb.toString();
    if (!CommonUtil.isEmpty(s)){
      texts.add(s);
    }

    int textHeight = fm.getHeight();
    for (int i = 0, size = texts.size(); i < size; i++) {
      int lineTop = top + i * textHeight;
      String txt = texts.get(i);
      g.drawString(txt, left, lineTop);
    }
  }

  /**
   * 修改图片,返回修改后的图片缓冲区（输出多个文本段） xory：true表示将内容在一行中输出；false表示将内容多行输出
   */
  public static BufferedImage modifyImage(Font font, BufferedImage img, Object[] contentArr, int x, int y, boolean xory) {
    try {
      int left = 0;
      int top = 0;

      int w = img.getWidth();
      int h = img.getHeight();
      int fontSize = 0;
      Graphics2D g = img.createGraphics();
      g.setBackground(Color.WHITE);
      g.setColor(Color.RED);
      if (font != null) {
        g.setFont(font);
        fontSize = font.getSize();
      }

      // 验证输出位置的纵坐标和横坐标
      if (x >= h || y >= w) {
        left = h - fontSize + 2;
        top = w;
      } else {
        left = x;
        top = y;
      }
      if (contentArr != null) {
        int arrlen = contentArr.length;
        if (xory) {
          for (int i = 0; i < arrlen; i++) {
            g.drawString(contentArr[i].toString(), left, top);
            left += contentArr[i].toString().length() * fontSize / 2 + 5;// 重新计算文本输出位置
          }
        } else {
          for (int i = 0; i < arrlen; i++) {
            g.drawString(contentArr[i].toString(), left, top);
            top += fontSize + 2;// 重新计算文本输出位置
          }
        }
      }
      g.dispose();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return img;
  }

  /**
   * 修改图片,返回修改后的图片缓冲区（只输出一行文本）
   * 时间:2007-10-8
   *
   * @param img
   * @return
   */
  public static BufferedImage modifyImageYe(Font font, BufferedImage img) {

    try {
      int w = img.getWidth();
      int h = img.getHeight();
      Graphics2D g = img.createGraphics();
      g.setBackground(Color.WHITE);
      g.setColor(Color.blue);//设置字体颜色
      if (font != null) {
        g.setFont(font);
      }
      g.drawString("www.hi.baidu.com?xia_mingjian", w - 85, h - 5);
      g.dispose();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return img;
  }


  public static BufferedImage generatedPicture(BufferedImage srcImage, BufferedImage bufferedImage, int left, int top, Float alpha) {

    try {
      int w = bufferedImage.getWidth();
      int h = bufferedImage.getHeight();

      Graphics2D g = srcImage.createGraphics();
      // 在图形和图像中实现混合和透明效果
      if (!CommonUtil.isEmpty(alpha)) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
      }
      // 抗锯齿
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g.drawImage(bufferedImage, left, top, w, h, null);
      //g.drawImage(b, 0, a.getHeight() - c.getHeight(), c.getWidth(), c.getHeight(), null);
      g.dispose();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return srcImage;
  }

  /**
   * 图片设置圆角
   *
   * @param srcImage
   * @param radius
   * @param border
   * @param padding
   * @return
   * @throws IOException
   */
  public static BufferedImage setRadius(BufferedImage srcImage, int radius, int border, int padding) throws IOException {
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    int canvasWidth = width + padding * 2;
    int canvasHeight = height + padding * 2;

    BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gs = image.createGraphics();
    gs.setComposite(AlphaComposite.Src);
    gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    gs.setColor(Color.WHITE);
    gs.fill(new RoundRectangle2D.Float(0, 0, canvasWidth, canvasHeight, radius, radius));
    gs.setComposite(AlphaComposite.SrcAtop);
    gs.drawImage(setClip(srcImage, radius), padding, padding, null);
    if (border != 0) {
      gs.setColor(Color.white);
      gs.setStroke(new BasicStroke(border));
      gs.drawRoundRect(padding, padding, canvasWidth - 2 * padding, canvasHeight - 2 * padding, radius, radius);
    }
    gs.dispose();
    return image;
  }

  /**
   * 图片设置圆角
   *
   * @param srcImage
   * @return
   * @throws IOException
   */
  public static BufferedImage setRadius(BufferedImage srcImage) throws IOException {
    int radius = (srcImage.getWidth() + srcImage.getHeight()) / 2;
    return setRadius(srcImage, radius, 1, 1);
  }

  /**
   * 图片切圆角
   *
   * @param srcImage
   * @param radius
   * @return
   */
  public static BufferedImage setClip(BufferedImage srcImage, int radius) {
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gs = image.createGraphics();

    gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    gs.setClip(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));
    gs.drawImage(srcImage, 0, 0, null);
    gs.dispose();
    return image;
  }

  //public static void member(String[] args) {
  //  Font font = new Font("微软雅黑", Font.PLAIN, 30);
  //
  //  // 558 * 886
  //  BufferedImage a = PictureUtil.loadImageLocal("f:/pic/11.png");
  //  // 99 * 99
  //  BufferedImage b = PictureUtil.loadImageLocal("f:/pic/t1.png");
  //  BufferedImage c = PictureUtil.loadImageLocal("f:/pic/t2.png");
  //
  //  //将多张图片合在一起
  //  BufferedImage img = PictureUtil.generatedPicture(a, b, 0, 0, 1F);
  //  //往图片上写文字
  //  PictureUtil.generatedText(font, "ff0000", img, "felling邀请你来打卡", 50, 50);
  //
  //  PictureUtil.writePicture("F:\\pic\\cc.png", img);
  //
  //  System.out.println("success");
  //}

}
