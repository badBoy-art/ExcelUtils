package cn.gzhu.test.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public class ExportUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportUtils.class);

    public static void packExcelExportResponse(HttpServletResponse response, String filename) {
        response.setContentType("application/msexcel;charset=utf-8");
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + new String(filename.getBytes(), "ISO8859-1") + ".xls\"");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("", e);
        }
    }
}
