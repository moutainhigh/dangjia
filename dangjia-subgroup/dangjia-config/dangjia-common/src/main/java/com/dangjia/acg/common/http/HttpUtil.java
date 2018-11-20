package com.dangjia.acg.common.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

public class HttpUtil {
  /**
   * 向指定URL发送GET方法的请求
   *
   * @param url   发送请求的URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return String 所代表远程资源的响应结果
   */
  @SuppressWarnings("unused")
  public static String sendGet(String url, String param) {
    String result = "";
    BufferedReader in = null;
    try {
      String urlNameString = url + "?" + param;
      //System.out.println(urlNameString);
      URL realUrl = new URL(urlNameString);
      // 打开和URL之间的连接
      URLConnection connection = realUrl.openConnection();
      // 设置通用的请求属性
      connection.setRequestProperty("accept", "*/*");
      connection.setRequestProperty("connection", "Keep-Alive");
      connection.setRequestProperty("member-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      // 建立实际的连接
      connection.connect();
      // 获取所有响应头字段
      Map<String, List<String>> map = connection.getHeaderFields();
      // 遍历所有的响应头字段
            /*for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }*/
      // 定义 BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 使用finally块来关闭输入流
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    return result;
  }

  /**
   * 向指定 URL 发送POST方法的请求
   *
   * @param url   发送请求的 URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return 所代表远程资源的响应结果
   */
  public static String sendPost(String url, String param){
    OutputStreamWriter out = null;
    BufferedReader reader = null;
    String response = "";
    try {
      URL httpUrl = null; //HTTP URL类 用这个类来创建连接
      //创建URL
      httpUrl = new URL(url);
      //建立连接
      HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("connection", "keep-alive");
      conn.setUseCaches(false);//设置不要缓存
      conn.setInstanceFollowRedirects(true);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.connect();
      //POST请求
      out = new OutputStreamWriter(conn.getOutputStream());
      out.write(param);
      out.flush();
      //读取响应
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String lines;
      while ((lines = reader.readLine()) != null) {
        lines = new String(lines.getBytes(), "utf-8");
        response += lines;
      }
      reader.close();
      // 断开连接
      conn.disconnect();
    } catch (Exception e) {
      System.out.println("发送 POST 请求出现异常！" + e);
      e.printStackTrace();
    }
    //使用finally块来关闭输出流、输入流
    finally {
      try {
        if (out != null) {
          out.close();
        }
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    return response;
  }



  /**
   * 向指定 URL 发送POST方法的请求
   *
   * @param url   发送请求的 URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return 所代表远程资源的响应结果
   */
  public static String sendPostKey(String url, String param){
    OutputStreamWriter out = null;
    BufferedReader reader = null;
    String response = "";
    try {
      URL httpUrl = null; //HTTP URL类 用这个类来创建连接
      //创建URL
      httpUrl = new URL(url);
      //建立连接
      HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("connection", "keep-alive");
      conn.setUseCaches(false);//设置不要缓存
      conn.setInstanceFollowRedirects(true);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.connect();
      //POST请求
      out = new OutputStreamWriter(conn.getOutputStream());
      out.write(param);
      out.flush();
      //读取响应
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String lines;
      while ((lines = reader.readLine()) != null) {
        lines = new String(lines.getBytes(), "utf-8");
        response += lines;
      }
      reader.close();
      // 断开连接
      conn.disconnect();
    } catch (Exception e) {
      System.out.println("发送 POST 请求出现异常！" + e);
      e.printStackTrace();
    }
    //使用finally块来关闭输出流、输入流
    finally {
      try {
        if (out != null) {
          out.close();
        }
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    return response;
  }

  public static InputStream httpPostWithJSON(String url, String json) throws Exception {
    String result = null;
    // 将JSON进行UTF-8编码,以便传输中文
    String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);
    InputStream instreams = null;
    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(url);
    httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

    StringEntity se = new StringEntity(json);
    se.setContentType("text/json");
    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
    httpPost.setEntity(se);
    // httpClient.execute(httpPost);
    HttpResponse response = httpClient.execute(httpPost);
    if (response != null) {
      HttpEntity resEntity = response.getEntity();
      if (resEntity != null) {
        instreams = resEntity.getContent();
      }
    }
    return instreams;
  }



  /**
   * 微信js 签名
   * @param param
   * @return
   */
  public static String getJsApiSignature(Map<String, Object> param) {
    String strParam = getUrlParamsByMap(param);
    if (strParam == null) {
      return null;
    }
    return getSha1(strParam);
  }

  /**
   * get请求参数组装
   * @param param
   * @return
   */
  public static String getUrlParamsByMap(Map<String, Object> param) {
    SortedMap<String, String> sort = new TreeMap<String, String>((HashMap) param);
    if (param != null && !param.isEmpty()) {
      Set es = sort.entrySet();// 所有参与传参的参数按照accsii排序(升序)
      StringBuffer sb = new StringBuffer();
      Iterator it = es.iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        String k = (String) entry.getKey();
        Object v = entry.getValue();
        if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
          sb.append(k + "=" + v + "&");
        }
      }
      String strParam = sb.toString();
      if (strParam.endsWith("&")) {
        strParam = strParam.substring(0, strParam.lastIndexOf("&"));
      }
      System.out.println("http request:===>" + strParam);
      return strParam;
    }
    return null;
  }

  private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String getSha1(String str) {
    if (str == null || str.length() == 0) {
      return null;
    }
    try {
      MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
      mdTemp.update(str.getBytes("UTF-8"));
      byte[] md = mdTemp.digest();
      int j = md.length;
      char buf[] = new char[j * 2];
      int k = 0;
      for (int i = 0; i < j; i++) {
        byte byte0 = md[i];
        buf[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
        buf[k++] = HEX_DIGITS[byte0 & 0xf];
      }
      return new String(buf);
    } catch (Exception e) {
      return null;
    }
  }
  /**
   * @param url
   */
  public static String saveFileImg(String wechatUrl,String address) throws Exception {
    //new一个URL对象
    URL url = new URL(wechatUrl);
    //打开链接
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    //设置请求方式为"GET"
    conn.setRequestMethod("GET");
    //超时响应时间为5秒
    conn.setConnectTimeout(5 * 1000);
    //通过输入流获取图片数据
    InputStream inStream = conn.getInputStream();
    //得到图片的二进制数据，以二进制封装得到数据，具有通用性
    byte[] data = readInputStream(inStream);
    //String imgUrl ="https://store.shiguangkey.com/worldcup/images/head/";
    //new一个文件对象用来保存图片，默认保存当前工程根目录
    String currTime = System.currentTimeMillis()+".jpeg";
    File imageFile = new File(address+currTime);

    //创建输出流
    FileOutputStream outStream = new FileOutputStream(imageFile);
    //写入数据
    outStream.write(data);
    //关闭输出流
    outStream.close();
    return currTime;
  }
  public static byte[] readInputStream(InputStream inStream) throws Exception{
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    //创建一个Buffer字符串
    byte[] buffer = new byte[1024];
    //每次读取的字符串长度，如果为-1，代表全部读取完毕
    int len = 0;
    //使用一个输入流从buffer里把数据读取出来
    while( (len=inStream.read(buffer)) != -1 ){
      //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
      outStream.write(buffer, 0, len);
    }
    //关闭输入流
    inStream.close();
    //把outStream里的数据写入内存
    return outStream.toByteArray();
  }
}
