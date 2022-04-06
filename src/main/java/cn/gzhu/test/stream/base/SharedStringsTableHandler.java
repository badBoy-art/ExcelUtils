package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ExcelMetaCache;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public class SharedStringsTableHandler extends DefaultHandler {
    private static final String T_TAG = "t";

    private static final String SI_TAG = "si";

    private static final String RPH_TAG = "rPh";

    private StringBuilder currentData;

    private StringBuilder currentElementData;

    private final ExcelMetaCache metaCache;

    private boolean ignoreTag = false;

    private boolean isTag = false;

    public SharedStringsTableHandler(ExcelMetaCache metaCache) {
        this.metaCache = metaCache;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        if (T_TAG.equals(name)) {
            currentElementData = null;
            isTag = true;
        } else if (SI_TAG.equals(name)) {
            currentData = null;
        } else if (RPH_TAG.equals(name)) {
            ignoreTag = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        if (T_TAG.equals(name)) {
            if (currentElementData != null) {
                if (currentData == null) {
                    currentData = new StringBuilder();
                }
                currentData.append(currentElementData);
            }
            isTag = false;
        } else if (SI_TAG.equals(name)) {
            if (currentData == null) {
                metaCache.add(null);
            } else {
                metaCache.add(currentData.toString());
            }
        } else if (RPH_TAG.equals(name)) {
            ignoreTag = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (!isTag || ignoreTag) {
            return;
        }
        if (currentElementData == null) {
            currentElementData = new StringBuilder();
        }
        currentElementData.append(ch, start, length);
    }
}
