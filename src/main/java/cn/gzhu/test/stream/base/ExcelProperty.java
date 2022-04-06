package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ExcelStreamImporter;

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
 * used by {@link ExcelStreamImporter}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelProperty {

    /**
     * excel 列名
     */
    String value() default "";
}
