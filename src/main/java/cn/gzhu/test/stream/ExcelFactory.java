package cn.gzhu.test.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public class ExcelFactory {
    /**
     * 默认返回03版的excel
     */
    public static IExcelBuilder createExcelBuilder(ExcelVersion version, OutputStream os) throws IOException {
        IExcelBuilder excelBuilder = null;

        // 目前只支持03版的流式导出, 因为07版的
        if (version == ExcelVersion.E2007) {
            excelBuilder = XlsBuilder.newExcel(os);
        } else {
            excelBuilder = XlsBuilder.newExcel(os);
        }

        return excelBuilder;
    }
}
