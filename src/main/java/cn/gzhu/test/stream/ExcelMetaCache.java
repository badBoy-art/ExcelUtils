package cn.gzhu.test.stream;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public interface ExcelMetaCache {

    void init();

    void add(String value);

    String get(Integer index);

    void putFinished();

    void clear();
}
