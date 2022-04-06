package cn.gzhu.test.stream;

import cn.gzhu.test.stream.base.ExcelCheckException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;

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
public abstract class ExcelStreamBatchImporter<T> extends ExcelStreamImporter<T> {

    private static final int DEFAULT_MAX_SIZE = 50;

    private int maxSize = DEFAULT_MAX_SIZE;

    private List<T> rows;

    public abstract void batchAppendRow(List<T> rows);

    public abstract void batchComplete();

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void appendRow(T rowObj) throws ExcelCheckException {
        if (rows == null) {
            rows = Lists.newArrayList();
        }
        if (rowObj == null) {
            return;
        }
        T instance = getInstance(rowObj);
        BeanUtils.copyProperties(rowObj, instance);
        rows.add(instance);
        if (rows.size() > maxSize) {
            try {
                batchAppendRow(rows);
            } catch (ExcelCheckException e) {
                ExcelCheckException.buildErrorInfoToContent(e, e.getErrorRowNum());
                log.warn("checkExcelCell failed! rowNum:{} columnName:{} msg:{}",
                        e.getErrorRowNum(), e.getErrorColumnName(), e.getErrorInfo());
            } finally {
                rows.clear();
            }
        }
    }

    @Override
    public void complete() {
        if (rows != null) {
            try {
                batchAppendRow(rows);
            } catch (ExcelCheckException e) {
                ExcelCheckException.buildErrorInfoToContent(e, e.getErrorRowNum());
                log.warn("checkExcelCell failed! rowNum:{} columnName:{} msg:{}",
                        e.getErrorRowNum(), e.getErrorColumnName(), e.getErrorInfo());
            } finally {
                rows.clear();
                rows = null;
                batchComplete();
            }
        }
    }

    @Override
    public void executeWithDownload() {
        super.executeWithDownload();
    }

    @Override
    public Message<ExcelImporterInfo> execute() {
        return super.execute();
    }
}
