package cn.gzhu.test.stream.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 * excel每行数据映射对象,需要自己定义VO,这里只是示例!
 */
@Data
public class ExcelImportExampleVO implements Serializable {
    private static final long serialVersionUID = 6260363583099321314L;

    @ExcelProperty("编码")
    private String code;

    @ExcelProperty("公司")
    private String company;

    @ExcelProperty("电话")
    private String phone;

    @ExcelProperty("兴趣")
    private String interest;

    @ExcelProperty("爱好")
    private String hobby;
}
