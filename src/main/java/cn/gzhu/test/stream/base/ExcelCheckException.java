package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ExcelImporterInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public class ExcelCheckException extends RuntimeException {

    /**
     * Excel列名称
     */
    private String errorColumnName;

    /**
     * 错误的行号
     */
    private String errorRowNum;

    /**
     * 需要提示用户的错误信息
     */
    private String errorInfo;

    public ExcelCheckException(String errorColumnName, String errorInfo) {
        super(errorInfo);
        this.errorColumnName = errorColumnName;
        this.errorInfo = errorInfo;
    }

    public ExcelCheckException(String errorColumnName, String errorRowNum, String errorInfo) {
        super(errorInfo);
        this.errorColumnName = errorColumnName;
        this.errorRowNum = errorRowNum;
        this.errorInfo = errorInfo;
    }

    public static void buildErrorInfoToContent(ExcelCheckException e, long rowColumn) {
        String errorInfo;
        if (StringUtils.isNotBlank(e.getErrorColumnName())) {
            errorInfo = String.format("Excel 行号:%s 列名:%s 错误信息:%s", rowColumn,
                    e.getErrorColumnName(), e.getErrorInfo());
        } else {
            errorInfo = String.format("Excel 行号:%s 错误信息:%s", rowColumn, e.getErrorInfo());
        }
        ExcelImporterInfo excelContext = ExcelStreamImporterContext.get();
        excelContext.getErrors().add(errorInfo);
    }

    public static void buildErrorInfoToContent(ExcelCheckException e, String rowColumn) {
        if (StringUtils.isBlank(rowColumn)) {
            rowColumn = "未知";
        }
        String errorInfo;
        if (StringUtils.isNotBlank(e.getErrorColumnName())) {
            errorInfo = String.format("Excel 行号:%s 列名:%s 错误信息:%s", rowColumn,
                    e.getErrorColumnName(), e.getErrorInfo());
        } else {
            errorInfo = String.format("Excel 行号:%s 错误信息:%s", rowColumn, e.getErrorInfo());
        }
        ExcelImporterInfo excelContext = ExcelStreamImporterContext.get();
        excelContext.getErrors().add(errorInfo);
    }

    static void buildErrorInfoToContent(ExcelCheckException e) {
        String errorInfo;
        if (StringUtils.isNotBlank(e.getErrorColumnName())) {
            errorInfo = String.format("Excel 行号:%s 错误信息:%s", e.getErrorColumnName(), e.getErrorInfo());
        } else {
            errorInfo = String.format("Excel 错误信息:%s", e.getErrorInfo());
        }
        ExcelImporterInfo excelContext = ExcelStreamImporterContext.get();
        excelContext.getErrors().add(errorInfo);
    }

    public ExcelCheckException(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getErrorColumnName() {
        return errorColumnName;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public String getErrorRowNum() {
        return errorRowNum;
    }
}
