package cn.gzhu.test.utils;


import cn.gzhu.test.anno.ExcleColumn;
import cn.gzhu.test.anno.ExcleColumnVerify;
import cn.gzhu.test.anno.ExcleSheet;
import cn.gzhu.test.constant.ExcelType;
import cn.gzhu.test.exception.NotExcelException;
import cn.gzhu.test.exception.NullFileException;
import cn.gzhu.test.exception.RowNumBeyondException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelUtils {


    public static <T> List<T> covertExcel2Model(FileInputStream file, Class<T> clazz) throws Exception {

        ExcleSheet excleSheet = clazz.getAnnotation(ExcleSheet.class);
        List result = new ArrayList<T>();
        Workbook wb = WorkbookFactory.create(file);
        Sheet sheet = wb.getSheetAt(0);

        if ((sheet.getLastRowNum() + 1 - excleSheet.startIndex()) > excleSheet.maxRowNum()) {
            throw new RowNumBeyondException("导入的行数超过最大值:" + excleSheet.maxRowNum());
        }

        //初始化标题名和下标
        HashMap<Integer, String> indexWithTitle = new HashMap<>();
        Row titleRow = sheet.getRow(excleSheet.titleIndex());
        int titleCellNum = titleRow.getPhysicalNumberOfCells();
        for (int i = 0; i < titleCellNum; ++i) {
            indexWithTitle.put(i, titleRow.getCell(i).getStringCellValue());
        }
        //唯一性值，用index+字段的string值进行存储
        List<String> onlyContainer = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < excleSheet.startIndex() || isBlankRow(excleSheet.ignoreOnlyHaveNoRow(), row)) {
                continue;
            }
            T t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                ExcleColumn excleColumn = field.getAnnotation(ExcleColumn.class);
                if (null == excleColumn) {
                    continue;
                }
                int index = excleColumn.index();
                Cell cell = row.getCell(index);
                //excel导入时数字采用科学计数法，需要还原
                DecimalFormat format = new DecimalFormat("0");
                switch (excleColumn.javaType()) {
                    case STRING:
                        String stringVal;
                        if (CellType.NUMERIC == cell.getCellTypeEnum()) {
                            stringVal = format.format(cell.getNumericCellValue());
                        } else {
                            stringVal = cell.getStringCellValue();
                        }
                        MyBeanUtils.setProperty(t, field.getName(), stringVal);
                        break;
                    case DOUBLE:
                        Double doubleVal = Double.parseDouble(format.format(cell.getNumericCellValue()));
                        MyBeanUtils.setProperty(t, field.getName(), doubleVal);
                        break;
                    case DATE:
                        Date dateVal = cell.getDateCellValue();
                        MyBeanUtils.setProperty(t, field.getName(), dateVal);
                        break;
                }
                //校验
                ExcleColumnVerify excleColumnVerify = field.getAnnotation(ExcleColumnVerify.class);

                if (null != excleColumnVerify) {
                    Object propVal = MyBeanUtils.getProperty(t, field.getName());
                    ExcelColumnVerifyUtils.verity(propVal, row.getRowNum() + 1, indexWithTitle.get(index), excleColumnVerify, onlyContainer, index);
                }
            }
            result.add(t);
        }
        return result;
    }


    public static Boolean isBlankRow(int noIndex, Row row) {
        int cellNum = row.getPhysicalNumberOfCells();
        for (int i = 0; i < cellNum; ++i) {
            Cell c = row.getCell(i);
            if (i != noIndex && c != null && c.getCellTypeEnum() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * 导出excel
     */
    public static <T> void export(List<T> modelList) throws Exception {
        if (null == modelList || modelList.size() < 1) {
            return;
        }
        Class<T> clazz = (Class<T>) modelList.get(0).getClass();
        //获取模版
        ExcleSheet excleSheet = clazz.getAnnotation(ExcleSheet.class);
        Workbook wb = getWorkbook(excleSheet.excellType());
        Sheet sheet = wb.createSheet(excleSheet.sheetName());
        //获取单元格样式
        setHeader(modelList.get(0), sheet.createRow(excleSheet.startIndex()));

        Integer counter = excleSheet.startIndex();

        for (T t : modelList) {
            Row row = sheet.createRow(++counter);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                ExcleColumn excleColumn = field.getAnnotation(ExcleColumn.class);
                if (null == excleColumn) {
                    continue;
                }
                int index = excleColumn.index();
                Cell cell = row.createCell(index);
                setCellValue(t, field, excleColumn, cell);
            }
        }
        wb.write(new FileOutputStream("/Users/zhaoxuedui/Desktop/" + excleSheet.exportFileName() + excleSheet.excellType().getSuffix()));
    }

    private static Workbook getWorkbook(ExcelType excelType) {
        if (excelType == ExcelType.EXCEL_2007) {
            return new XSSFWorkbook();
        }

        return new HSSFWorkbook();
    }


    private static <T> void setHeader(T data, Row row) {
        if (data == null) {
            return;
        }
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ExcleColumn excleColumn = field.getAnnotation(ExcleColumn.class);
            if (null == excleColumn) {
                continue;
            }
            int index = excleColumn.index();
            Cell cell = row.createCell(index);
            cell.setCellValue(excleColumn.name());
        }

    }

    private static <E> void setCellValue(E data, Field field, ExcleColumn excleColumn, Cell cell) throws Exception {
        if (field == null) {
            cell.setCellValue("");
            return;
        }
        Object o = MyBeanUtils.getProperty(data, field.getName());
        switch (excleColumn.javaType()) {
            case DOUBLE:
                cell.setCellValue(new Double((Double) o));
                break;
            case BOOLEAN:
                cell.setCellValue((Boolean) o);
                break;
            case CALENDAR:
                cell.setCellValue((Calendar) o);
                break;
            case DATE:
                cell.setCellValue(DateTimeFormatter.ofPattern(excleColumn.dateFormat()).format(((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
                break;
            default:
                cell.setCellValue(o.toString());
                break;
        }

    }

    public static void checkExcleFile(File file) {
        if (null == file) {
            throw new NullFileException();
        }
        //判断是否为excel文件
        String filename = "";
        if (!filename.endsWith(".xls") && !filename.endsWith(".xlsx")) {
            throw new NotExcelException();
        }
    }

}
