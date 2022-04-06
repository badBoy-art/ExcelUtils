package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ServiceException;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:52
 */
public enum ExcelErrorCodeEnum {
    SUCCESS(1, "OK"),

    PARAM_INVALID(2, "参数错误"),

    CALL_ERROR(3, "调用第三方服务错误"),

    SERVER_ERROR(4, "服务器错误"),

    REQUEST_DEGRADED(5, "请求降级"),

    SHOP_INPUT_DATA_ERROR(6, "输入的数据错误"),

    TIME_OUT(7, "请求超时"),

    EXCEL_ERROR(8, "Excel解析错误"),

    FILE_LOAD_ERROR(9, "文件加载错误");

    private int code;
    private String msg;

    ExcelErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public ServiceException toException() {
        return ServiceException.ofMessage(code, msg);
    }

    public ServiceException toException(String overrideMsg) {
        return ServiceException.ofMessage(code, overrideMsg);
    }
}
