package cn.gzhu.test.stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.function.Function;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public class Column {
    private static final int DEFAULT_WIDTH = 60;

    private String name;
    private int width;
    private String fieldName;
    private Function converter;


    private Column(String name, int width) {
        this.name = name;
        this.width = width;
    }

    private Column(String name, int width, String fieldName, Function converter) {
        this.name = name;
        this.width = width;
        this.fieldName = fieldName;
        this.converter = converter;
    }

    private Column(String name) {
        this.name = name;
    }

    public static Column create(String name, int width) {
        return new Column(name, width);
    }

    public static Column create(String name, int width, String fieldName, Function converter) {
        return new Column(name, width, fieldName, converter);
    }

    public static Column create(String name) {
        return new Column(name, DEFAULT_WIDTH);
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Function getConverter() {
        return converter;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
