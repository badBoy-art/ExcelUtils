package cn.gzhu.test.stream.base;

import cn.gzhu.test.constant.CommonConstants;
import cn.gzhu.test.stream.ExcelMetaCache;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:54
 */
public class LocalExcelMetaCache implements ExcelMetaCache {
    private Map<Integer, String> cache = new HashMap<>();
    private int index = CommonConstants.ZERO_INT;

    @Override
    public void init() {
    }

    @Override
    public void add(String value) {
        cache.put(index++, value);
    }

    @Override
    public String get(Integer key) {
        if (key == null || key < CommonConstants.ZERO_INT) {
            return null;
        }
        return cache.get(key);
    }

    @Override
    public void putFinished() {
    }


    @Override
    public void clear() {
        if (cache != null) {
            cache.clear();
            cache = null;
        }
    }
}
