package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ExcelImporterInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Optional;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public class ExcelStreamImporterContext {
    private static final ThreadLocal<ExcelImporterInfo> EXCEL_CONTEXT = ThreadLocal.withInitial(ExcelImporterInfo::new);

    public static ExcelImporterInfo get() {
        return EXCEL_CONTEXT.get();
    }

    public static void remove() {
        EXCEL_CONTEXT.remove();
    }

    public static List<String> getErrors() {
        return Optional.ofNullable(EXCEL_CONTEXT).map(ThreadLocal::get).map(ExcelImporterInfo::getErrors)
                .orElse(Lists.newArrayList());
    }

    public static long getCurrentRowNum() {
        return Optional.ofNullable(EXCEL_CONTEXT).map(ThreadLocal::get).map(ExcelImporterInfo::getCurrentRowNum)
                .orElse(NumberUtils.LONG_ZERO);
    }

    /**
     * 获取文件名称带后缀(只适用于mutipart的情况,本地测试则获取不到)
     */
    public static String getFileName() {
        return Optional.ofNullable(EXCEL_CONTEXT).map(ThreadLocal::get).map(ExcelImporterInfo::getOriginalFilename)
                .orElse(StringUtils.EMPTY);
    }

    public static String getContextType() {
        return Optional.ofNullable(EXCEL_CONTEXT).map(ThreadLocal::get).map(ExcelImporterInfo::getContentType)
                .orElse(StringUtils.EMPTY);
    }

    public static long getFileSize() {
        return Optional.ofNullable(EXCEL_CONTEXT).map(ThreadLocal::get).map(ExcelImporterInfo::getSize)
                .orElse(NumberUtils.LONG_ZERO);
    }
}
