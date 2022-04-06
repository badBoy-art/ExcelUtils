package cn.gzhu.test.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:47
 */
@Data
@EqualsAndHashCode
@ToString
public class Message <T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int status;                                     // 状态码

    private String message;                                 // 状态描述

    private T data;                                 // 实际返回的数据

    private boolean success;  // 成功标志

    private String traceId; // traceId

    private Message() {
    }

    private Message(T data, int code, String errMsg, boolean success) {
        this.data = data;
        this.status = code;
        this.message = errMsg;
        this.success = success;
        this.traceId = MDC.get("ktraceId");
    }

    public static <T> Message<T> instance() {
        return new Message<>();
    }

    public static <T> Message<T> instance(int code, String errorMsg, boolean success) {
        return new Message<>(null, code, errorMsg, success);
    }

    public static <T> Message<T> instance(T data, int code, String errorMsg, boolean success) {
        return new Message<>(data, code, errorMsg, success);
    }

    public static <T> Message<T> ok() {
        return instance(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), true);
    }

    public static <T> Message<T> ok(T data) {
        return instance(data, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), true);
    }

    public static <T> Message<T> error(T data, int errorCode, String errorMsg) {
        return instance(data, errorCode, errorMsg, false);
    }

    public static <T> Message<T> error(int code, String errMsg) {
        return instance(code, errMsg, false);
    }

    public static <T> Message<T> error(ResultCode resultCode) {
        return instance(resultCode.getCode(), resultCode.getMessage(), false);
    }
}