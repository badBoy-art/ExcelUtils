package cn.gzhu.test.stream;

import java.io.IOException;
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
public interface IExcelSheetBuilder {

    int BATCH_MAX_SIZE = 10000;

    /**
     * 写入表头
     */
    IExcelSheetBuilder appendHeader(Column... headersParam)
            throws IOException;

    /**
     * 写入一行内容
     */
    IExcelSheetBuilder appendContent(List<Object> excelRow) throws IOException;


    /**
     * 写入一行内容
     *
     * @param excelRows <= IExcelSheetBuilder.BATCH_MAX_SIZE
     */
    IExcelSheetBuilder batchAppendContent(List<List<Object>> excelRows)
            throws IOException;

    /**
     * 结束一页数据的写入
     */
    void end() throws IOException;
}
