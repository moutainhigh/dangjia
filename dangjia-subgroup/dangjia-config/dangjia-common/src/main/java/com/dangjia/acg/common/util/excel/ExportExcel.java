package com.dangjia.acg.common.util.excel;

import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.common.util.ClazzUtil;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 */
public class ExportExcel {

    private static Logger log = LoggerFactory.getLogger(ExportExcel.class);

    /**
     * 工作薄对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 样式列表
     */
    private Map<String, CellStyle> styles;

    /**
     * 当前行号
     */
    private int rownum;

    List<Object[]> annotationList = Lists.newArrayList();


    /**
     * 添加一行
     * @return 行对象
     */
    public Row addRow(){
        return sheet.createRow(rownum++);
    }

    /**
     * 添加一个单元格
     * @param row 添加的行
     * @param column 添加列号
     * @param val 添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val){

        return this.addCell(row, column, val, 0, Class.class,"");
    }

    /**
     * 构造函数
     */
    public ExportExcel(){
        this.wb = new SXSSFWorkbook();
    }


    /**
     * 初始化函数
     * @param title 表格标题，传“空值”，表示无标题
     */
    private void initialize(String title,  Class<?> cls) {
        rownum=0;
        annotationList = Lists.newArrayList();
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs){
            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type()==0)){

                annotationList.add(new Object[]{ef, f});
            }
        }
        // Get annotation method
        Method[] ms = cls.getDeclaredMethods();
        for (Method m : ms){
            ExcelField ef = m.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type()==0 )){

                annotationList.add(new Object[]{ef, m});

            }
        }
        // Initialize
        List<String> headerList = Lists.newArrayList();
        for (Object[] os : annotationList){
            String t = ((ExcelField)os[0]).titile();
            headerList.add(t);
        }
        Collections.sort(annotationList, new Comparator<Object[]>() {
            public int compare(Object[] o1, Object[] o2) {
                return new Integer(((ExcelField)o1[0]).offset()).compareTo(
                        new Integer(((ExcelField)o2[0]).offset()));
            }
        });
        this.sheet = wb.createSheet(title);

        this.styles = createStyles(wb);
        if (headerList == null){
            throw new RuntimeException("headerList not null!");
        }
        Row headerRow = sheet.createRow(rownum++);
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String[] ss = StringUtils.split(headerList.get(i), "**", 2);
            if (ss.length==2){
                cell.setCellValue(ss[0]);
                Comment comment = this.sheet.createDrawingPatriarch().createCellComment(
                        new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                comment.setString(new XSSFRichTextString(ss[1]));
                cell.setCellComment(comment);
            }else{
                cell.setCellValue(headerList.get(i));
            }
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < headerList.size(); i++) {
            int colWidth = sheet.getColumnWidth(i)*2;
            sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
        }
        log.debug("Initialize success.");
    }

    /**
     * 创建表格样式
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();

        CellStyle style = wb.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setAlignment(CellStyle.ALIGN_CENTER);
        Font dataFont = wb.createFont();
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);


        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }
    /**
     * 添加一个单元格
     * @param row 添加的行
     * @param column 添加列号
     * @param val 添加值
     * @param align 对齐方式（1：靠左；2：居中；3：靠右）
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType,String dateformat){
        Cell cell = row.createCell(column);
        CellStyle style = styles.get("data");
        try {
            if (val == null){
                cell.setCellValue("");
            } else if (val instanceof String) {
                cell.setCellValue((String) val);
            } else if (val instanceof Integer) {
                cell.setCellValue((Integer) val);
            } else if (val instanceof Date) {
                if(StringUtils.isBlank(dateformat)) {
                    dateformat=DateUtil.FORMAT2;
                }
                Date date=(Date) val;
                String dateStr = DateUtil.convert(date,dateformat);
                cell.setCellValue(dateStr);
            } else if (val instanceof Double) {
                cell.setCellValue((Double) val);
            } else if (val instanceof Float) {
                cell.setCellValue((Float) val);
            }else {
                if (fieldType != Class.class){
                    cell.setCellValue((String)fieldType.getMethod("setValue", Object.class).invoke(null, val));
                }else{
                    cell.setCellValue((String)Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                            "fieldtype."+val.getClass().getSimpleName()+"Type")).getMethod("setValue", Object.class).invoke(null, val));
                }
            }
        } catch (Exception ex) {
            log.info("Set cell value ["+row.getRowNum()+","+column+"] error: " + ex.toString());
            cell.setCellValue(val.toString());
        }
        cell.setCellStyle(style);
        return cell;
    }


    /**
     *  添加数据（通过annotation.ExportField添加数据）
     * @param title sheet标题
     * @param cls 实体对象
     * @param list 数据列表
     * @param <T>
     * @return
     */
    public <T> ExportExcel setDataList(String title,  Class<?> cls,List<T> list){
        initialize(title,cls);
        for (T t : list){
            int colunm = 0;
            Row row = this.addRow();
            StringBuilder sb = new StringBuilder();
            for (Object[] os : annotationList){
                ExcelField ef = (ExcelField)os[0];
                Object val = null;
                // Get entity value
                try{
                        if (os[1] instanceof Field){
                            val = ClazzUtil.invokeGetter(t, ((Field)os[1]).getName());
                        }else if (os[1] instanceof Method){
                            val = ClazzUtil.invokeMethod(t, ((Method)os[1]).getName(), new Class[] {}, new Object[] {});
                        }


                }catch(Exception ex) {
                    // Failure to ignore
                    log.error(ex.toString());
                    val = "";
                }
                this.addCell(row, colunm++, val, 0, ef.fieldType(),ef.dateFormat());
                sb.append(val + ", ");
            }
            log.debug("写出成功: ["+row.getRowNum()+"] "+sb.toString());
        }
        return this;
    }

    /**
     * 输出到客户端
     * @param fileName 输出文件名
     */
    public ExportExcel write(HttpServletResponse response, String fileName) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(fileName, "UTF-8"));
        write(response.getOutputStream());
        return this;
    }
    /**
     * 输出数据流
     * @param os 输出数据流
     */
    public ExportExcel write(OutputStream os) throws IOException{
        wb.write(os);
        return this;
    }

    /**
     * 输出到文件
     * @param filePath 输出文件名
     */
    public ExportExcel writeFile(String filePath) throws  IOException{
        FileOutputStream os = new FileOutputStream(filePath);
        this.write(os);
        return this;
    }
    /**
     * 输出到文件并下载
     * @param fileFullPath 输出指定文件（物理地址）
     * @param fileName 输出文件名
     */
    public ExportExcel writeFileDownload(HttpServletResponse response,String fileFullPath, String fileName) throws  IOException{
        FileOutputStream os = new FileOutputStream(fileFullPath);
        this.write(os);
        download(response,fileFullPath,fileName);
        return this;
    }
    /**
     * 下载文件
     * @param fileFullPath 文件全路径
     * @param response
     * @throws IOException
     */
    public static void download(HttpServletResponse response,String fileFullPath, String fileName) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        Path path = Paths.get(fileFullPath);
        String contenType = Files.probeContentType(path);
        if(CommonUtil.isEmpty(contenType)){
            contenType="application/octet-stream";
        }
        File f = new File(fileFullPath);
        long fileLength = f.length();
        response.setContentType(contenType+";charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename="
                + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
        response.setHeader("Content-Length", String.valueOf(fileLength));
        try{
            bis = new BufferedInputStream(new FileInputStream(fileFullPath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();
        }catch(IOException e){
            throw e;
        }finally{
            if(bis!=null){
                bis.close();
            }
            if(bos!=null){
                bos.close();
            }
        }


    }
    public static void main(String[] args,HttpServletResponse response) throws Exception{
        ExportExcel exportExcel=new ExportExcel();//创建表格实例
        List<BaseEntity> baseEntities1=new ArrayList<>();//数据结果集
        List<BaseEntity> baseEntities2=new ArrayList<>();//数据结果集
        exportExcel.setDataList("精算", BaseEntity.class,baseEntities1);
        exportExcel.setDataList("概括", BaseEntity.class,baseEntities2);
        exportExcel.write(response,"文件名称.xls");//创建文件并输出
        exportExcel.writeFile("E;//文件名称.xls");//指定本地文件输出
    }
}
