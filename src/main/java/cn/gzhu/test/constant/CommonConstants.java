package cn.gzhu.test.constant;

import com.google.common.base.Splitter;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:54
 */
public class CommonConstants {
    public static final int ZERO_INT = 0;
    public static final int ONE_INT = 1;
    public static final String POINT = ".";
    public static final Splitter SPLITTER_POINT = Splitter.on(POINT).omitEmptyStrings().trimResults();

    public static final String EXCEL_XLSX = "xlsx";

    public static final String ZERO_STRING = "0";

    public static final String ONE_STRING = "1";

}
