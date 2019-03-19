package com.dangjia.acg.common.util.excel;


import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.util.ClazzUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
public class ImportExcel {

    private Logger logger = LoggerFactory.getLogger(ImportExcel.class);
    /**
     * 工作薄对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 标题行号
     */
    private int headerNum;

    public ImportExcel(MultipartFile file, int headerNum) throws IOException {
        this(file.getOriginalFilename(), file.getInputStream(), headerNum, 0);
    }
    public ImportExcel(MultipartFile file, int headerNum, int sheetIndex) throws IOException {
        this(file.getOriginalFilename(), file.getInputStream(), headerNum, sheetIndex);
    }

    public ImportExcel(String fileName, InputStream is, int headerNum, int sheetIndex)
            throws InvalidFormatException, IOException {
        if (StringUtils.isBlank(fileName)){
            throw new BaseException(ServerCode.SERVER_EXCEPTION_NULLPOINTER,"文件为空");
        }else if(fileName.toLowerCase().endsWith("xls")){
            this.wb = new HSSFWorkbook(is);
        }else if(fileName.toLowerCase().endsWith("xlsx")){
            this.wb = new XSSFWorkbook(is);
        }else{
            throw new BaseException(ServerCode.ILLEGAL_ARGUMENT_ERROR);
        }
        if (this.wb.getNumberOfSheets()<sheetIndex){
            throw new RuntimeException("文档中没有工作表!");
        }
        this.sheet = this.wb.getSheetAt(sheetIndex);
        this.headerNum = headerNum;

    }



    /**
     * 获取单元格值
     * @param row 获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    public Object getCellValue(Row row, int column){
        Object val = "";
        try{
            Cell cell = row.getCell(column);
            if (cell != null){
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                    val = String.valueOf(new DecimalFormat("#").format(cell.getNumericCellValue()));
                }else if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                    val = cell.getStringCellValue();
                }else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA){
                    val = cell.getCellFormula();
                }else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
                    val = cell.getBooleanCellValue();
                }else if (cell.getCellType() == Cell.CELL_TYPE_ERROR){
                    val = cell.getErrorCellValue();
                }
            }
        }catch (Exception e) {
            return val;
        }
        return val;
    }

    /**
     * 获取导入数据列表
     */
    public <T> List<T> getDataList(Class<T> targetClass, int columnNum) throws InstantiationException, IllegalAccessException{
        List<Object[]> annotationList = Lists.newArrayList();
        // Get annotation field
        Field[] fs = targetClass.getDeclaredFields();
        for (Field f : fs){
            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type()==0 || ef.type()==1)){
                annotationList.add(new Object[]{ef, f});

            }
        }
        Method[] ms = targetClass.getDeclaredMethods();

        for (Method m : ms){
            ExcelField ef = m.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type()==0 || ef.type()==1)){
                annotationList.add(new Object[]{ef, m});
            }
        }
        Collections.sort(annotationList, new Comparator<Object[]>() {
            public int compare(Object[] o1, Object[] o2) {
                return new Integer(((ExcelField)o1[0]).offset()).compareTo(
                        new Integer(((ExcelField)o2[0]).offset()));
            };
        });

        List<T> dataList = Lists.newArrayList();
        for (int i = this.getDataRowNum(); i <= this.getLastDataRowNum(); i++) {
            T e = (T)targetClass.newInstance();
            Row row = this.getRow(i);
            if (isRowEmpty(row)) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            int countError = 0 ;
            for (Object[] os : annotationList){
                ExcelField ef = (ExcelField)os[0];
                Object val = this.getCellValue(row, ef.offset()-1);
                if (val != null){

                    Class<?> valType = Class.class;
                    if (os[1] instanceof Field){
                        valType = ((Field)os[1]).getType();
                    }else if (os[1] instanceof Method){
                        Method method = ((Method)os[1]);
                        if ("get".equals(method.getName().substring(0, 3))){
                            valType = method.getReturnType();
                        }else if("set".equals(method.getName().substring(0, 3))){
                            valType = ((Method)os[1]).getParameterTypes()[0];
                        }
                    }
                    //log.debug("Import value type: ["+i+","+column+"] " + valType);
                    try {
                        if (valType == String.class){
                            val = val.toString();
                        }else if (valType == Integer.class){
                            val = Double.valueOf(val.toString().replaceAll("\"", "")).intValue();
                        }else if (valType == Long.class){
                            val = Double.valueOf(val.toString()).longValue();
                        }else if (valType == Double.class){
                            val = Double.valueOf(val.toString());
                        }else if (valType == Float.class){
                            val = Float.valueOf(val.toString());
                        }else if (valType == Date.class){
                            val = org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Double)val);
                        }else{
                            if (ef.fieldType() != Class.class){
                                val = ef.fieldType().getMethod("getValue", String.class).invoke(null, val.toString());
                            }else{
                                val = Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                                        "fieldtype."+valType.getSimpleName()+"Type")).getMethod("getValue", String.class).invoke(null, val.toString());
                            }
                        }
                    } catch (Exception ex) {
                        logger.info("Get cell value ["+i+","+ef.offset()+"] error: " + ex.toString());
                        val = null;
                    }
                    // set entity value
                    if (os[1] instanceof Field){
                        if(StringUtils.isEmpty(val.toString())){
                            countError++;
                        }
                        ClazzUtil.invokeSetter(e, ((Field)os[1]).getName(), val);
                    }else if (os[1] instanceof Method){
                        String mthodName = ((Method)os[1]).getName();
                        if ("get".equals(mthodName.substring(0, 3))){
                            mthodName = "set"+StringUtils.substringAfter(mthodName, "get");
                        }
                        ClazzUtil.invokeMethod(e, mthodName, new Class[] {valType}, new Object[] {val});
                    }
                }
                sb.append(val+", ");
            }
            if(annotationList.size() == countError){
                continue;
            }
            dataList.add(e);

        }
        return dataList;
    }

  /**
   * 判断行是否为空
   *
   * @param row
   * @return
   */
    public static boolean isRowEmpty(Row row) {

        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false;
        }
        return true;
    }

    /**
     * 获取行对象
     * @param rownum
     * @return
     */
    public Row getRow(int rownum){
        return this.sheet.getRow(rownum);
    }

    /**
     * 获取数据行号
     * @return
     */
    public int getDataRowNum(){
        return headerNum+1;
    }

    /**
     * 获取最后一个数据行号
     * @return
     */
    public int getLastDataRowNum(){
        return this.sheet.getLastRowNum()+headerNum;
    }

    /**
     * 获取最后一个列号
     * @return
     */
    public int getLastCellNum(){
        return this.getRow(headerNum).getLastCellNum();
    }

    public Workbook getWb() {
        return wb;
    }

    public void setWb(Workbook wb) {
        this.wb = wb;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public int getHeaderNum() {
        return headerNum;
    }

    public void setHeaderNum(int headerNum) {
        this.headerNum = headerNum;
    }

}
