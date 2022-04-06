package cn.gzhu.test.stream;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
public enum ExcelVersion {
    E2003("2003"),
    E2007("2007"),
    E2010("2010");

    private String version;

    ExcelVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
