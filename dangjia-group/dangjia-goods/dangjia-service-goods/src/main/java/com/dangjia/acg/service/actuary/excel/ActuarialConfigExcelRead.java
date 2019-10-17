package com.dangjia.acg.service.actuary.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActuarialConfigExcelRead {
        public int totalRows; //sheet中总行数
        public static int totalCells; //每一行总单元格数
        /**
         * read the Excel .xlsx,.xls
         * @param file jsp中的上传文件
         * @return
         * @throws IOException
         */
       /* public List<Map<String,Object>> readExcel(String fileName,File file) throws IOException {

            if(file==null||ExcelUtil.EMPTY.equals(file.getOriginalFilename().trim())){
                return null;
            }else{
                String postfix = ExcelUtil.getPostfix(file.getOriginalFilename());
                if(!ExcelUtil.EMPTY.equals(postfix)){
                    if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
                        return readXls(file);
                    }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){
                        return readXlsx(file);
                    }else{
                        return null;
                    }
                }
            }
            return null;

        }*/
        public String checkExcelType(String fileName){
            String[] strArray = fileName.split("\\.");
            int suffixIndex = strArray.length -1;
            String postfix=strArray[suffixIndex];
            if(!ExcelUtil.EMPTY.equals(postfix)){
                if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){
                    return "xls";
                }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){
                    return "xlsx";
                }
            }
            return "excel文件类型不正确，请上传正确格式的excel文件，后缀为.xls、.xlsx结尾的文件";
        }

        private String getWorkerTypeId(String sheetName){
            String workerTypeId="";
            switch(sheetName){
                case "设计师" :
                    workerTypeId="1";
                    break; //可选
                case "精算师" :
                    workerTypeId="2";
                    break; //可选
                case "大管家" :
                    workerTypeId="3";
                    break; //可选
                case "拆除" :
                    workerTypeId="4";
                    break; //可选
                case "水电" :
                    workerTypeId="6";
                    break; //可选
                case "泥工" :
                    workerTypeId="8";
                    break; //可选
                case "木工" :
                    workerTypeId="9";
                    break; //可选
                case "油漆" :
                    workerTypeId="10";
                    break; //可选
                default : //可选
                    //语句
            }
            return workerTypeId;
        }
        /**
         * read the Excel 2010 .xlsx
         * @param file
         * @return
         * @throws IOException
         */
        @SuppressWarnings("deprecation")
        public List<Map<String,Object>> readXlsx(File file){
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            // IO流读取文件
            InputStream input = null;
            XSSFWorkbook  wb = null;
            try {
                input = new FileInputStream(file.getAbsolutePath());
                // 创建文档
                wb = new XSSFWorkbook(input);
                //读取sheet(页)
                for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){
                    XSSFSheet  xssfSheet = wb.getSheetAt(numSheet);
                    if(xssfSheet == null){
                        continue;
                    }
                    String sheetName = xssfSheet.getSheetName();
                    String workTypeId = getWorkerTypeId(sheetName);//判断工种类型
                    totalRows = xssfSheet.getLastRowNum();
                    //获取每一列对应的列表
                    List titleList=new ArrayList();
                    XSSFRow  xssfRow = xssfSheet.getRow(0);
                    if(xssfRow==null)
                        continue;
                    totalCells = xssfRow.getLastCellNum();
                    for(int c=0;c<=totalCells+1;c++) {
                        XSSFCell cell = xssfRow.getCell(c);
                        if(cell!=null){
                            titleList.add(ExcelUtil.getXValue(cell).trim());
                        }else{
                            titleList.add(c+"无");
                        }

                    }
                    System.out.println("titileList:"+titleList);
                    //读取Row,从第二行开始
                    for(int rowNum = 1;rowNum <= totalRows;rowNum++){
                        xssfRow = xssfSheet.getRow(rowNum);
                        if(xssfRow!=null){
                            totalCells = xssfRow.getLastCellNum();
                            Map resultMap=new HashMap<>();
                            //读取列，从第一列开始
                            for(int c=0;c<titleList.size();c++){
                                String titleName=(String)titleList.get(c);
                                XSSFCell  cell = xssfRow.getCell(c);
                                String value="";
                                if(cell==null){
                                    value="";
                                }else{
                                    value=ExcelUtil.getXValue(cell).trim();
                                }
                                if("商品编码".equals(titleName)){
                                    resultMap.put("productSn",value);
                                }else if("商品类型编码".equals(titleName)){
                                    resultMap.put("productCode",value);
                                }else if("数量".equals(titleName)){
                                    resultMap.put("productCount",value);
                                }

                            }
                            resultMap.put("workTypeId",workTypeId);
                            list.add(resultMap);
                        }
                    }
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }
        /**
         * read the Excel 2003-2007 .xls
         * @param file
         * @return
         * @throws IOException
         */
        public List<Map<String,Object>> readXls(File file){
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            // IO流读取文件
            InputStream input = null;
            HSSFWorkbook  wb = null;
            try {
                input = new FileInputStream(file.getAbsolutePath());
                // 创建文档
                wb = new HSSFWorkbook(input);
                //读取sheet(页)
                for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){
                    HSSFSheet  hssfSheet = wb.getSheetAt(numSheet);
                    if(hssfSheet == null){
                        continue;
                    }
                    String sheetName = hssfSheet.getSheetName();
                    String workTypeId = getWorkerTypeId(sheetName);//判断工种类型
                    totalRows = hssfSheet.getLastRowNum();
                    //获取每一列对应的列表
                    List titleList=new ArrayList();
                    HSSFRow  hssfRow = hssfSheet.getRow(0);
                    if(hssfRow==null)
                        continue;
                    totalCells = hssfRow.getLastCellNum();
                    for(int c=0;c<=totalCells+1;c++) {
                        HSSFCell  cell = hssfRow.getCell(c);
                        if(cell!=null){
                            titleList.add(ExcelUtil.getHValue(cell).trim());
                        }else{
                            titleList.add(c+"无");
                        }

                    }

                    //读取Row,从第二行开始
                    for(int rowNum = 1;rowNum <= totalRows;rowNum++){
                          hssfRow = hssfSheet.getRow(rowNum);
                        if(hssfRow!=null){
                            Map resultMap=new HashMap<>();
                            totalCells = hssfRow.getLastCellNum();
                            //读取列，从第一列开始
                            for(short c=0;c<titleList.size();c++){
                                HSSFCell  cell = hssfRow.getCell(c);
                                String titleName = (String)titleList.get(c);
                                String value="";
                                if(cell==null){
                                    value="";
                                }else{
                                    value=ExcelUtil.getHValue(cell).trim();
                                }
                                if("商品编码".equals(titleName)){
                                    resultMap.put("productSn",value);
                                }else if("商品类型编码".equals(titleName)){
                                    resultMap.put("productCode",value);
                                }else if("数量".equals(titleName)){
                                    resultMap.put("productCount",value);
                                }

                            }
                            resultMap.put("workTypeId",workTypeId);
                            list.add(resultMap);
                        }
                    }
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

}
