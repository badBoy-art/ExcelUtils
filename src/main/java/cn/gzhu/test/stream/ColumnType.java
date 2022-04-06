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
public enum ColumnType {
    auto, // 根据数据类型自适应
    json_string, // 将bean json 结果存入excel 表
    number, // 数值类型,当长度超过excel规定的长度时会转变成 科学记数法,该类型可以是 short int long double float
    integer, // 保存之后不保留小数,也不会以科学记数法显示
    date, //
    string, //
    rich_string, // unsupported now
    bool
}
