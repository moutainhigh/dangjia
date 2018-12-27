package com.dangjia.acg;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@EnableFeignClients
public class AccessFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(AccessFilter.class);




    @Override
    public String filterType() {
        //前置过滤器
        return "post";
    }

    @Override
    public int filterOrder() {
        //优先级，数字越大，优先级越低
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        return true;
    }

    private static String getRequestInfo(Map<String, String> param) {
        StringBuffer sb = new StringBuffer();
        Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
        sb.append("{");
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();

            if (i > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Object run() {
        try {
            RequestContext context = RequestContext.getCurrentContext();
            HttpServletRequest request = context.getRequest();
            HttpServletResponse response = context.getResponse();
            String transCode = request.getRequestURI();
            //路径包含export关键字时直接跳过
            if (transCode.indexOf("export")>0) {
                return null;
            }
            response.setHeader("Access-Control-Allow-Origin","*");
            response.setHeader("Access-Control-Allow-Origin","*");
            // 响应类型
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS, DELETE");
            // 响应头设置
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header, HaiYi-Access-Token");
            if ("OPTIONS".equals(request.getMethod())){
                response.setStatus(HttpStatus.SC_NO_CONTENT);
            }
            //获取入参
//      String requestInfo = null;
//      if (request.getContentLength() > 0) {
//        Map<String, String> param = this.parseParam(request);
//        requestInfo = this.getRequestInfo(param);
//        this.getRequestInfo(param);
//      }
            // 获取返回
            InputStream stream = context.getResponseDataStream();
            String responseInfo =copyToString(stream);
            // 不加前端接收不到返回
            context.setResponseBody(responseInfo);

//      String unionId = request.getHeader("unionId");

            // 保存用户日志
//      UserLogDTO userLogDTO = new UserLogDTO();
//      userLogDTO.setUnionId(unionId);
//      userLogDTO.setTransCode(transCode);
//      userLogDTO.setRequestParam(requestInfo);
//      userLogDTO.setResponseParam(responseInfo);
//      userLogServiceAPI.insertUserLog(userLogDTO);

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(" access token ok");
        //这里return的值没有意义，zuul框架没有使用该返回值
        return null;
    }


    public String splitString(String str, String temp) {
        String result = null;
        if (str.indexOf(temp) != -1) {
            if (str.substring(str.indexOf(temp)).indexOf("&") != -1) {
                result = str.substring(str.indexOf(temp)).substring(str.substring(str.indexOf(temp)).indexOf("=") + 1, str.substring(str.indexOf(temp)).indexOf("&"));
            } else {
                result = str.substring(str.indexOf(temp)).substring(str.substring(str.indexOf(temp)).indexOf("=") + 1);
            }
        }
        return result;

    }
    public static String copyToString(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in);
        char[] buffer = new char[4096];
        int bytesRead = -1;
        while ((bytesRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, bytesRead);
        }
        return out.toString();
    }
    private Map<String, String> parseParam(HttpServletRequest request) throws IOException {
        final int NONE = 0; // 状态码，表示没有特殊操作
        final int DATAHEADER = 1; // 表示下一行要读到报头信息
        final int FILEDATA = 2; // 表示下面要读的是上传文件和二进制数据
        final int FIELD_LENGTH = 3;// 表单域值得长度
        final int FIELDDATA = 4; // 表示下面要读到表单域的文本值
        // 请求消息实体的总长度(请求消息中除消息头之外的数据长度)
        int totalbytes = request.getContentLength();
        File f; // 上传文件储存在服务器上
        // 容纳请求消息实体的字节数组
        byte[] dataOrigin = new byte[totalbytes];
        // 对于post多个文件的表单，b作为原始数据的副本提供提取文件数据的操作
        byte[] b = new byte[totalbytes];
        // 请求消息类型
        String contentType = request.getContentType();
        String fieldname = ""; // 表单域的名称
        String fieldvalue = ""; // 表单域的值
        String fileFormName = ""; // 上传的文件再表单中的名称
        String fileRealName = ""; // 上传文件的真实名字
        String boundary = ""; // 分界符字符串
        String lastboundary = ""; // 结束分界符字符串
        // int fileSize = 0; // 文件长度
        // 容纳表单域的名称/值的哈希表
        Map<String, String> formfieldsTable = new HashMap<String, String>();
        // 容纳文件域的名称/文件名的哈希表
        Map<String, String> filenameTable = new HashMap<>();
        // 在消息头类型中找到分界符的定义
        int pos = contentType.indexOf("boundary=");
        int pos2; // position2
        if (pos != -1) {
            pos += "boundary=".length();
            boundary = "--" + contentType.substring(pos); // 解析出分界符
            lastboundary = boundary + "--"; // 得到结束分界符
        }
        int state = NONE; // 起始状态为NONE
        // 得到请求消息的数据输入流
        DataInputStream in = new DataInputStream(request.getInputStream());
        in.readFully(dataOrigin); // 根据长度，将消息实体的内容读入字节数组dataOrigin中
        in.close(); // 关闭数据流
        String reqcontent = new String(dataOrigin); // 从字节数组中得到表示实体的字符串
        // 从字符串中得到输出缓冲流
        BufferedReader reqbuf = new BufferedReader(new StringReader(reqcontent));
        // 设置循环标志
        boolean flag = true;
        // int i = 0;
        while (flag == true) {
            String s = reqbuf.readLine();
            if (s == lastboundary || s == null)
                break;
            switch (state) {
                case NONE:
                    if (s.startsWith(boundary)) {
                        // 如果读到分界符，则表示下一行一个头信息
                        state = DATAHEADER;
                        // i += 1;
                    }
                    break;
                case DATAHEADER:
                    pos = s.indexOf("filename=");
                    // 先判断出这是一个文本表单域的头信息，还是一个上传文件的头信息
                    if (pos == -1) {
                        // 如果是文本表单域的头信息，解析出表单域的名称
                        pos = s.indexOf("name=");
                        pos += "name=".length() + 1; // 1表示后面的"的占位
                        s = s.substring(pos);
                        int l = s.length();
                        s = s.substring(0, l - 1); // 应该是"
                        fieldname = s; // 表单域的名称放入fieldname
                        state = FIELD_LENGTH; // 设置状态码，准备读取表单域的值
                    } else {
                        // 如果是文件数据的头，先存储这一行，用于在字节数组中定位
                        String temp = s;
                        // 先解析出文件名
                        pos = s.indexOf("name=");
                        pos += "name=".length() + 1; // 1表示后面的"的占位
                        pos2 = s.indexOf("filename=");
                        String s1 = s.substring(pos, pos2 - 3); // 3表示";加上一个空格
                        fileFormName = s1;
                        pos2 += "filename=".length() + 1; // 1表示后面的"的占位
                        s = s.substring(pos2);
                        int l = s.length();
                        s = s.substring(0, l - 1);
                        pos2 = s.lastIndexOf("\\"); // 对于IE浏览器的设置
                        s = s.substring(pos2 + 1);
                        fileRealName = s;
                        if (fileRealName.length() != 0) { // 确定有文件被上传
                            // 下面这一部分从字节数组中取出文件的数据
                            b = dataOrigin; // 复制原始数据以便提取文件
                            pos = byteIndexOf(b, temp, 0); // 定位行
                            // 定位下一行，2 表示一个回车和一个换行占两个字节
                            b = subBytes(b, pos + temp.getBytes().length + 2, b.length);
                            // 再读一行信息，是这一部分数据的Content-type
                            s = reqbuf.readLine();
                            // 设置文件输入流，准备写文件
                            f = new File("d:" + File.separator + fileRealName);
                            DataOutputStream fileout = new DataOutputStream(new FileOutputStream(f));
                            // 字节数组再往下一行，4表示两回车换行占4个字节，本行的回车换行2个字节，Content-type的下
                            // 一行是回车换行表示的空行，占2个字节
                            // 得到文件数据的起始位置
                            b = subBytes(b, s.getBytes().length + 4, b.length);
                            pos = byteIndexOf(b, boundary, 0); // 定位文件数据的结尾
                            b = subBytes(b, 0, pos - 1); // 取得文件数据
                            fileout.write(b, 0, b.length - 1); // 将文件数据存盘
                            fileout.close();
                            // fileSize = b.length - 1; // 文件长度存入fileSize
                            filenameTable.put(fileFormName, fileRealName);
                            state = FIELD_LENGTH;
                        }
                    }
                    break;
                case FIELD_LENGTH:
                    s = reqbuf.readLine();
                    state = FIELDDATA;
                    break;
                case FIELDDATA:
                    // 读取表单域的值
                    s = reqbuf.readLine();
                    fieldvalue = s; // 存入fieldvalue
                    formfieldsTable.put(fieldname, fieldvalue);
                    state = NONE;
                    break;
                case FILEDATA:
                    // 如果是文件数据不进行分析，直接读过去
                    while ((!s.startsWith(boundary)) && (!s.startsWith(lastboundary))) {
                        s = reqbuf.readLine();
                        if (s.startsWith(boundary)) {
                            state = DATAHEADER;
                        } else {
                            break;
                        }
                    }
                    break;
            }
        }
        return formfieldsTable;
    }

    private static int byteIndexOf(byte[] b, String s, int start) {
        return byteIndexOf(b, s.getBytes(), start);
    }

    private static int byteIndexOf(byte[] b, byte[] s, int start) {
        int i;
        if (s.length == 0) {
            return 0;
        }
        int max = b.length - s.length;
        if (max < 0) {
            return -1;
        }
        if (start > max) {
            return -1;
        }
        if (start < 0) {
            start = 0;
        }
        // 在b中找到s的第一个元素
        search:
        for (i = start; i <= max; i++) {
            if (b[i] == s[0]) {
                // 找到了s中的第一个元素后，比较剩余的部分是否相等
                int k = 1;
                while (k < s.length) {
                    if (b[k + i] != s[k]) {
                        continue search;
                    }
                    k++;
                }
                return i;
            }
        }
        return -1;
    }

    private static byte[] subBytes(byte[] b, int from, int end) {
        byte[] result = new byte[end - from];
        System.arraycopy(b, from, result, 0, end - from);
        return result;
    }
}
