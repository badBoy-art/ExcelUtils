package cn.gzhu.test.stream;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:49
 */
public enum ResultCode {

    SUCCESS(200, "成功"),
    /**
     * 500-599是服务端错误
     */
    SERVICE_BUSY(500, "内部系统繁忙"), //
    SERVICE_RATE_EXCEEDED(520, "请求超限"),

    /**
     * 600-699 外部系统异常
     */
    RELY_SERVICE_BUSY(600, "外部系统繁忙"),
    ;

    private final int code;
    private final String message;


    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
