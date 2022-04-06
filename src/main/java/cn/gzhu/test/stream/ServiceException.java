package cn.gzhu.test.stream;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:52
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;
    private final String messageKey;
    private final String overrideMessage;
    private final String[] args;
    private final Map<String, Object> payload = new HashMap<>();
    private final String errorUrl;

    public static ServiceException of(int code, String messageKey) {
        return new ServiceException(code, messageKey, null, null);
    }

    public static ServiceException ofMessage(int code, String overrideMessage) {
        return new ServiceException(code, overrideMessage);
    }

    public static ServiceException ofFormat(int code, String... args) {
        return new ServiceException(code, null, null, args);
    }

    public static ServiceException ofFormatKey(int code, String messageKey, String... args) {
        return new ServiceException(code, messageKey, null, args);
    }

    public static ServiceException ofErrorUrl(int code, String url) {
        return new ServiceException(code, null, url, null);
    }

    /**
     * 一行搞定异常检查，条件过长时最好抽个临时变量
     */
    public static void throwIf(int errorCode, boolean errorCondition) {
        if (errorCondition) {
            throw new ServiceException(errorCode);
        }
    }

    public ServiceException(int code) {
        this(code, null, null, null);
    }

    private ServiceException(int code, String overrideMessage) {
        this.code = code;
        this.overrideMessage = overrideMessage;
        this.messageKey = null;
        this.errorUrl = null;
        this.args = null;
    }

    private ServiceException(int code, String messageKey, String errorUrl, String[] args) {
        this.code = code;
        this.messageKey = messageKey;
        this.errorUrl = errorUrl;
        this.args = args;
        this.overrideMessage = null;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return "code." + code;
    }

    public String getOverrideMessage() {
        return overrideMessage;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public String[] getArgs() {
        return args;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

}
