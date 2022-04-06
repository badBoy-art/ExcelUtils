package cn.gzhu.test.stream;

import cn.gzhu.test.stream.base.BaseExcelImporter;
import cn.gzhu.test.stream.base.ExcelStreamImporterContext;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public abstract class ExcelStreamImporter<T> extends BaseExcelImporter<T> {

    /**
     * Excel每行内容检查专用!
     *
     * @param expression 条件表达式
     * @param errorRowName excel列名
     * @param errorMessage 对用户有友好的提示信息
     */
    @Override
    public void checkExcelCell(boolean expression, String errorRowName, Object errorMessage) {
        super.checkExcelCell(expression, errorRowName, errorMessage);
    }

    /**
     * Excel每行内容检查专用!(检查失败后,会跳过excel当前行,程序继续执行)
     *
     * @param expression 条件表达式
     * @param errorMessage 对用户有友好的提示信息
     */
    public void checkExcelCell(boolean expression, Object errorMessage) {
        super.checkExcelCell(expression, StringUtils.EMPTY, errorMessage);
    }

    /**
     * 上传文件预检查 配合{@link ExcelStreamImporterContext#get()}使用! (检查失败后,程序终止)
     *
     * @param expression 条件表达式
     * @param errorMessage 对用户有友好的提示信息
     */
    @Override
    public void checkExcelFile(boolean expression, Object errorMessage) {
        super.checkExcelFile(expression, errorMessage);
    }

    /**
     * 作为接口返回直接展示类型,适用于整体导入时间较短的场景(技术边界:180s).
     */
    @Override
    public Message<ExcelImporterInfo> execute() {
        return super.execute();
    }

    /**
     * 将错误文件打包成excel返回,适用于整体导入时间较长的场景(技术边界:180s).
     */
    @Override
    public void executeWithDownload() {
        super.executeWithDownload();
    }
}
