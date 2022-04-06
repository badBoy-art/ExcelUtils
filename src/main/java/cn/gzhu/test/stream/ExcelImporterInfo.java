package cn.gzhu.test.stream;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
public class ExcelImporterInfo implements Serializable {

    /**
     * 每行校验不通过, 错误信息容器
     */
    private List<String> errors = Lists.newArrayList();

    /**
     * 文件名称(带后缀)
     */
    private String originalFilename;

    /**
     * 类型
     */
    private String contentType;

    /**
     * 当前执行到的行号
     */
    private long currentRowNum;

    /**
     * 文件大小,单位:字节
     */
    private long size;
}
