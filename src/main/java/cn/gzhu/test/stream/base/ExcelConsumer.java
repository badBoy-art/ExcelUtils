package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ExcelStreamBatchImporter;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public interface ExcelConsumer<T> {

    /**
     * 回调业务接口,如果希望使用批量回调,请使用{@link ExcelStreamBatchImporter}
     */
    void appendRow(T rowObj) throws ExcelCheckException;

    /**
     * Excel解析结束,将回调此方法
     */
    void complete();
}
