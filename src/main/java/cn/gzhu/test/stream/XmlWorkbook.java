package cn.gzhu.test.stream;

import com.google.common.base.Charsets;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;


/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:54
 * 流式xml excel生成器
 */
public class XmlWorkbook {
    private Writer writer;

    public XmlWorkbook(OutputStream os) {
        writer = new BufferedWriter(new OutputStreamWriter(os, Charsets.UTF_8));
    }

    public void write(String content) throws IOException {
        writer.write(content, 0, content.length());
        writer.flush();
    }

    public void batchWrite(List<String> contents) throws IOException {
        if (CollectionUtils.isEmpty(contents)) {
            return;
        }
        StringBuilder b = new StringBuilder();

        for (String content : contents) {
            b.append(content);

        }
        writer.write(b.toString(), 0, b.toString().length());
        writer.flush();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();

    }
}
