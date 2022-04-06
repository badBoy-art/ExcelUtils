package cn.gzhu.test.stream.base;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:54
 */
public class BeanUtils {

    public static Object getProperty(Object obj, String propertyName) throws
            IntrospectionException, InvocationTargetException, IllegalAccessException {
        if (null == obj) {
            return null;
        }
        PropertyDescriptor descriptor = null;
        descriptor = new PropertyDescriptor(propertyName, obj.getClass());
        Method readMethod = descriptor.getReadMethod();
        return readMethod.invoke(obj);
    }

    public static void setProperty(Object obj, String propertyName, Object value) throws IntrospectionException,
            InvocationTargetException, IllegalAccessException {
        if (null == obj) {
            return;
        }
        PropertyDescriptor descriptor = new PropertyDescriptor(propertyName, obj.getClass());
        Method writeMethod = descriptor.getWriteMethod();
        writeMethod.invoke(obj, value);
    }

}
