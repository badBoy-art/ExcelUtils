package cn.gzhu.test.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
@Slf4j
public class CsvStreamExportUtils {

    private final XmlWorkbook xmlWorkbook;

    private static final byte[] BYTES = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public CsvStreamExportUtils(Column[] columns, String fileName,
                                HttpServletResponse response) {

        response.setContentType("text/CSV;charset=utf-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=\""
                    +
                    new String(fileName.getBytes(), "ISO8859-1") + ".csv\"");
        } catch (UnsupportedEncodingException e) {
            log.error("CsvStreamExportUtil encode error.", e);
        }

        OutputStream os;
        try {
            os = response.getOutputStream();
        } catch (IOException e) {
            log.error("CsvStreamExportUtil getOutputStream error.{}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        xmlWorkbook = new XmlWorkbook(os);
        try {
            //header 处理
            String strColumns = Arrays.stream(columns).filter(x -> (x != null && x.getName() != null))
                    .map(Column::getName).collect(Collectors.joining(","));

            xmlWorkbook.write(new String(BYTES));
            xmlWorkbook.write(new String((strColumns + "\n").getBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.warn("CsvStreamExportUtil init header failed, error:{}", e.getMessage());
        }
    }

    public void append(List<Object> content) {

        if (CollectionUtils.isEmpty(content)) {
            return;
        }
        String res = content.stream().map(String::valueOf).collect(Collectors.joining(","));

        try {
            xmlWorkbook.write(new String((res + "\n").getBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.warn("CsvStreamExportUtil init append failed, error:{}", e.getMessage());
        }
    }

    public void close() {
        try {
            xmlWorkbook.close();
        } catch (IOException e) {
            log.warn("CsvStreamExportUtil init close failed, error:{}", e.getMessage());
        }
    }
}
