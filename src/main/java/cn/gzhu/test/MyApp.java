package cn.gzhu.test;

import cn.gzhu.test.pojo.ExamineeExcelModel;
import cn.gzhu.test.pojo.ExamineeGradeExcelModel;
import cn.gzhu.test.utils.ExcelUtils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class MyApp {

    private static String desktopString = "/Users/zhaoxuedui/Desktop/";

    public static void main(String[] args) throws Exception {
        //testExportList();
        testImport();
    }


    public static void testExportList() throws Exception {
        List<ExamineeGradeExcelModel> list = new ArrayList<ExamineeGradeExcelModel>();
        for (int i = 0; i < 5; ++i) {
            ExamineeGradeExcelModel e = new ExamineeGradeExcelModel();
            e.setAccount("hello" + i);
            e.setNo(i + 1);
            e.setSubmitTime(new Date());
            list.add(e);
        }
        ExcelUtils.export(list);
        System.out.println("已经导出。。。");
    }


    public static void testEncodeAndDecode() throws Exception {
        String originalInput = "我是小明";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

        System.out.println(encodedString);
        System.out.println("===============");
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        System.out.println(decodedString);
    }

    public static void testImport() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(new File("/Users/zhaoxuedui/Desktop/导入考生信息模版.xlsx"));
        //导入
        List<ExamineeExcelModel> examineeExcelModels = ExcelUtils.covertExcel2Model(fileInputStream, ExamineeExcelModel.class);
        for (ExamineeExcelModel a : examineeExcelModels) {
            System.out.println(a);
            System.out.println("======================");
        }
    }

    //XSSF07  HSSF03
    public static void testa() throws Exception {
        //第一步创建workbook
        XSSFWorkbook wb = new XSSFWorkbook();
        //第二步创建sheet
        XSSFSheet sheet = wb.createSheet("测试");
        //第三步创建行row:添加表头0行
        XSSFRow row = sheet.createRow(0);
        XSSFCellStyle style = wb.createCellStyle();
        //style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  //居中
        //第四步创建单元格
        XSSFCell cell = row.createCell(0); //第一个单元格
        cell.setCellValue("姓名");
        cell.setCellStyle(style);
        cell = row.createCell(1);         //第二个单元格
        cell.setCellValue("年龄");
        cell.setCellStyle(style);
        //第五步插入数据
        for (int i = 0; i < 5; i++) {
            //创建行
            row = sheet.createRow(i + 1);
            //创建单元格并且添加数据
            row.createCell(0).setCellValue("aa" + i);
            row.createCell(1).setCellValue(i);

        }
        //第六步将生成excel文件保存到指定路径下
        try {
            FileOutputStream fout = new FileOutputStream(desktopString + "qqwww.xlsx");
            wb.write(fout);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Excel文件生成成功...");
    }

    public static void testb() throws Exception {
        Workbook wb = WorkbookFactory.create(new FileInputStream(desktopString + "src.xlsx"));
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        row.createCell(0).setCellValue("abc");
        row.createCell(1).setCellValue(123);
        wb.write(new FileOutputStream(desktopString + "target.xlsx"));
    }

    public static void testType() {
        List<ExamineeExcelModel> list = new ArrayList<>();
        System.out.println();
    }


}



