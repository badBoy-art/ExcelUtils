package cn.gzhu.test.stream.base;

import cn.gzhu.test.constant.CommonConstants;
import cn.gzhu.test.stream.ExcelMetaCache;
import cn.gzhu.test.stream.ServiceException;
import cn.gzhu.test.utils.Safes;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.gzhu.test.stream.base.ExcelCheckException.buildErrorInfoToContent;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:52
 */
@Slf4j
public class ExcelSheetHandler<T> extends DefaultHandler {

    private static final String TAG_DIMENSION = "dimension";
    private static final String TAG_REF = "ref";
    private static final String TAG_ROW = "row";
    private static final String TAG_R = "r";
    private static final String TAG_C = "c";
    private static final String TAG_V = "v";
    private static final String TAG_T = "t";
    private static final String TAG_S = "s";

    private boolean isIndex;

    private final BaseExcelImporter<T> baseExcelImporter;

    private final T fieldObj;

    private final ExcelMetaCache metaCache;

    private String tempCellIndex;

    private Map<String, String> rowPropertyNameMap = null;

    private String rowId = CommonConstants.ONE_STRING;

    private String currentRowId;

    private String currentColumn;

    private String dimension;

    private final Map<String, String> valueMap = Maps.newHashMap();

    private String currentColumnNumber;

    private Stopwatch contextStopwatch;

    public ExcelSheetHandler(T fieldObj, ExcelMetaCache metaCache, BaseExcelImporter<T> baseExcelImporter) {
        this.fieldObj = fieldObj;
        this.baseExcelImporter = baseExcelImporter;
        this.metaCache = metaCache;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        // 清空内容缓存
        tempCellIndex = StringUtils.EMPTY;

        if (TAG_DIMENSION.equals(name)) {
            dimension = attributes.getValue(TAG_REF);
        }
        if (TAG_ROW.equals(name)) {
            currentRowId = attributes.getValue(TAG_R);
        }
        if (TAG_C.equals(name)) {
            currentColumnNumber = findLetter(attributes.getValue(TAG_R));
            currentColumn = attributes.getValue(TAG_R);
        }
        if (TAG_V.equals(name)) {
            return;
        }
        String cellType = attributes.getValue(TAG_T);
        if (cellType != null && cellType.equals(TAG_S)) {
            isIndex = true;
        } else {
            isIndex = false;
        }
    }

    private static String findLetter(String var) {
        if (StringUtils.isBlank(var)) {
            return CommonConstants.ZERO_STRING;
        }
        try {
            StringBuilder builder = new StringBuilder();
            for (char c : var.toCharArray()) {
                if (Character.isLetter(c)) {
                    builder.append(c);
                }
            }
            return builder.toString();
        } catch (Exception e) {
            log.warn("findLetter failed! error.", e);
            return CommonConstants.ZERO_STRING;
        }
    }

    private long findDigit(String var) {
        if (StringUtils.isBlank(var)) {
            return CommonConstants.ZERO_INT;
        }
        try {
            StringBuilder builder = new StringBuilder();
            for (char c : var.toCharArray()) {
                if (Character.isDigit(c)) {
                    builder.append(c);
                }
            }
            return Long.parseLong(builder.toString());
        } catch (Exception e) {
            log.warn("findDigit failed", e);
            return CommonConstants.ZERO_INT;
        }
    }

    private T convert() {
        Class<?> clazz = fieldObj.getClass();
        Preconditions.checkArgument(Objects.nonNull(fieldObj), "fieldClass can not be null");
        try {
            Field[] fields = clazz.getDeclaredFields();
            boolean allNoAnnotation = true;
            for (Field field : fields) {
                field.setAccessible(true);
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                if (null == excelProperty) {
                    continue;
                }

                allNoAnnotation = false;
                String propertyName;
                if (StringUtils.isBlank(excelProperty.value())) {
                    throw new IllegalArgumentException("@ExcelProperty注解Value必传!");
                } else {
                    propertyName = excelProperty.value();
                }

                String columnNumber = rowPropertyNameMap.get(propertyName);
                String stringVal = Safes.of(valueMap.get(columnNumber));

                BeanUtils.setProperty(fieldObj, field.getName(), stringVal);
            }
            Preconditions.checkArgument(!allNoAnnotation, "请使用@ExcelProperty绑定excel列名");
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            log.error("excelSheetHandler convert failed! error.", e);
            throw ServiceException.ofMessage(ExcelErrorCodeEnum.EXCEL_ERROR.getCode(), Throwables.getStackTraceAsString(e));
        }
        return fieldObj;
    }


    /**
     * 返回单元格的值
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        String s = String.valueOf(ch, start, length);
        tempCellIndex += s;
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        if (currentRowId != null && !currentRowId.equals(rowId)) {

            if (rowId.equals(CommonConstants.ONE_STRING)) {
                rowId = currentRowId;
                Map<String, String> columnValueNumMap = Maps.newLinkedHashMap();
                for (Entry<String, String> entry : valueMap.entrySet()) {
                    columnValueNumMap.put(entry.getValue(), entry.getKey());
                }
                if (MapUtils.isEmpty(rowPropertyNameMap)) {
                    rowPropertyNameMap = columnValueNumMap;
                }
                log.info("column name parse completed, rowNum:{}, values: {}",
                        CommonConstants.ONE_INT, rowPropertyNameMap.toString());
                contextStopwatch = Stopwatch.createStarted();
                valueMap.clear();
            } else {
                boolean isLog = true;
                rowId = currentRowId;
                Stopwatch started = Stopwatch.createStarted();
                T convert = convert();
                long number = findDigit(currentColumn) - CommonConstants.ONE_INT;
                try {
                    baseExcelImporter.appendRow(convert);
                } catch (ExcelCheckException e) {
                    buildErrorInfoToContent(e, number);
                    log.warn("checkExcelCell failed! rowNum:{} columnName:{} time:{} msg:{}",
                            number, e.getErrorColumnName(), started.elapsed(TimeUnit.SECONDS), e.getErrorInfo(), e);
                    isLog = false;
                }
                valueMap.clear();
                if (isLog) {
                    log.info("fileName:{} current execution rowNum:{}, dimension:{}, time:{}ms",
                            ExcelStreamImporterContext.getFileName(), number, dimension,
                            started.elapsed(TimeUnit.MILLISECONDS));
                }
                ExcelStreamImporterContext.get().setCurrentRowNum(number);
            }
        }

        String currentCellValue;
        if (isIndex) {
            currentCellValue = metaCache.get(Integer.parseInt(tempCellIndex));
        } else {
            currentCellValue = tempCellIndex;
        }

        // 内容读取后输出
        if (name.equals(TAG_V)) {
            valueMap.putIfAbsent(currentColumnNumber, currentCellValue);
        }
    }

    public void endDocument() {
        long rowNum = findDigit(currentColumn);
        if (MapUtils.isNotEmpty(valueMap)) {
            Stopwatch started = Stopwatch.createStarted();
            T convert = convert();
            boolean isLog = true;
            try {
                baseExcelImporter.appendRow(convert);
            } catch (ExcelCheckException e) {
                buildErrorInfoToContent(e, rowNum);
                log.warn("checkExcelCell failed! rowNum:{} columnName:{} time:{} msg:{}",
                        rowNum, e.getErrorColumnName(), started.elapsed(TimeUnit.SECONDS), e.getErrorInfo());
                isLog = false;
            }
            if (isLog) {
                log.info("fileName:{} current execution rowNum:{}, dimension:{}, time:{}ms",
                        ExcelStreamImporterContext.getFileName(), rowNum, dimension,
                        started.elapsed(TimeUnit.MILLISECONDS));
            }
            valueMap.clear();
        }

        baseExcelImporter.complete();
        rowPropertyNameMap.clear();
        rowPropertyNameMap = null;
        log.info("execution completed! rowNum:{}, dimension:{},time:{}ms",
                rowNum, dimension, contextStopwatch.elapsed(TimeUnit.MILLISECONDS));
        contextStopwatch = null;
        ExcelStreamImporterContext.get().setCurrentRowNum(rowNum);
    }
}
