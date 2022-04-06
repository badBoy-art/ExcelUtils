package cn.gzhu.test.stream;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
@Data
public class ExcelContent<T> {

    private MultipartFile multipartFile;

    private T fieldClass;

    private String localFilePath;

    private HttpServletResponse response;

    /**
     * http上传使用
     *
     * @param multipartFile http upload file
     * @param fieldClass row obj
     */
    public ExcelContent(T fieldClass, MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
        this.fieldClass = fieldClass;
    }

    /**
     * 本地文件测试使用
     *
     * @param fieldClass row obj
     * @param localFilePath 本地文件路径
     */
    public ExcelContent(T fieldClass, String localFilePath) {
        this.fieldClass = fieldClass;
        this.localFilePath = localFilePath;
    }

    /**
     * 超大文件的时候使用,边界为,导入执行时间超过2分钟!
     * 效果为点击上传后,浏览器端直接对错误信息进行下载!
     * 这样就不担心NG超时了.
     */
    public ExcelContent(T fieldClass, MultipartFile multipartFile, HttpServletResponse response) {
        this.multipartFile = multipartFile;
        this.fieldClass = fieldClass;
        this.response = response;
    }
}
