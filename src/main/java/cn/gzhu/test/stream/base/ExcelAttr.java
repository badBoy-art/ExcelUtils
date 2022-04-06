package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ColumnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelAttr {
    /**
     * excel的列标题
     */
    String value() default "";

    /**
     * excel的列宽度，数值代表字符数,0表示,根据字符个数,自适应
     */
    int columnWidth() default 0;

    ColumnType columnType() default ColumnType.auto;

    /**
     * excel的列索引
     */
    int columnIndex() default -1;
}