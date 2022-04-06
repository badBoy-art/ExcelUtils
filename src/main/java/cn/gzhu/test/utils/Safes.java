package cn.gzhu.test.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:58
 */
public class Safes {

    private static final Logger logger = LoggerFactory.getLogger(
            Safes.class);

    public static <K, V> Map<K, V> of(Map<K, V> source) {
        return Optional.ofNullable(source).orElse(Maps.newHashMapWithExpectedSize(0));
    }

    public static <T> Iterator<T> of(Iterator<T> source) {
        return Optional.ofNullable(source).orElse(Collections.emptyIterator());
    }

    public static <T> Collection<T> of(Collection<T> source) {
        return Optional.ofNullable(source).orElse(Lists.newArrayListWithCapacity(0));
    }

    public static <T> Iterable<T> of(Iterable<T> source) {
        return Optional.ofNullable(source).orElse(Lists.newArrayListWithCapacity(0));
    }

    public static <T> List<T> of(List<T> source) {
        return Optional.ofNullable(source).orElse(Lists.newArrayListWithCapacity(0));
    }

    public static <T> Set<T> of(Set<T> source) {
        return Optional.ofNullable(source).orElse(Sets.newHashSetWithExpectedSize(0));
    }

    public static Byte of(Byte source) {
        return Optional.ofNullable(source).orElse((byte) 0);
    }

    public static Integer of(Integer source) {
        return Optional.ofNullable(source).orElse(0);
    }

    public static Long of(Long source) {
        return Optional.ofNullable(source).orElse(0L);
    }

    public static Float of(Float source) {
        return Optional.ofNullable(source).orElse(0F);
    }

    public static Double of(Double source) {
        return Optional.ofNullable(source).orElse(0D);
    }

    public static BigDecimal of(BigDecimal source) {
        return Optional.ofNullable(source).orElse(BigDecimal.ZERO);
    }

    public static String of(String source) {
        return Optional.ofNullable(source).orElse("");
    }

    public static String of(String source, String defaultStr) {
        return Optional.ofNullable(source).orElse(defaultStr);
    }
    public static <T> Optional<T> of(T source) {
        return Optional.ofNullable(source);
    }

    public static <T> T first(Collection<T> source) {
        if (org.springframework.util.CollectionUtils.isEmpty(source)) {
            return null;
        }
        T t = null;
        Iterator<T> iterator = source.iterator();
        if (iterator.hasNext()) {
            t = iterator.next();
        }
        return t;
    }

    public static <T> Optional<T> firstOpt(Collection<T> source) {
        if (org.springframework.util.CollectionUtils.isEmpty(source)) {
            return Optional.empty();
        }
        T t = null;
        Iterator<T> iterator = source.iterator();
        if (iterator.hasNext()) {
            t = iterator.next();
        }
        return Optional.ofNullable(t);
    }

    public static BigDecimal toBigDecimal(String source, BigDecimal defaultValue) {
        Preconditions.checkNotNull(defaultValue);
        try {
            return new BigDecimal(trimToEmpty(source));
        } catch (Throwable t) {
            logger.warn("未能识别的boolean类型, source:{}", source, t);
            return defaultValue;
        }
    }

    public static int toInt(String source, int defaultValue) {
        if (!StringUtils.hasText(source)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(trimToEmpty(source));
        } catch (Throwable t) {
            logger.warn("未能识别的类型 {}", source);
            return defaultValue;
        }
    }

    public static long toLong(String source, long defaultValue) {
        if (!StringUtils.hasText(source)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(trimToEmpty(source));
        } catch (Throwable t) {
            logger.warn("未能识别的长类型 {}", source);
            return defaultValue;
        }
    }

    public static boolean toBoolean(String source, boolean defaultValue) {
        if (!StringUtils.hasText(source)) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(trimToEmpty(source));
        } catch (Throwable t) {
            logger.warn("未能识别的boolean类型, source:{}", source, t);
            return defaultValue;
        }
    }

    public static void run(Runnable runnable, Consumer<Throwable> error) {
        try {
            runnable.run();
        } catch (Throwable t) {
            error.accept(t);
        }
    }

    static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }
}
