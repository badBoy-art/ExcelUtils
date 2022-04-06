package cn.gzhu.test.stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public class ExcelStreamExportUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelStreamExportUtils.class);

    private static final String DEFAULT_SHEET_TITLE = "Sheet";

    private IExcelBuilder excelBuilder;
    private IExcelSheetBuilder sheetBuilder;
    private boolean fileClosed = Boolean.FALSE;

    public ExcelStreamExportUtils(ExcelVersion version, String sheetName, Column[] columns, OutputStream os) {
        init(version, sheetName, columns, os);
    }

    public ExcelStreamExportUtils(ExcelVersion version, Column[] columns, OutputStream os) {
        init(version, null, columns, os);

    }

    public ExcelStreamExportUtils(ExcelVersion version, String sheetName, Column[] columns, String filename) {
        OutputStream os;
        try {
            os = new BufferedOutputStream(new FileOutputStream(new File(filename)));
        } catch (FileNotFoundException e) {
            LOGGER.error("file not found!", e);
            throw new RuntimeException(e.getMessage(), e);
        }

        init(version, sheetName, columns, os);
    }

    public ExcelStreamExportUtils(ExcelVersion version, String sheetName, Column[] columns,
            HttpServletResponse response,
            String filename) {
        ExportUtils.packExcelExportResponse(response, filename);
        OutputStream os;
        try {
            os = response.getOutputStream();
            init(version, sheetName, columns, os);
        } catch (IOException e) {
            LOGGER.warn("ExcelStreamExportUtils getOutputStream failed, error:{}", e.getMessage());
        }
    }

    private void init(ExcelVersion version, String sheetName, Column[] columns, OutputStream os) {
        if (StringUtils.isBlank(sheetName)) {
            sheetName = DEFAULT_SHEET_TITLE;
        }

        try {
            excelBuilder = ExcelFactory.createExcelBuilder(version, os);
            sheetBuilder = excelBuilder.newSheet(sheetName);
            sheetBuilder.appendHeader(columns);
        } catch (IOException e) {
            LOGGER.warn("ExcelStreamExportUtils init failed, error.", e);
        }
    }

    /**
     * 单行写入,List<Object> contents 为一行数据
     */
    public void append(List<Object> contents) {
        if (fileClosed) {
            throw new RuntimeException("文件流已关闭, 无法写入数据!");
        }
        try {
            sheetBuilder.appendContent(contents);
        } catch (IOException e) {
            LOGGER.warn("ExcelStreamExportUtils appendContent failed, error", e);
        }
    }

    /**
     * 批量写入, 内嵌的 List<Object> 为一行数据
     * <p>
     * packageContents must <= IExcelSheetBuilder.BATCH_MAX_SIZE
     */
    public void batchAppend(List<List<Object>> packageContents) {
        if (fileClosed) {
            throw new RuntimeException("文件流已关闭, 无法写入数据!");
        }
        try {
            sheetBuilder.batchAppendContent(packageContents);
        } catch (IOException e) {
            LOGGER.warn("ExcelStreamExportUtils batchAppendContent failed, error", e);
        }
    }

    public void close() {
        try {
            sheetBuilder.end();
            excelBuilder.flushAndClose();
        } catch (IOException e) {
            LOGGER.warn("ExcelStreamExportUtils close failed, error", e);
        }
        fileClosed = Boolean.TRUE;
    }

    public static <T> List<Object> buildLine(T item, Column[] columns) {
        if (item == null || columns == null || columns.length == 0) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<>(columns.length);

        List<String> fieldNames = Arrays.stream(columns).map(Column::getFieldName).collect(Collectors.toList());
        List<Field> fields = getFields(fieldNames, item.getClass());

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            field.setAccessible(true);
            try {
                Object value = field.get(item);
                if (value == null) {
                    result.add(StringUtils.EMPTY);
                } else if (columns[i].getConverter() != null) {
                    String textValue = columns[i].getConverter().apply(value).toString();
                    result.add(textValue);
                } else {
                    result.add(value.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException("buildLine error field:" + field.getName(), e);
            }
        }
        return result;
    }

    private static List<Field> getFields(List<String> fieldNames, Class<?> clazz) {
        return fieldNames.stream()
                .map(fieldName -> getField(fieldName, clazz))
                .collect(Collectors.toList());
    }

    private static Field getField(String fieldName, Class<?> clazz) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClazz = clazz.getSuperclass();
            while (superClazz != Object.class) {
                try {
                    return superClazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ex) {
                    // 如果父类不是object，表明其继承的有其他类。 逐级获取所有父类的字段
                    superClazz = superClazz.getSuperclass();
                }
            }
            throw new RuntimeException("getField Error: " + fieldName);
        }
    }
}
